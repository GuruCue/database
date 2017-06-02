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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.spi.ExtendedLoggerWrapper;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Logger meant for services: when a service is invoked, it takes a
 * ServiceLogger instance and provides it to the lower processing
 * layers when invoking them, so all their logs are grouped under
 * the same "umbrella". This is primarily done with the inclusion
 * of thread ID in every log message.
 */
public final class ServiceLogger extends ExtendedLoggerWrapper {
    private static final long serialVersionUID = 2729617998188385029L;
    private static final ConcurrentMap<LoggerKey, ServiceLogger> loggers = new ConcurrentHashMap<>();

    private final Logger logger;
    private final String logPrefix;

    private ServiceLogger(final ExtendedLogger logger, final String logPrefix) {
        super(logger, logger.getName(), logger.getMessageFactory());
        this.logger = logger;
        this.logPrefix = logPrefix;
    }

    /**
     * The ServiceLogger factory. It takes a logger name and prefix to use
     * for each log message.
     *
     * @param name the logger name
     * @param logPrefix the prefix to use with each log message
     * @return a ServiceLogger instance with the given name and using the given log prefix
     */
    public static ServiceLogger getLogger(final String name, final String logPrefix) {
        if (name == null) throw new NullPointerException("The logger name is null");
        if (logPrefix == null) throw new NullPointerException("The logging prefix is null");
        final LoggerKey key = new LoggerKey(name, logPrefix);
        ServiceLogger logger = loggers.get(key);
        if (logger != null) return logger;
        final Logger trueLogger = LogManager.getLogger(key.loggerName);
        final ExtendedLogger proxiedLogger = trueLogger instanceof ExtendedLogger ? (ExtendedLogger) trueLogger : new FallbackLoggerWrapper(trueLogger);
        logger = new ServiceLogger(proxiedLogger, key.logPrefix);
        ServiceLogger previousLogger = loggers.putIfAbsent(key, logger);
        if (previousLogger == null) return logger;
        return previousLogger;
    }

    /**
     * Create a sub-logger from this logger, appending the givenname part,
     * using the dot delimiter.
     *
     * @param subname the name part to append to the name of this logger
     * @return a logger with the extended name and using the same log prefix
     */
    public ServiceLogger subLogger(final String subname) {
        return getLogger(logger.getName() + "." + subname, logPrefix);
    }

    @Override
    public void logMessage(final String s, final Level level, final Marker marker, final Message message, final Throwable throwable) {
        super.logMessage(s, level, marker, new PrefixedMessage(logPrefix, message), throwable);
    }

    private static final class LoggerKey {
        final String loggerName;
        final String logPrefix;
        final int hash;

        LoggerKey(final String loggerName, final String logPrefix) {
            this.loggerName = loggerName;
            this.logPrefix = logPrefix;
            this.hash = (loggerName.hashCode() * 31) + logPrefix.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj == null) return false;
            if (obj instanceof LoggerKey) {
                final LoggerKey other = (LoggerKey)obj;
                if (!loggerName.equals(other.loggerName)) return false;
                return logPrefix.equals(other.logPrefix);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    /**
     * A fallback class, instantiated when ServiceLogger doesn't obtain an ExtendedLogger
     * instance to proxy logs, but some other Logger instance. This class wraps a Logger,
     * making it an ExtendedLogger. It's not exactly a very good solution, but it
     * should be good enough.
     */
    public static final class FallbackLoggerWrapper extends AbstractLogger {
        private static final long serialVersionUID = -8887442070042142654L;
        private final Logger originalLogger;

        FallbackLoggerWrapper(final Logger originalLogger) {
            super(originalLogger.getName(), originalLogger.getMessageFactory());
            this.originalLogger = originalLogger;
        }

        @Override
        public boolean isEnabled(final Level level, final Marker marker, final Message message, final Throwable throwable) {
            return originalLogger.getLevel().intLevel() >= level.intLevel();
        }

        @Override
        public boolean isEnabled(final Level level, final Marker marker, final Object o, final Throwable throwable) {
            return originalLogger.getLevel().intLevel() >= level.intLevel();
        }

        @Override
        public boolean isEnabled(final Level level, final Marker marker, final String s, final Throwable throwable) {
            return originalLogger.getLevel().intLevel() >= level.intLevel();
        }

        @Override
        public boolean isEnabled(final Level level, final Marker marker, final String s) {
            return originalLogger.getLevel().intLevel() >= level.intLevel();
        }

        @Override
        public boolean isEnabled(final Level level, final Marker marker, final String s, final Object... objects) {
            return originalLogger.getLevel().intLevel() >= level.intLevel();
        }

        @Override
        public void logMessage(final String s, final Level level, final Marker marker, final Message message, final Throwable throwable) {
            originalLogger.log(level, marker, message, throwable);
        }

        @Override
        public Level getLevel() {
            return originalLogger.getLevel();
        }
    }

    /**
     * Prefixes a log message with the given prefix.
     */
    public static final class PrefixedMessage implements Message {
        private final String prefix;
        private final Message message;

        PrefixedMessage(final String prefix, final Message message) {
            this.prefix = prefix;
            this.message = message;
        }

        @Override
        public String getFormattedMessage() {
            return prefix + message.getFormattedMessage();
        }

        @Override
        public String getFormat() {
            return message.getFormat();
        }

        @Override
        public Object[] getParameters() {
            return message.getParameters();
        }

        @Override
        public Throwable getThrowable() {
            return message.getThrowable();
        }
    }
}
