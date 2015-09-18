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

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.jamocha.function.fwa.ConstantLeaf;
import org.jamocha.function.fwa.ECLeaf;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwatransformer.FWATranslator;
import org.jamocha.languages.common.RuleCondition.EquivalenceClass;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class FWAEvaluatorForConstantECs extends FWATranslator<ECLeaf, PathLeaf> {
	final Map<EquivalenceClass, ConstantLeaf<PathLeaf>> lookup;

	public static ConstantLeaf<PathLeaf> evaluate(final Map<EquivalenceClass, ConstantLeaf<PathLeaf>> lookup,
			final FunctionWithArguments<ECLeaf> expression) {
		return new ConstantLeaf<>(expression.accept(new FWAEvaluatorForConstantECs(lookup)).functionWithArguments);
	}

	@Override
	public void visit(final ECLeaf leaf) {
		this.functionWithArguments = this.lookup.get(leaf.getEc());
	}

	@Override
	public FWATranslator<ECLeaf, PathLeaf> of() {
		return new FWAEvaluatorForConstantECs(this.lookup);
	}
}
