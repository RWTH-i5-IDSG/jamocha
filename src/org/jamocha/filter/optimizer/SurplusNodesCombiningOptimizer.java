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
package org.jamocha.filter.optimizer;

import java.util.Collection;

import org.jamocha.dn.ConstructCache.Defrule.TranslatedPath;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
public class SurplusNodesCombiningOptimizer implements Optimizer {

	static final String name = "SurplusNodesCombiner";
	static final SurplusNodesCombiningOptimizer instance = new SurplusNodesCombiningOptimizer();
	static {
		OptimizerFactory.addImpl(name, () -> instance);
	}

	@Override
	public Collection<TranslatedPath> optimize(final Collection<TranslatedPath> rules) {
		// TODO Auto-generated method stub
		return rules;
	}

}
