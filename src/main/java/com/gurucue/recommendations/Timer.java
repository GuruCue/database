/*
 * This file is part of Guru Cue Search & Recommendation Engine.
 * Copyright (C) 2017 Guru Cue Ltd.
 *
 * Guru Cue Search & Recommendation Engine is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * Guru Cue Search & Recommendation Engine is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Guru Cue Search & Recommendation Engine. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package com.gurucue.recommendations;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * Timer to schedule jobs to run at a specific time. The jobs are objects
 * implementing the {@link com.gurucue.recommendations.TimerListener}
 * interface.
 * </p><p>
 * The timer is initialized automatically when first used. At application
 * stop it must be stopped explicitely with {@link #stop()} to stop
 * the timing thread and invoker threads.
 * </p>
 *
 * @see com.gurucue.recommendations.TimerListener
 */
public final class Timer {
    private static final Logger log = LogManager.getLogger(Timer.class);
    private static final CurrentTimeRetriever currentTimeRetriever;
    static {
        String offsetStr = null;
        long offset = 0L;
        try {
            offsetStr = System.getenv("REC_TIME_START");
            if (offsetStr != null) {
                try {
                    offset = Long.parseLong(offsetStr, 10);
                }
                catch (NumberFormatException e1) {
                    final long now = System.currentTimeMillis();
                    final SimpleDateFormat timestampFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        offset = now - timestampFormat1.parse(offsetStr).getTime();
                    }
                    catch (ParseException e2) {
                        final SimpleDateFormat timestampFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            offset = now - timestampFormat2.parse(offsetStr).getTime();
                        }
                        catch (ParseException e3) {
                            final SimpleDateFormat timestampFormat3 = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                offset = now - timestampFormat3.parse(offsetStr).getTime();
                            }
                            catch (ParseException e4) {}
                        }
                    }
                }
            }
        }
        catch (RuntimeException e) {
            log.error("Could not convert to long the value in the environment variable REC_TIME_START: " + e.toString(), e);
        }
        if (offset > 0L) {
            currentTimeRetriever = new SimulatedTimeRetriever(offset);
            log.info("Using simulated time with offset " + offset + " ms (start at " + offsetStr + ")");
        }
        else {
            currentTimeRetriever = new RealTimeRetriever();
            log.info("Using real time");
        }
    }
    public static final Timer INSTANCE = new Timer();

    private final AtomicBoolean running;
    private final Lock lock = new ReentrantLock();
    private final Condition newTimer = lock.newCondition();
    private final Thread thread;
    private final SortedMap<Long, ExpirationEntry> timers = new TreeMap<Long, ExpirationEntry>();
    private final ExecutorService invokerPool = Executors.newFixedThreadPool(10);

    private Long currentTimer;

    public static long currentTimeMillis() {
        return currentTimeRetriever.currentTimeMillis();
    }

    private Timer() {
        log.debug("Starting");
        running = new AtomicBoolean(true);
        thread = new Thread(new Worker(this), "Timer worker");
        thread.start();
    }

    public void schedule(final Long timeMillis, final TimerListener listener) {
        lock.lock();
        try {
            ExpirationEntry entry = timers.get(timeMillis);
            if (null == entry) {
//                log.debug("schedule: creating new timer entry for time " + timeMillis + " and adding a listener");
                entry = new ExpirationEntry(timeMillis);
                timers.put(timeMillis, entry);
            }
/*            else {
                log.debug("schedule: adding a listener to the timer entry for time " + timeMillis);
            }*/
            entry.listeners.add(listener);
            if ((null == currentTimer) || (timeMillis < currentTimer)) {
//                log.debug((null == currentTimer ? "schedule: active timer is not set" : "schedule: active timer is greater from the new timer: " + currentTimer) + ", signalling the worker to reschedule");
                newTimer.signal();
            }
        }
        finally {
            lock.unlock();
        }
    }

    public void unschedule(final Long timeMillis, final TimerListener listener) {
        lock.lock();
        try {
            ExpirationEntry entry = timers.get(timeMillis);
            if (null == entry) {
//                log.debug("unschedule: removing a listener for time " + timeMillis + ", but no timer entry found for this time, so doing nothing");
                return;
            }
            entry.listeners.remove(listener);
//            log.debug("unschedule: removing a listener for time " + timeMillis + ", remaining listeners for this time: " + entry.listeners.size());
        }
        finally {
            lock.unlock();
        }
    }

    public void stop() {
        log.debug("Shutting down");
        running.set(false);
        lock.lock();
        try {
            newTimer.signal();
        }
        finally {
            lock.unlock();
        }
        try {
            thread.join();
        }
        catch (InterruptedException e) {
            log.warn("Interrupted while waiting for the timer thread to finish: " + e.toString(), e);
        }
        finally {
            invokerPool.shutdown();
        }
    }

    private void runThread() {
        processing:
        while (running.get()) {
            ExpirationEntry entry = null;
            lock.lock();
            try {
                // if there are no timers yet, wait for one to appear
                while (timers.size() == 0) {
                    currentTimer = null;
//                    log.debug("No timers scheduled, waiting for one");
                    try {
                        newTimer.await();
                        if (!running.get()) break processing;
                    } catch (InterruptedException e) {
                        if (!running.get()) break processing;
                        log.error("Interrupted while awaiting a timer: " + e.toString(), e);
                    }
                }

                // wait for the closest timer to expire
                currentTimer = timers.firstKey();
                long now = currentTimeMillis();
                long delta = currentTimer - now;
                while (delta > 0) {
//                    log.debug("Waiting for the timer at time " + currentTimer + " to expire, that's in " + delta + " ms (now=" + now + ")");
                    try {
                        newTimer.await(delta, TimeUnit.MILLISECONDS);
                        if (!running.get()) break processing;
                    } catch (InterruptedException e) {
                        if (!running.get()) break processing;
                        log.error("Interrupted while waiting for a timer to expire, or a new timer to show up: " + e.toString(), e);
                    }
                    currentTimer = timers.firstKey();
                    now = currentTimeMillis();
                    delta = currentTimer - now;
                }
                entry = timers.remove(currentTimer);
            }
            finally {
                lock.unlock();
            }

//            log.debug("Scheduling " + entry.listeners.size() + " listeners for executing after timer " + currentTimer + " expired");
            // invoke callbacks on everyone that registered
            for (TimerListener listener : entry.listeners) {
                invokerPool.execute(new Invoker(listener, currentTimer));
            }
            entry.listeners.clear();
        }
    }

    /**
     * Proxy class, needed just to bootstrap a new thread running in the parent class.
     * The intention is to remove the public visibility of the run method from the parent class.
     */
    private static final class Worker implements Runnable {
        private final Timer timer;

        Worker(final Timer timer) {
            this.timer = timer;
        }

        @Override
        public void run() {
            timer.runThread();
        }
    }

    private static final class ExpirationEntry implements Comparable<ExpirationEntry> {
        final long expirationMillis;
        final Set<TimerListener> listeners = new HashSet<TimerListener>();

        ExpirationEntry(final long expirationMillis) {
            this.expirationMillis = expirationMillis;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof ExpirationEntry)) return false;

            final ExpirationEntry that = (ExpirationEntry) o;

            if (expirationMillis != that.expirationMillis) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return (int) (expirationMillis ^ (expirationMillis >>> 32));
        }

        @Override
        public int compareTo(final ExpirationEntry that) {
            if (this.expirationMillis < that.expirationMillis) return -1;
            if (this.expirationMillis > that.expirationMillis) return 1;
            return 0;
        }
    }

    private static final class Invoker implements Runnable {
        private final static Logger log = LogManager.getLogger(Timer.class.getCanonicalName() + ".Invoker");
        private final TimerListener listener;
        private final long expiryTime;

        Invoker(final TimerListener listener, final long expiryTime) {
            this.listener = listener;
            this.expiryTime = expiryTime;
        }

        @Override
        public void run() {
//            log.debug("Invoking a listener");
            try {
                listener.onTimerExpired(expiryTime);
            }
            catch (Throwable e) {
                log.error("Error while invoking a callback after a timer expired: " + e.toString(),  e);
            }
        }
    }

    private static interface CurrentTimeRetriever {
        long currentTimeMillis();
    }

    private static final class RealTimeRetriever implements CurrentTimeRetriever {
        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    private static final class SimulatedTimeRetriever implements CurrentTimeRetriever {
        private final long offset;

        SimulatedTimeRetriever(final long offset) {
            this.offset = offset;
        }

        @Override
        public long currentTimeMillis() {
            return System.currentTimeMillis() - offset;
        }
    }
}
