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

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jamocha.function.fwa.FunctionWithArguments;
import org.jamocha.function.fwa.TypeLeaf;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public interface ExistentialInfo {
	public boolean isExistential();

	public boolean isPositive();

	public int[] getExistentialArguments();

	public static final ExistentialInfo REGULAR = new ExistentialInfo() {
		@Override
		public boolean isExistential() {
			return false;
		}

		@Override
		public boolean isPositive() {
			throw new UnsupportedOperationException();
		}

		@Override
		public int[] getExistentialArguments() {
			throw new UnsupportedOperationException();
		}
	};

	@RequiredArgsConstructor
	public static class PositiveExistentialInfo implements ExistentialInfo {
		@Getter(onMethod = @__({@Override}))
		final int[] existentialArguments;

		@Override
		public boolean isPositive() {
			return true;
		}

		@Override
		public boolean isExistential() {
			return true;
		}
	}

	@RequiredArgsConstructor
	public static class NegatedExistentialInfo implements ExistentialInfo {
		@Getter(onMethod = @__({@Override}))
		final int[] existentialArguments;

		@Override
		public boolean isPositive() {
			return false;
		}

		@Override
		public boolean isExistential() {
			return true;
		}
	}

	@Value
	public static class FunctionWithExistentialInfo {
		FunctionWithArguments<TypeLeaf> function;
		ExistentialInfo existentialInfo;
	}
}