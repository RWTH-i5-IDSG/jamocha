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
package test.jamocha.util;

import static org.jamocha.util.ToArray.toArray;

import java.util.Arrays;
import java.util.Map;

import lombok.experimental.UtilityClass;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.ObjectTypeNode;
import org.jamocha.filter.Path;
import org.jamocha.filter.PathFilter;
import org.jamocha.filter.PathNodeFilterSet;
import org.jamocha.function.fwa.PathLeaf;
import org.jamocha.function.fwa.PredicateWithArgumentsComposite;
import org.jamocha.function.impls.predicates.DummyPredicate;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 *
 */
@UtilityClass
public class ShareNode {
	public static void shareOTN(final ObjectTypeNode otn, final Map<Path, FactAddress> map, final Path... path) {
		otn.shareNode(PathNodeFilterSet.newRegularPathNodeFilterSet(new PathFilter(
				new PredicateWithArgumentsComposite<PathLeaf>(DummyPredicate.instance, toArray(
						Arrays.stream(path).map(p -> new PathLeaf(p, null)), PathLeaf[]::new)))), map, path);
	}
}
