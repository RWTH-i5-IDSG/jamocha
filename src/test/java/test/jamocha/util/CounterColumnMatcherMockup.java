/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */
package test.jamocha.util;

import org.jamocha.dn.memory.CounterColumn;
import org.jamocha.dn.memory.CounterColumnMatcher;
import org.jamocha.filter.PathFilter;

/**
 * Mockup implementation of {@link CounterColumnMatcher} matching every {@link PathFilter} to null.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class CounterColumnMatcherMockup implements CounterColumnMatcher {
    public static final CounterColumnMatcherMockup COUNTER_COLUMN_MATCHER_MOCKUP = new CounterColumnMatcherMockup();

    @Override
    public CounterColumn getCounterColumn(final PathFilter filterElement) {
        return null;
    }
}
