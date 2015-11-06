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

import java.util.Set;

import lombok.Getter;

import org.jamocha.dn.compiler.ecblocks.Filter.ExplicitFilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstance;
import org.jamocha.dn.compiler.ecblocks.Filter.FilterInstanceVisitor;
import org.jamocha.dn.compiler.ecblocks.element.Element;

import com.google.common.collect.Sets;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
@Getter
public class ElementCollector implements FilterInstanceVisitor {
	final Set<Element> elements = Sets.newIdentityHashSet();

	public static Set<Element> getElements(final Iterable<FilterInstance> filterInstances) {
		final ElementCollector elementCollector = new ElementCollector();
		filterInstances.forEach(fi -> fi.accept(elementCollector));
		return elementCollector.elements;
	}

	@Override
	public void visit(final ExplicitFilterInstance filterInstance) {
	}

	@Override
	public void visit(final ImplicitElementFilterInstance filterInstance) {
		elements.add(filterInstance.left);
		elements.add(filterInstance.right);
	}

	@Override
	public void visit(final ImplicitECFilterInstance filterInstance) {
		elements.add(filterInstance.left);
		elements.add(filterInstance.right);
	}
}
