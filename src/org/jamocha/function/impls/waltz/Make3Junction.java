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
package org.jamocha.function.impls.waltz;

import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.function.Function;
import org.jamocha.function.FunctionDictionary;
import org.jamocha.function.impls.FunctionVisitor;
import org.jamocha.languages.common.ScopeStack.Symbol;

import com.google.common.collect.ImmutableMap;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public abstract class Make3Junction implements Function<Object> {
	public static final String inClips = "make-3-junction";
	private static final SlotType[] paramTypes = new SlotType[] { SlotType.LONG, SlotType.LONG, SlotType.LONG,
			SlotType.LONG };

	@Override
	public String inClips() {
		return inClips;
	}

	@Override
	public SlotType getReturnType() {
		return SlotType.FACTADDRESS;
	}

	@Override
	public <V extends FunctionVisitor> V accept(final V visitor) {
		visitor.visit(this);
		return visitor;
	}

	@Override
	public SlotType[] getParamTypes() {
		return paramTypes;
	}

	static long get_x(final long val) {
		return val / 100;
	}

	static long get_y(final long val) {
		return val % 100;
	}

	static double get_angle(final long p1, final long p2) {
		final long delta_x = (get_x(p2) - get_x(p1));
		final long delta_y = (get_y(p2) - get_y(p1));
		if (delta_x == 0) {
			if (delta_y > 0)
				return Math.PI / 2;
			else if (delta_y < 0)
				return Math.PI / -2;
		} else if (delta_y == 0) {
			if (delta_x > 0)
				return 0.0;
			else if (delta_x < 0)
				return Math.PI;
		}
		return Math.atan2(delta_y, delta_x);
	}

	static double inscribed_angle(final long basepoint, final long p1, final long p2) {
		final double angle1 = get_angle(basepoint, p1);
		final double angle2 = get_angle(basepoint, p2);
		final double temp = Math.abs(angle1 - angle2);
		if (temp > Math.PI) {
			return Math.abs(2 * Math.PI - temp);
		}
		return temp;
	}

	static {
		FunctionDictionary.addFixedArgsGeneratorWithSideEffects(inClips, paramTypes, (
				final SideEffectFunctionToNetwork network, final SlotType[] paramTypes) -> {
			return new Make3Junction() {
				@Override
				public Object evaluate(final Function<?>... params) {
					final long basepoint = (Long) params[0].evaluate();
					final long p1 = (Long) params[1].evaluate();
					final long p2 = (Long) params[2].evaluate();
					final long p3 = (Long) params[3].evaluate();

					final double angle12 = inscribed_angle(basepoint, p1, p2);
					final double angle13 = inscribed_angle(basepoint, p1, p3);
					final double angle23 = inscribed_angle(basepoint, p2, p3);

					final double sum1213 = angle12 + angle13;
					final double sum1223 = angle12 + angle23;
					final double sum1323 = angle13 + angle23;

					double sum;
					final long barb1, barb2, shaft;
					if (sum1213 < sum1223) {
						if (sum1213 < 1323) {
							sum = sum1213;
							shaft = p1;
							barb1 = p2;
							barb2 = p3;
						} else {
							sum = sum1323;
							shaft = p3;
							barb1 = p1;
							barb2 = p2;
						}
					} else {
						if (sum1223 < 1323) {
							sum = sum1223;
							shaft = p2;
							barb1 = p1;
							barb2 = p3;
						} else {
							sum = sum1323;
							shaft = p3;
							barb1 = p1;
							barb2 = p2;
						}
					}

					final double delta = Math.abs(sum - Math.PI);
					final Symbol jtype;
					if (delta < 0.001) {
						jtype = network.createTopLevelSymbol("tee");
					} else if (sum > Math.PI) {
						jtype = network.createTopLevelSymbol("fork");
					} else {
						jtype = network.createTopLevelSymbol("arrow");
					}

					// (assert (junction (p1 (integer ?barb1))
					// (p2 (integer ?shaft))
					// (p3 (integer ?barb2))
					// (base_point (integer ?basepoint))
					// (jtype ?jtype))))

					final Template template = network.getTemplate("junction");
					final SlotAddress slot_p1 = template.getSlotAddress("p1");
					final SlotAddress slot_p2 = template.getSlotAddress("p2");
					final SlotAddress slot_p3 = template.getSlotAddress("p3");
					final SlotAddress slot_bp = template.getSlotAddress("base_point");
					final SlotAddress slot_jt = template.getSlotAddress("jtype");

					final Fact newFact =
							template.newFact(ImmutableMap.of(slot_p1, barb1, slot_p2, shaft, slot_p3, barb2, slot_bp,
									basepoint, slot_jt, jtype));
					final FactIdentifier[] factIdentifier = network.assertFacts(newFact);

					return factIdentifier.length == 0 ? null : factIdentifier[0];
				}
			};
		});
	}
}
