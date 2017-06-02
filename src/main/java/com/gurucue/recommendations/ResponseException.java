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

import com.gurucue.recommendations.translator.DataTranslator;
import com.gurucue.recommendations.translator.TranslatorAware;

import java.io.IOException;

/**
 * The exception to raise from within the
 */
public class ResponseException extends Exception implements TranslatorAware {
    private static final long serialVersionUID = 6682111326353235906L;
    protected ResponseStatus status = null;
    protected String details = null;

    public ResponseException(ResponseStatus status) {
        super(status.getDescription());
        this.status = status;
        this.details  = status.getDescription();
    }

    public ResponseException(ResponseStatus status, String details) {
        super(status.getDescription());
        this.status = status;
        this.details = details;
    }

    public ResponseException(ResponseStatus status, Throwable cause) {
        super(status.getDescription(), cause);
        this.status = status;
        this.details  = status.getDescription();
    }

    public ResponseException(ResponseStatus status, Throwable cause, String details) {
        super(status.getDescription(), cause);
        this.status = status;
        this.details = details;
    }

    public ResponseStatus getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public void translate(DataTranslator translator) throws IOException {
        translator.beginObject("response");
        translator.addKeyValue("resultCode", status.getCode());
        translator.addKeyValue("resultMessage", getDetails());
        translator.endObject();
    }

    @Override
    public String toString() {
        return "ResponseException(status=" + (status == null ? "null" : status.toString()) +
            ", status.code=" + ((status == null) || (status.getCode() == null) ? "null" : status.getCode()) +
            ", status.description=" + ((status == null) || (status.getDescription() == null) ? "null" : status.getDescription()) +
            ", details=" + (details == null ? "null" : details) +
            ")";
    }
}
