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
package org.apache.logging.log4j.jpl;

import java.lang.System.Logger;

/**
 * @since 2.14
 */
public class Log4jSystemLoggerFinder extends System.LoggerFinder {

    private final Log4jSystemLoggerAdapter loggerAdapter = new Log4jSystemLoggerAdapter();

    @Override
    public Logger getLogger(String name, Module module) {
        return loggerAdapter.getLogger(name);
    }
}
