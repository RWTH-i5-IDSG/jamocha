package org.jamocha.rete.eventhandling;


/**
 * @author Josef Alexander Hahn
 */
public interface ModuleChangedListener {
	
	public void ruleAdded(ModuleChangedEvent ev);
	
	public void ruleRemoved(ModuleChangedEvent ev);
	
	public void templateAdded(ModuleChangedEvent ev);
	
	public void templateRemoved(ModuleChangedEvent ev);
	
	public void factAdded(ModuleChangedEvent ev);
	
	public void factRemoved(ModuleChangedEvent ev);

	
}
