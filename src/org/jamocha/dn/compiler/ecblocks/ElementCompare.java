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

import lombok.RequiredArgsConstructor;

import org.jamocha.dn.compiler.ecblocks.ECBlocks.ConstantExpression;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.Element;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.ElementVisitor;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.FactBinding;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.SlotBinding;
import org.jamocha.dn.compiler.ecblocks.ECBlocks.VariableExpression;
import org.jamocha.dn.compiler.ecblocks.FactVariablePartition.FactVariableSubSet;
import org.jamocha.languages.common.SingleFactVariable;

import com.google.common.base.Objects;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
@RequiredArgsConstructor
public class ElementCompare implements ElementVisitor {

	final FactVariablePartition blockFVP;
	final IdentityHashMap<FactVariableSubSet, SingleFactVariable> fvExtension;
	boolean equal = false;

	static boolean compare(final Iterable<Element> elements, final FactVariablePartition blockFVP,
			final IdentityHashMap<FactVariableSubSet, SingleFactVariable> fvExtension) {
		boolean equal = true;
		for (final Element element : elements) {
			equal &= element.accept(new ElementCompare(blockFVP, fvExtension)).equal;
		}
		return equal;
	}

	@Override
	public void visit(final FactBinding element) {
		this.equal = element.accept(new FactBindingCompare(element)).equal;
	}

	@Override
	public void visit(final SlotBinding element) {
		this.equal = element.accept(new SlotBindingCompare(element)).equal;
	}

	@Override
	public void visit(final ConstantExpression element) {
		this.equal = element.accept(new ConstantCompare(element)).equal;
	}

	@Override
	public void visit(final VariableExpression element) {
		this.equal = element.accept(new VariableCompare(element)).equal;
	}

	abstract class OtherElementIdentifier implements ElementVisitor {
		boolean equal = false;

		@Override
		public void visit(final ConstantExpression element) {
		}

		@Override
		public void visit(final FactBinding element) {
		}

		@Override
		public void visit(final SlotBinding element) {
		}

		@Override
		public void visit(final VariableExpression element) {
		}

	}

	@RequiredArgsConstructor
	class FactBindingCompare extends OtherElementIdentifier {
		final FactBinding blockElement;

		@Override
		public void visit(final FactBinding element) {
			final FactVariableSubSet lookup = blockFVP.lookup(blockElement.getFactVariable());
			if (null != lookup && fvExtension.get(lookup) == element.getFactVariable()) {
				equal = true;
			}
		}
	}

	@RequiredArgsConstructor
	class SlotBindingCompare extends OtherElementIdentifier {
		final SlotBinding blockElement;

		@Override
		public void visit(final SlotBinding element) {
			final FactVariableSubSet lookup = blockFVP.lookup(blockElement.getFactVariable());
			if (null != lookup && fvExtension.get(lookup) == element.getFactVariable()
					&& element.getSlot().getSlot() == blockElement.getSlot().getSlot()) {
				equal = true;
			}
		}
	}

	@RequiredArgsConstructor
	class ConstantCompare extends OtherElementIdentifier {
		final ConstantExpression blockElement;

		@Override
		public void visit(final ConstantExpression element) {
			if (Objects.equal(blockElement.constant.evaluate(), element.constant.evaluate())) {
				equal = true;
			}
		}
	}

	@RequiredArgsConstructor
	class VariableCompare extends OtherElementIdentifier {
		final VariableExpression blockElement;

		@Override
		public void visit(final VariableExpression element) {
			throw new UnsupportedOperationException();
		}
	}
}
