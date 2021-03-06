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
package org.jamocha.dn.memory.javaimpl;

import java.util.Map;
import java.util.Set;

import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryHandlerMainAndCounterColumnMatcher;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.dn.nodes.Edge;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathNodeFilterSet;

/**
 * Implementation of the {@link org.jamocha.dn.memory.MemoryFactory} interface. Supplies a SINGLETON INSTANCE via {@link
 * MemoryFactory#getMemoryFactory()}.
 *
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @see org.jamocha.dn.memory.MemoryFactory
 */
public final class MemoryFactory implements org.jamocha.dn.memory.MemoryFactory {

    private static final MemoryFactory SINGLETON = new MemoryFactory();

    /**
     * Retrieves a singleton INSTANCE of the {@link org.jamocha.dn.memory.MemoryFactory} implementation.
     *
     * @return a singleton INSTANCE of the {@link org.jamocha.dn.memory.MemoryFactory} implementation
     */
    public static org.jamocha.dn.memory.MemoryFactory getMemoryFactory() {
        return SINGLETON;
    }

    @Override
    public Template newTemplate(final String name, final String description, final Slot... slots) {
        return new org.jamocha.dn.memory.javaimpl.Template(name, description, slots);
    }

    private MemoryFactory() {
    }

    @Override
    public MemoryHandlerMain newMemoryHandlerMain(final Template template, final Path... paths) {
        return new MemoryHandlerMain(template, paths);
    }

    @Override
    public MemoryHandlerMainAndCounterColumnMatcher newMemoryHandlerMain(final PathNodeFilterSet filter,
            final Map<Edge, Set<Path>> edgesAndPaths) {
        return MemoryHandlerMain.newMemoryHandlerMain(filter, edgesAndPaths);
    }

    protected int factIdentifierCounter = 0;

    @Override
    public void resetFactIdentifierCounter() {
        factIdentifierCounter = 0;
    }

    /**
     * Returns a FactIdentifier containing the next available fact identifier.
     *
     * @return a FactIdentifier containing the next available fact identifier
     */
    public FactIdentifier getNextFactIdentifier() {
        return new FactIdentifier(factIdentifierCounter++);
    }
}
