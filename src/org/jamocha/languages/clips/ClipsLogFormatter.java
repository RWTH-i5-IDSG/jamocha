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
package org.jamocha.languages.clips;

import org.apache.logging.log4j.Logger;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.logging.LogFormatter;
import org.jamocha.logging.MarkerType;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class ClipsLogFormatter implements LogFormatter {

	private final static ClipsLogFormatter singleton = new ClipsLogFormatter();

	/**
	 * Retrieves a singleton instance of the {@link org.jamocha.dn.memory.MemoryFactory}
	 * implementation.
	 * 
	 * @return a singleton instance of the {@link org.jamocha.dn.memory.MemoryFactory}
	 *         implementation
	 */
	public static LogFormatter getMessageFormatterFactory() {
		return singleton;
	}

	private ClipsLogFormatter() {
	}

	@Override
	public void messageFactDetails(final SideEffectFunctionToNetwork network, int id,
			MemoryFact value) {
		network.getInteractiveEventsLogger().info("f-{}\t{}", id, value);
	}

	@Override
	public void messageTemplateDetails(final SideEffectFunctionToNetwork network,
			final Template template) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(deftemplate ").append(template.getName());
		final String description = template.getDescription();
		if (null != description && !description.isEmpty()) {
			sb.append(" ").append(SlotType.STRING.toString(description));
		}
		for (final Slot slot : template.getSlots()) {
			sb.append("\n\t(slot ").append(slot.getName()).append(" (type ")
					.append(slot.getSlotType().toString()).append("))");
		}
		sb.append(")\n");
		network.getInteractiveEventsLogger().info(sb.toString());
	}

	@Override
	public void messageFactAssertions(final SideEffectFunctionToNetwork network,
			FactIdentifier[] assertedFacts) {
		if (!network.getTypedFilter().isRelevant(MarkerType.FACTS)) {
			return;
		}
		final Logger interactiveEventsLogger = network.getInteractiveEventsLogger();
		for (final FactIdentifier fi : assertedFacts) {
			final MemoryFact memoryFact = network.getMemoryFact(fi);
			if (null == memoryFact) {
				continue;
			}
			interactiveEventsLogger.info(memoryFact.getTemplate().getInstanceMarker(),
					"==> f-{}\t{}", fi.getId(), memoryFact);
		}
	}

	@Override
	public void messageFactRetractions(final SideEffectFunctionToNetwork network,
			FactIdentifier[] factsToRetract) {
		final Logger interactiveEventsLogger = network.getInteractiveEventsLogger();
		for (final FactIdentifier fi : factsToRetract) {
			final MemoryFact memoryFact = network.getMemoryFact(fi);
			if (null == memoryFact) {
				interactiveEventsLogger.error("[PRNTUTIL1] Unable to find fact f-{}", fi);
				continue;
			}
			interactiveEventsLogger.info(memoryFact.getTemplate().getInstanceMarker(),
					"<== f-{}\t{}", fi, memoryFact);
		}
	}

	@Override
	public void messageArgumentTypeMismatch(SideEffectFunctionToNetwork network, String function,
			int paramIndex, String expected) {
		network.getInteractiveEventsLogger().error(
				"[ARGACCES5] Function {} expected argument #{} to be of type {}", function,
				paramIndex, expected);
	}

	@Override
	public void messageUnknownSymbol(final SideEffectFunctionToNetwork network,
			final String expectedType, final String name) {
		network.getInteractiveEventsLogger().error("[PRNTUTIL1] Unable to find {} {}",
				expectedType, name);
	}
}
