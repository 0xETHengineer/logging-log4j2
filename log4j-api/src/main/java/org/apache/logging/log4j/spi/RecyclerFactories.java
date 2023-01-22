/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
package org.apache.logging.log4j.spi;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.logging.log4j.util.QueueFactory;
import org.apache.logging.log4j.util.Queues;
import org.apache.logging.log4j.util.StringParameterParser;

import static org.apache.logging.log4j.util.Constants.isThreadLocalsEnabled;

public final class RecyclerFactories {

    private RecyclerFactories() {}

    private static int getDefaultCapacity() {
        return Math.max(
                2 * Runtime.getRuntime().availableProcessors() + 1,
                8);
    }

    public static RecyclerFactory getDefault() {
        return isThreadLocalsEnabled()
                ? ThreadLocalRecyclerFactory.getInstance()
                : new QueueingRecyclerFactory(Queues.MPMC.factory(getDefaultCapacity()));
    }

    public static RecyclerFactory ofSpec(final String recyclerFactorySpec) {

        // TLA-, MPMC-, or ABQ-based queueing factory -- if nothing is specified.
        if (recyclerFactorySpec == null) {
            return getDefault();
        }

        // Is a dummy factory requested?
        else if (recyclerFactorySpec.equals("dummy")) {
            return DummyRecyclerFactory.getInstance();
        }

        // Is a TLA factory requested?
        else if (recyclerFactorySpec.equals("threadLocal")) {
            return ThreadLocalRecyclerFactory.getInstance();
        }

        // Is a queueing factory requested?
        else if (recyclerFactorySpec.startsWith("queue")) {

            // Determine the default capacity.
            final int defaultCapacity = getDefaultCapacity();

            return readQueueingRecyclerFactory(recyclerFactorySpec, defaultCapacity);
        }

        // Bogus input, bail out.
        else {
            throw new IllegalArgumentException(
                    "invalid recycler factory: " + recyclerFactorySpec);
        }

    }

    private static RecyclerFactory readQueueingRecyclerFactory(
            final String recyclerFactorySpec,
            final int defaultCapacity) {

        // Parse the spec.
        final String queueFactorySpec = recyclerFactorySpec.substring(
                "queue".length() +
                        (recyclerFactorySpec.startsWith("queue:")
                                ? 1
                                : 0));
        final Map<String, StringParameterParser.Value> parsedValues =
                StringParameterParser.parse(
                        queueFactorySpec, Set.of("supplier", "capacity"));

        // Read the capacity.
        final StringParameterParser.Value capacityValue = parsedValues.get("capacity");
        final int capacity;
        if (capacityValue == null || capacityValue instanceof StringParameterParser.NullValue) {
            capacity = defaultCapacity;
        } else {
            try {
                capacity = Integer.parseInt(capacityValue.toString());
            } catch (final NumberFormatException error) {
                throw new IllegalArgumentException(
                        "failed reading capacity in queueing recycler " +
                                "factory: " + queueFactorySpec, error);
            }
        }

        // Read the supplier path.
        final StringParameterParser.Value supplierValue = parsedValues.get("supplier");
        final String supplierPath;
        if (supplierValue == null || supplierValue instanceof StringParameterParser.NullValue) {
            supplierPath = Queues.MPMC.factory(capacity);
        } else {
            supplierPath = supplierValue.toString();
        }

        // Execute the read spec.
        final QueueFactory queueFactory = Queues.createQueueFactory(queueFactorySpec, supplierPath, capacity);

        return new QueueingRecyclerFactory(queueFactory);
    }

}
