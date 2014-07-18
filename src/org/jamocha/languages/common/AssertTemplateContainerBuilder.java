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
package org.jamocha.languages.common;

import java.util.HashMap;
import java.util.Map;

import lombok.Value;

import org.jamocha.dn.memory.SlotAddress;
import org.jamocha.dn.memory.Template;
import org.jamocha.filter.fwa.Assert;
import org.jamocha.filter.fwa.FunctionWithArguments;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
public class AssertTemplateContainerBuilder {
	final Template template;
	final Map<SlotAddress, FunctionWithArguments> values = new HashMap<>();

	public AssertTemplateContainerBuilder addValue(final SlotAddress slotAddress,
			final FunctionWithArguments value) {
		if (template.getSlotType(slotAddress) != value.getReturnType()) {
			throw new IllegalArgumentException("Type mismatch!");
		}
		this.values.put(slotAddress, value);
		return this;
	}

	public AssertTemplateContainerBuilder addValue(final String slotName,
			final FunctionWithArguments value) {
		final SlotAddress slotAddress = template.getSlotAddress(slotName);
		if (null == slotAddress) {
			throw new IllegalArgumentException("No Slot with name " + slotName + "!");
		}
		return addValue(slotAddress, value);
	}

	public Assert.TemplateContainer build() {
		return new Assert.TemplateContainer(template, template.applyDefaultsAndOrder(values));
	}
}
