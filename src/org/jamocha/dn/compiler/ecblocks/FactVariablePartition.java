/*
 * Copyright 2002-2015 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.compiler.ecblocks;

import static org.jamocha.util.Lambdas.newHashSet;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.Template;
import org.jamocha.languages.common.SingleFactVariable;

import com.atlassian.fugue.Either;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
class FactVariablePartition extends Partition<SingleFactVariable, FactVariablePartition.FactVariableSubSet> {
	@Getter
	static class FactVariableSubSet extends Partition.SubSet<SingleFactVariable> {
		final Template template;

		public FactVariableSubSet(final IdentityHashMap<Either<Rule, ExistentialProxy>, SingleFactVariable> elements) {
			super(elements);
			this.template = elements.values().iterator().next().getTemplate();
		}

		public FactVariableSubSet(final Map<Either<Rule, ExistentialProxy>, SingleFactVariable> elements) {
			this(new IdentityHashMap<>(elements));
		}

		public FactVariableSubSet(final FactVariableSubSet copy) {
			super(copy);
			this.template = copy.template;
		}
	}

	final IdentityHashMap<Template, Set<FactVariablePartition.FactVariableSubSet>> templateLookup =
			new IdentityHashMap<>();

	public FactVariablePartition(final FactVariablePartition copy) {
		super(copy, FactVariableSubSet::new);
		this.templateLookup.putAll(copy.templateLookup);
	}

	@Override
	public void add(final FactVariablePartition.FactVariableSubSet newSubSet) {
		super.add(newSubSet);
		this.templateLookup.computeIfAbsent(newSubSet.template, newHashSet()).add(newSubSet);
	}

	public Set<FactVariablePartition.FactVariableSubSet> lookupByTemplate(final Template template) {
		return this.templateLookup.get(template);
	}

	@Override
	public boolean remove(final FactVariableSubSet s) {
		final boolean removed = super.remove(s);
		if (removed) {
			final Set<FactVariableSubSet> set = templateLookup.get(s.template);
			set.remove(s);
			if (set.isEmpty()) {
				templateLookup.remove(s.template);
			}
		}
		return removed;
	}
}