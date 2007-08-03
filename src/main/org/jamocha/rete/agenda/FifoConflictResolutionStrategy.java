package org.jamocha.rete.agenda;

import java.util.List;

public class FifoConflictResolutionStrategy implements
		ConflictResolutionStrategy {

	@Override
	public void addActivation(List<Activation> activations, Activation a) {
		activations.add(a);
	}

	@Override
	public void removeActivation(List<Activation> activations, Activation a) {
		activations.remove(a);
	}

}
