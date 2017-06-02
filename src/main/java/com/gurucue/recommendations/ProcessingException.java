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
 * A runtime exception representing an error in the processing logic.
 * This type of exception should be thrown when processing finds
 * itself in a situation it was not designed for, i.e. a precondition
 * failed, improper use, and similar.
 */
public class ProcessingException extends RuntimeException {
    private static final long serialVersionUID = -4987989171201794670L;
    protected final ResponseStatus status;

    public ProcessingException() {
        super(ResponseStatus.UNKNOWN_ERROR.getDescription());
        status = ResponseStatus.UNKNOWN_ERROR;
    }

    public ProcessingException(final String message) {
        super(message);
        status = ResponseStatus.UNKNOWN_ERROR;
    }

    public ProcessingException(final Throwable cause) {
        super(ResponseStatus.UNKNOWN_ERROR.getDescription(), cause);
        status = ResponseStatus.UNKNOWN_ERROR;
    }

    public ProcessingException(final String message, final Throwable cause) {
        super(message, cause);
        status = ResponseStatus.UNKNOWN_ERROR;
    }

    public ProcessingException(final ResponseStatus status) {
        super(status.getDescription());
        this.status = status;
    }

    public ProcessingException(final ResponseStatus status, final Throwable cause) {
        super(status.getDescription(), cause);
        this.status = status;
    }

    public ProcessingException(final ResponseStatus status, final String message) {
        super(message);
        this.status = status;
    }

    public ProcessingException(final ResponseStatus status, final String message, final Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public ResponseStatus getStatus() {
        return status;
    }
}
