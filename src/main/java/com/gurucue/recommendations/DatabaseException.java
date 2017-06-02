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

/**
 * Indicates an error while working with a database.
 */
public class DatabaseException extends RuntimeException {
    private static final long serialVersionUID = 1927382871816119151L;
    private final boolean isRetriable;

    public DatabaseException() {
        super();
        this.isRetriable = false;
    }

    public DatabaseException(final String message) {
        super(message);
        this.isRetriable = false;
    }

    public DatabaseException(final Throwable cause) {
        super(cause);
        this.isRetriable = false;
    }

    public DatabaseException(final String message, final Throwable cause) {
        super(message, cause);
        this.isRetriable = false;
    }

    public DatabaseException(final String message, final boolean isRetriable) {
        super(message);
        this.isRetriable = isRetriable;
    }

    public DatabaseException(final Throwable cause, final boolean isRetriable) {
        super(cause);
        this.isRetriable = isRetriable;
    }

    public DatabaseException(final String message, final Throwable cause, final boolean isRetriable) {
        super(message, cause);
        this.isRetriable = isRetriable;
    }

    /**
     * Tells whether the database operation that was the cause of the
     * exception can be retried with a possibility that it will not cause
     * an exception again.
     *
     * @return whether there's a possibility that a retry will not throw an exception
     */
    public boolean getRetriable() {
        return isRetriable;
    }
}
