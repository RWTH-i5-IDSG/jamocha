package org.jamocha.rete.agenda;

import java.util.List;

/**
 * @author Josef Alexander Hahn
 */
public interface ConflictResolutionStrategy {

	public void addActivation(List<Activation> activations, Activation a);
	
	public void removeActivation(List<Activation> activations, Activation a);
	
	
}
