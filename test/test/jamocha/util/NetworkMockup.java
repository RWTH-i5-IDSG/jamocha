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
package test.jamocha.util;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

import org.apache.logging.log4j.Logger;
import org.jamocha.dn.ConstructCache.Deffacts;
import org.jamocha.dn.ConstructCache.Defrule;
import org.jamocha.dn.ParserToNetwork;
import org.jamocha.dn.SideEffectFunctionToNetwork;
import org.jamocha.dn.memory.Fact;
import org.jamocha.dn.memory.FactIdentifier;
import org.jamocha.dn.memory.MemoryFact;
import org.jamocha.dn.memory.MemoryFactToFactIdentifier;
import org.jamocha.dn.memory.SlotType;
import org.jamocha.dn.memory.Template;
import org.jamocha.dn.memory.Template.Slot;
import org.jamocha.function.fwa.Assert.TemplateContainer;
import org.jamocha.languages.common.ScopeStack;
import org.jamocha.logging.LogFormatter;
import org.jamocha.logging.TypedFilter;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
public class NetworkMockup implements ParserToNetwork, SideEffectFunctionToNetwork {

	Map<String, Template> templates = new HashMap<>();
	Map<String, Defrule> rules = new HashMap<>();
	final ScopeStack scope = new ScopeStack();
	final Template initialFactTemplate;

	@Getter(onMethod = @__(@Override))
	final EnumMap<SlotType, Object> defaultValues = new EnumMap<>(SlotType.class);

	public NetworkMockup() {
		this.initialFactTemplate = defTemplate("initial-fact", "");
		defFacts("initial-fact", "", new TemplateContainer(initialFactTemplate));

		{
			final Template dummyFact =
					this.defTemplate("dummy-fact", "used as default value for FACT-ADDRESS");
			final FactIdentifier dummyFactIdentifier =
					new org.jamocha.function.fwa.Assert(this,
							new TemplateContainer[] { new TemplateContainer(dummyFact) })
							.evaluate();
			for (final SlotType type : EnumSet.allOf(SlotType.class)) {
				switch (type) {
				case BOOLEAN:
					defaultValues.put(type, Boolean.FALSE);
					break;
				case DATETIME:
					defaultValues.put(type, ZonedDateTime.now());
					break;
				case DOUBLE:
					defaultValues.put(type, Double.valueOf(0.0));
					break;
				case LONG:
					defaultValues.put(type, Long.valueOf(0));
					break;
				case NIL:
					defaultValues.put(type, null);
					break;
				case STRING:
					defaultValues.put(type, "");
					break;
				case SYMBOL:
					defaultValues.put(type, this.getScope().getOrCreateSymbol("nil"));
					break;
				case FACTADDRESS:
					defaultValues.put(type, dummyFactIdentifier);
					break;
				}
			}
		}
	}

	@Override
	public Template defTemplate(String name, String description, Slot... slots) {
		TemplateMockup temp = new TemplateMockup(name, description, Arrays.asList(slots));
		templates.put(name, temp);
		return temp;
	}

	@Override
	public Template getTemplate(String name) {
		return templates.get(name);
	}

	@Override
	public Collection<Template> getTemplates() {
		return templates.values();
	}

	@Override
	public void defRules(Defrule... defrules) {
		for (Defrule defrule : defrules) {
			rules.put(defrule.getName(), defrule);
		}
	}

	@Override
	public Defrule getRule(String name) {
		return rules.get(name);
	}

	@Override
	public Collection<Defrule> getRules() {
		return rules.values();
	}

	@Override
	public Deffacts defFacts(String name, String description, TemplateContainer... containers) {
		return null;
	}

	@Override
	public Deffacts getDeffacts(String name) {
		return null;
	}

	@Override
	public Collection<Deffacts> getDeffacts() {
		return null;
	}

	@Override
	public void reset() {
	}

	@Override
	public void clear() {
		// TODO is this correct / enough ?
		this.templates.clear();
		this.rules.clear();
	}

	@Override
	public Map<FactIdentifier, MemoryFact> getMemoryFacts() {
		return null;
	}

	@Override
	public MemoryFact getMemoryFact(FactIdentifier id) {
		return null;
	}

	@Override
	public MemoryFactToFactIdentifier getMemoryFactToFactIdentifier() {
		return null;
	}

	@Override
	public FactIdentifier[] assertFacts(Fact... array) {
		return new FactIdentifier[] { null };
	}

	@Override
	public void retractFacts(FactIdentifier... array) {
	}

	@Override
	public LogFormatter getLogFormatter() {
		return null;
	}

	@Override
	public Logger getInteractiveEventsLogger() {
		return null;
	}

	@Override
	public TypedFilter getTypedFilter() {
		return null;
	}

	@Override
	public Template getInitialFactTemplate() {
		return initialFactTemplate;
	}

	@Override
	public ScopeStack getScope() {
		return scope;
	}

	@Override
	public void run(long maxNumRules) {
	}
}
