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

package org.jamocha.languages.sl.sl2clips_adapter.configurations;

public class IdentifyingExpressionSLConfiguration implements SLConfiguration {

	private SLConfiguration refOp;

	private SLConfiguration termOrIE;

	private SLConfiguration wff;

	public SLConfiguration getRefOp() {
		return refOp;
	}

	public void setRefOp(SLConfiguration refOp) {
		this.refOp = refOp;
	}

	public SLConfiguration getTermOrIE() {
		return termOrIE;
	}

	public void setTermOrIE(SLConfiguration termOrIE) {
		this.termOrIE = termOrIE;
	}

	public SLConfiguration getWff() {
		return wff;
	}

	public void setWff(SLConfiguration wff) {
		this.wff = wff;
	}

	public String compile(SLCompileType compileType) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("(").append(refOp.compile(compileType)).append(" ")
				.append(termOrIE.compile(compileType)).append(" ").append(
						wff.compile(compileType)).append(")");
		return buffer.toString();
	}
}
