/*
 * Copyright 2002-2014 The Jamocha Team
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
package org.jamocha.function.impls;

import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;

/**
 * Loads implementations of the {@link Function} interface having side effects.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see Function
 * @see FunctionDictionary
 */
public class SideEffects {
	static {
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Clear.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Exit.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Facts.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.ListDeftemplates.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Ppdeftemplate.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Printout.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Reset.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Run.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Halt.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.ExportGv.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Unwatch.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Watch.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.SetStrategy.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.GetStrategy.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Load.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.LoadAsterisk.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.LoadFacts.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.Save.class);
		FunctionDictionary.addImpl(org.jamocha.function.impls.sideeffects.SaveFacts.class);
	}
}
