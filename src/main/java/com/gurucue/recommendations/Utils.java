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

import java.util.Iterator;
import java.util.List;

public final class Utils {
    private static final String INDENT_PREFIX = "\n    ";

    public static void formatStackTrace(final StringBuilder output, final Throwable throwable) {
        if (throwable == null) return;
        formatStackTrace(output, throwable.getStackTrace());
    }

    public static void formatStackTrace(final StringBuilder output, final StackTraceElement[] stack) {
        if (stack == null) return;
        final int n = stack.length;
        for (int i = 0; i < n; i++) {
            output.append(INDENT_PREFIX).append(stack[i].toString());
        }
    }

    public static void formatStackTraceRange(final StringBuilder output, final StackTraceElement[] stack, final int startIndex, final int endIndex) {
        if (stack == null) return;
        final int n = endIndex < stack.length ? endIndex + 1 : stack.length;
        for (int i = startIndex; i < n; i++) {
            output.append(INDENT_PREFIX).append(stack[i].toString());
        }
    }

    public static void join(final StringBuilder output, final List<String> strings, final String delimiter) {
        if ((strings == null) || (strings.size() == 0)) return;
        final Iterator<String> iterator = strings.iterator();
        output.append(iterator.next());
        while (iterator.hasNext()) {
            output.append(delimiter).append(iterator.next());
        }
    }
}
