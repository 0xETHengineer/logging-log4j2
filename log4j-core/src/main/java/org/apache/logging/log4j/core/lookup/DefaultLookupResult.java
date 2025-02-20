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
package org.apache.logging.log4j.core.lookup;

import java.util.Objects;

/** Default internal implementation of {@link LookupResult}. */
final class DefaultLookupResult implements LookupResult {

    private final String value;

    DefaultLookupResult(String value) {
        this.value = Objects.requireNonNull(value, "value is required");
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public boolean isLookupEvaluationAllowedInValue() {
        return false;
    }

    @Override
    public String toString() {
        return "DefaultLookupResult{value='" + value + "'}";
    }
}
