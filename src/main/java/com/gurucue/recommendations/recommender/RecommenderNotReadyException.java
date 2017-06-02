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
package com.gurucue.recommendations.recommender;

public class RecommenderNotReadyException extends Exception {
    private static final long serialVersionUID = 639948180991954143L;
    private static final String defaultMessage = "Recommender not ready yet!";

    public RecommenderNotReadyException() {
        super(defaultMessage);
    }

    public RecommenderNotReadyException(String message) {
        super(message);
    }

    public RecommenderNotReadyException(String message, Throwable cause) {
        super(message, cause);
    }

    public RecommenderNotReadyException(Throwable cause) {
        super(defaultMessage, cause);
    }
}
