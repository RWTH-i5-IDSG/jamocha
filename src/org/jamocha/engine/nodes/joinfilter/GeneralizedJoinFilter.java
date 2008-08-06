/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jamocha.engine.nodes.joinfilter;

import org.jamocha.parser.EvaluationException;
import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.FactTuple;
import org.jamocha.engine.workingmemory.elements.Fact;

/**
 * The generalized join filters are used with the multi-join-nodes. here we
 * don't have right- and left-inputs but mixed input. so, the whole resulting
 * fact tuple will be evaluated instead of the combination of left- and right
 * input 
 * @author Josef Alexander Hahn
 */
public interface GeneralizedJoinFilter {

	boolean evaluate(FactTuple t, Engine engine)
			throws JoinFilterException, EvaluationException;

	public String toPPString();

}
