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

import java.util.IdentityHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.compiler.ecblocks.ElementPartition.ElementSubSet;
import org.jamocha.dn.compiler.ecblocks.element.Element;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
@Getter
public class ElementPartition extends Partition<Element, ElementSubSet> {
	@Getter
	public static class ElementSubSet extends Partition.SubSet<Element> {
		public ElementSubSet(final IdentityHashMap<RowIdentifier, Element> elements) {
			super(elements);
		}

		public ElementSubSet(final Map<RowIdentifier, ? extends Element> elements) {
			this(new IdentityHashMap<>(elements));
		}

		public ElementSubSet(final ElementSubSet copy) {
			super(copy);
		}
	}

	public ElementPartition(final Partition<Element, ElementSubSet> copy) {
		super(copy, ElementSubSet::new);
	}
}