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
 * the specific language governing permissions and limitations under
 * the License.
 */

package org.jamocha.dn.compiler.ecblocks;

import lombok.RequiredArgsConstructor;
import org.jamocha.dn.compiler.ecblocks.FilterPartition.FilterSubSet;
import org.jamocha.filter.ECFilter;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@RequiredArgsConstructor
public class FilterPartition
		extends InformedPartition<ECFilter, ExistentialInfo.FunctionWithExistentialInfo, FilterSubSet> {
	public static class FilterSubSet
			extends InformedPartition.InformedSubSet<ECFilter, ExistentialInfo.FunctionWithExistentialInfo> {
		public FilterSubSet(final IdentityHashMap<RowIdentifier, ECFilter> elements,
				final ExistentialInfo.FunctionWithExistentialInfo info) {
			super(elements, info);
		}

		public FilterSubSet(final FilterSubSet copy) {
			super(copy);
		}

		public FilterSubSet(final Map<RowIdentifier, ? extends ECFilter> elements,
				final ExistentialInfo.FunctionWithExistentialInfo info) {
			super(elements, info);
		}
	}

	public FilterPartition(final FilterPartition copy) {
		super(copy, FilterSubSet::new);
	}
}
