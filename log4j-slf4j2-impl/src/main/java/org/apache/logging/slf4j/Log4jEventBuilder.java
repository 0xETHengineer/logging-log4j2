/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.apache.logging.log4j.BridgeAware;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.CloseableThreadContext.Instance;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.Logger;
import org.slf4j.Marker;
import org.slf4j.spi.LoggingEventBuilder;

public class Log4jEventBuilder implements LoggingEventBuilder {

    private static final String FQCN = Log4jEventBuilder.class.getName();

    private final Log4jMarkerFactory markerFactory;
    private final Logger logger;
    private final List<Object> arguments = new ArrayList<>();
    private String message = null;
    private org.apache.logging.log4j.Marker marker = null;
    private Throwable throwable = null;
    private Map<String, String> keyValuePairs = null;
    private final Level level;

    public Log4jEventBuilder(final Log4jMarkerFactory markerFactory, final Logger logger, final Level level) {
        this.markerFactory = markerFactory;
        this.logger = logger;
        this.level = level;
    }

    @Override
    public LoggingEventBuilder setCause(Throwable cause) {
        this.throwable = cause;
        return this;
    }

    @Override
    public LoggingEventBuilder addMarker(Marker marker) {
        this.marker = markerFactory.getLog4jMarker(marker);
        return this;
    }

    @Override
    public LoggingEventBuilder addArgument(Object p) {
        arguments.add(p);
        return this;
    }

    @Override
    public LoggingEventBuilder addArgument(Supplier<?> objectSupplier) {
        arguments.add(objectSupplier.get());
        return this;
    }

    @Override
    public LoggingEventBuilder addKeyValue(String key, Object value) {
        if (keyValuePairs == null) {
            keyValuePairs = new HashMap<>();
        }
        keyValuePairs.put(key, String.valueOf(value));
        return this;
    }

    @Override
    public LoggingEventBuilder addKeyValue(String key, Supplier<Object> valueSupplier) {
        if (keyValuePairs == null) {
            keyValuePairs = new HashMap<>();
        }
        keyValuePairs.put(key, String.valueOf(valueSupplier.get()));
        return this;
    }

    @Override
    public LoggingEventBuilder setMessage(String message) {
        this.message = message;
        return this;
    }

    @Override
    public LoggingEventBuilder setMessage(Supplier<String> messageSupplier) {
        this.message = messageSupplier.get();
        return this;
    }

    @Override
    public void log() {
        final LogBuilder logBuilder = logger.atLevel(level)
                .withMarker(marker)
                .withThrowable(throwable);
        if (logBuilder instanceof BridgeAware) {
            ((BridgeAware) logBuilder).setEntryPoint(FQCN);
        }
        if (keyValuePairs == null || keyValuePairs.isEmpty()) {
            logBuilder.log(message, arguments.toArray());
        } else {
            try (Instance c = CloseableThreadContext.putAll(keyValuePairs)) {
                logBuilder.log(message, arguments.toArray());
            }
        }
    }

    @Override
    public void log(String message) {
        setMessage(message);
        log();
    }

    @Override
    public void log(String message, Object arg) {
        setMessage(message);
        addArgument(arg);
        log();
    }

    @Override
    public void log(String message, Object arg0, Object arg1) {
        setMessage(message);
        addArgument(arg0);
        addArgument(arg1);
        log();
    }

    @Override
    public void log(String message, Object... args) {
        setMessage(message);
        for (final Object arg : args) {
            addArgument(arg);
        }
        log();
    }

    @Override
    public void log(Supplier<String> messageSupplier) {
        setMessage(messageSupplier);
        log();
    }

}
