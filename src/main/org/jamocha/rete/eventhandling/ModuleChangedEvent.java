package org.jamocha.rete.eventhandling;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Module;
import org.jamocha.rete.Template;
import org.jamocha.rule.Rule;

public class ModuleChangedEvent {
	
	public Module module;
	public Fact fact;
	public Rule rule;
	public Template template;
	
}
