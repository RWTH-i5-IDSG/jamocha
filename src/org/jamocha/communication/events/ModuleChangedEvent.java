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

package org.jamocha.communication.events;

import org.jamocha.engine.modules.Module;
import org.jamocha.engine.workingmemory.elements.Template;
import org.jamocha.rules.Rule;

/**
 * @author Josef Alexander Hahn
 */
public class ModuleChangedEvent extends AbstractEvent {

	protected Module module;
	protected Rule rule;
	protected Template template;

	public ModuleChangedEvent(Object source, Module module, Template template) {
		super(source);
		this.module = module;
		this.template = template;
	}

	public ModuleChangedEvent(Object source, Module module, Rule rule) {
		super(source);
		this.module = module;
		this.rule = rule;
	}

	public Module getModule() {
		return module;
	}

	public Rule getRule() {
		return rule;
	}

	public Template getTemplate() {
		return template;
	}
}
