package org.jamocha.rete.eventhandling;

import org.jamocha.rete.modules.Module;

/**
 * @author Josef Hahn
 */
public interface ModulesChangeListener {
	public void evModuleAdded(Module newModule);
	public void evModuleRemoved(Module oldModule);
}
