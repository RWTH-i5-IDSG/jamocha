package org.jamocha.rete.eventhandling;

import org.jamocha.rete.modules.Module;
import org.jamocha.rete.wme.Fact;
import org.jamocha.rete.wme.Template;
import org.jamocha.rule.Rule;

public class ModuleChangedEvent {
	
	public Module module;
	public Fact fact;
	public Rule rule;
	public Template template;
	
}
