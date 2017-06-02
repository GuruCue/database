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

import com.gurucue.recommendations.data.DataLink;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TimeZone;

public final class Transaction implements AutoCloseable {
    private static final Logger log = LogManager.getLogger(Transaction.class);
    private static final ThreadLocal<Transaction> transactions = new ThreadLocal<>();
    private static final TimeZone utcTimeZone = TimeZone.getTimeZone("UTC");

    public static Transaction get() {
        return transactions.get();
    }

    // Note: no synchronization is needed because of ThreadLocal
    public static Transaction newTransaction(final DataLink link) {
        final Transaction existing = transactions.get();
        if (existing != null) {
            if (existing.link == link) {
                log.error("Requested a new transaction, but the calling thread already owns a transaction with the same DataLink as was given -- returning the existing transaction instead of creating the new");
                return existing;
            }
            log.error("Requested a new transaction, but the calling thread already owns a transaction -- rolling it back, and then creating a new transaction");
            try {
                existing.rollback();
            }
            catch (RuntimeException e) {
                log.error("Error during rollback of the existing thread, ignoring: " + e.toString(), e);
            }
        }
        final Transaction transaction = new Transaction(link);
        transactions.set(transaction);
        return transaction;
    }

    private final LinkedList<TransactionCloseJob> closeJobs = new LinkedList<>();
    private final DataLink link;
    private final Thread ownerThread;
    private final Map<TransactionLock, Integer> lockedObjects = new HashMap<>();

    private Transaction(final DataLink link) {
        this.link = link;
        this.ownerThread = Thread.currentThread();
    }

    private void cleanup() {
        closeJobs.clear();
        transactions.remove();
    }

    public void onTransactionClose(final TransactionCloseJob job) {
        if (Thread.currentThread().getId() != ownerThread.getId()) throw new IllegalStateException("Calling thread is not the same as the transaction owning thread: " + Thread.currentThread().getId() + " != " + ownerThread.getId());
        closeJobs.add(job);
    }

    public void commit() {
        if (Thread.currentThread().getId() != ownerThread.getId()) throw new IllegalStateException("Calling thread is not the same as the transaction owning thread: " + Thread.currentThread().getId() + " != " + ownerThread.getId());
        link.commit();
        final Iterator<TransactionCloseJob> iterator = closeJobs.iterator();
        while (iterator.hasNext()) {
            final TransactionCloseJob job = iterator.next();
            try {
                job.commit();
            }
            catch (Throwable e) {
                log.error("A commit job failed, ignoring: " + e.toString(), e);
            }
        }
        cleanup();
    }

    public void rollback() {
        if (Thread.currentThread().getId() != ownerThread.getId()) throw new IllegalStateException("Calling thread is not the same as the transaction owning thread: " + Thread.currentThread().getId() + " != " + ownerThread.getId());
        final Iterator<TransactionCloseJob> iterator = closeJobs.iterator();
        while (iterator.hasNext()) {
            final TransactionCloseJob job = iterator.next();
            try {
                job.rollback();
            }
            catch (Throwable e) {
                log.error("[" + ownerThread.getId() + "] A rollback job failed, ignoring: " + e.toString(), e);
            }
        }
        try {
            link.rollback();
        }
        catch (Throwable e) {
            log.error("[" + ownerThread.getId() + "] Link rollback failed, ignoring: " + e.toString(), e);
        }
        cleanup();
    }

    public DataLink getLink() {
        return link;
    }

    public Thread getThread() {
        return ownerThread;
    }

    public long getId() {
        return ownerThread.getId();
    }

    /**
     * Memorizes the given lock being held, and returns the number of times
     * this lock has been memorized (locked) by the transaction up to and
     * including now.
     *
     * @param lockable the lock for which to account
     * @return the number of times the given lockable has been memorized (locked)
     */
    public Integer addLock(final TransactionLock lockable) {
        // by definition this can execute only in the owning thread, so no synchronization needed
        final Integer count = lockedObjects.get(lockable);
        final Integer newCount;
        if (count == null) newCount = 1;
        else newCount = count + 1;
        lockedObjects.put(lockable, newCount);
        log.debug("[" + Thread.currentThread().getId() + "]   added lock @" + Integer.toHexString(lockable.getLock().hashCode()) + ", count=" + newCount);
        return newCount;
    }

    /**
     * Decreases the lock count for the given lockable, and returns the new
     * lock count for the given lock.
     *
     * @param lockable the lock for which to account
     * @return the new lock count; 0 means it is not locked anymore
     */
    public Integer removeLock(final TransactionLock lockable) {
        // by definition this can execute only in the owning thread, so no synchronization needed
        final Integer count = lockedObjects.get(lockable);
        if (count == null) throw new IllegalStateException("The given lock is not being held");
        final Integer newCount = count - 1;
        if (newCount.intValue() == 0) lockedObjects.remove(lockable);
        else lockedObjects.put(lockable, newCount);
        log.debug("[" + Thread.currentThread().getId() + "] removed lock @" + Integer.toHexString(lockable.getLock().hashCode()) + ", count=" + newCount);
        return newCount;
    }

    /**
     * Returns whether the given transaction is waiting for a lock among
     * the list of this transaction's locked objects.
     *
     * @param transaction the transaction against which to check for a deadlock
     * @return whether the given transaction would cause a deadlock with this transaction
     */
    public boolean hasQueuedSomewhere(final Transaction transaction) {
        // by definition this can execute only in the owning thread, so no synchronization needed
        if (transaction == null) return false;
        for (final TransactionLock lockable : lockedObjects.keySet()) {
            if (lockable.isQueued(transaction)) {
                log.warn("[" + Thread.currentThread().getId() + "] found transaction " + transaction.getId() + " is waiting on lock @" + Integer.toHexString(lockable.getLock().hashCode()) + " which is owned by me");
                return true;
            }
        }
        return false;
    }

    @Override
    public void close() {
        rollback();
    }

    private Calendar utcCalendar = null;

    /**
     * Returns a {@link Calendar} instance, suitably configured for interfacing with the database.
     * @return a {@link Calendar} instance, suitably configured for working with the database
     */
    public Calendar getUtcCalendar() {
        if (utcCalendar == null) {
            utcCalendar = Calendar.getInstance(utcTimeZone);
            utcCalendar.setMinimalDaysInFirstWeek(4);
            utcCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        }
        return utcCalendar;
    }
}
