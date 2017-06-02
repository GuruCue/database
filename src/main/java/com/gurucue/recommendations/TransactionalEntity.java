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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TransactionalEntity<V extends Id<?>> implements TransactionLock {
    private static final Logger log = LogManager.getLogger(TransactionalEntity.class);
    protected V committed;
    public final ReentrantLock writeLock = new ReentrantLock(true); // it's public for debugging purposes
    protected V uncommitted;
    protected Transaction owner;

    protected TransactionalEntity(final V committedValue) {
        this.committed = committedValue;
        this.uncommitted = null;
        this.owner = null;
    }

    protected TransactionalEntity(final V uncommittedValue, final Transaction owner) {
        this.committed = null;
        this.uncommitted = uncommittedValue;
        this.owner = owner;
        final long threadId = Thread.currentThread().getId();
        final StringBuilder logBuilder = new StringBuilder(4096);
        logBuilder
                .append("[")
                .append(threadId)
                .append("]   lock() @")
                .append(Integer.toHexString(writeLock.hashCode()))
                .append(" on ")
                .append(getClass().getSimpleName())
                .append("  in constructor - no entity");
//        Utils.formatStackTraceRange(logBuilder, Thread.currentThread().getStackTrace(), 2, 6);
        log.debug(logBuilder.toString());
        this.writeLock.lock();
        owner.addLock(this);
    }

    @Override
    public final void lock(final Transaction transaction) {
        final StringBuilder logBuilder = new StringBuilder(4096);
        final long threadId = Thread.currentThread().getId();
        logBuilder
                .append("[")
                .append(threadId)
                .append("]   lock() @")
                .append(Integer.toHexString(writeLock.hashCode()))
                .append(" on ")
                .append(getClass().getSimpleName())
                .append(" of entity class ");
        if (committed != null) logBuilder.append(committed.getClass().getCanonicalName()).append(" with ID ").append(committed.getId());
        else if (uncommitted != null) logBuilder.append(uncommitted.getClass().getCanonicalName()).append(" with ID ").append(uncommitted.getId());
        else logBuilder.append("(null)");
//        Utils.formatStackTraceRange(logBuilder, Thread.currentThread().getStackTrace(), 2, 6);
        log.debug(logBuilder.toString());

        if (writeLock.tryLock()) { // this does not honour fairness!
            owner = transaction;
            transaction.addLock(this);
            return;
        }

        if (transaction.hasQueuedSomewhere(owner)) {
            throw new IllegalStateException("Deadlock detected: thread " + transaction.getId() + " tried to obtain a lock locked by thread " + owner.getId() + " which itself is already waiting on a lock held by the former thread");
        }

        final boolean gotLock;
        try {
            gotLock = writeLock.tryLock(10000, TimeUnit.MILLISECONDS);
//            writeLock.lockInterruptibly();
        }
        catch (InterruptedException e) {
            log.error("[" + threadId + "] Interrupted while trying to lock @" + Integer.toHexString(writeLock.hashCode()));
            throw new ProcessingException(ResponseStatus.UNKNOWN_ERROR, "Interrupted while waiting to lock an entity @" + Integer.toHexString(writeLock.hashCode()));
        }
        final Transaction currentOwner = owner;
        if (!gotLock) {
            final StringBuilder errBuilder = new StringBuilder(512);
            errBuilder.append("[").append(threadId).append("] Timed out on lock @").append(Integer.toHexString(writeLock.hashCode()));
            if (currentOwner == null) errBuilder.append(", lock owner is not known");
            else errBuilder.append(", lock owner is thread [").append(currentOwner.getId()).append("]");
            log.error(errBuilder.toString());
            throw new ProcessingException(ResponseStatus.UNKNOWN_ERROR, "Timed out on lock @" + Integer.toHexString(writeLock.hashCode()));
        }

        owner = transaction;
        transaction.addLock(this);
    }

    @Override
    public final void unlock(final Transaction transaction) {
        final int lockCount = writeLock.getHoldCount();
        if (lockCount < 1) throw new IllegalStateException("The thread used for unlocking is not the same as the thread used for locking");

        final StringBuilder logBuilder = new StringBuilder(4096);
        logBuilder
                .append("[")
                .append(Thread.currentThread().getId())
                .append("] unlock() @")
                .append(Integer.toHexString(writeLock.hashCode()))
                .append(" on ")
                .append(getClass().getSimpleName())
                .append(" of entity class ");
        if (committed != null) logBuilder.append(committed.getClass().getCanonicalName()).append(" with ID ").append(committed.getId());
        else if (uncommitted != null) logBuilder.append(uncommitted.getClass().getCanonicalName()).append(" with ID ").append(uncommitted.getId());
        else logBuilder.append("(null)");
//        Utils.formatStackTraceRange(logBuilder, Thread.currentThread().getStackTrace(), 2, 6);
        log.debug(logBuilder.toString());

//        if (transaction != owner) throw new IllegalStateException("The transaction used for unlocking is not the same as the transaction used for locking");
        owner.removeLock(this);
        if (lockCount == 1) owner = null;
        writeLock.unlock();
    }

    @Override
    public boolean isQueued(final Transaction transaction) {
        return writeLock.hasQueuedThread(transaction.getThread());
    }

    @Override
    public final ReentrantLock getLock() {
        return writeLock;
    }

    public V getCurrentValue() {
        return committed;
    }

    public void setCurrentValue(final V value) {
        committed = value;
    }

    public V getNewValue() {
        return uncommitted;
    }

    public void setNewValue(final V newValue) {
        uncommitted = newValue;
    }

    public Transaction getTransaction() {
        return owner;
    }

/*    public void setTransaction(final Transaction transaction) {
        owner = transaction;
    }*/

    public static <V extends Id<?>> TransactionalEntity<V> createCommitted(final V value) {
        return new TransactionalEntity<>(value);
    }

    public static <V extends Id<?>> TransactionalEntity<V> createInTransaction(final V value, final Transaction transaction) {
        return new TransactionalEntity<>(value, transaction);
    }
}
