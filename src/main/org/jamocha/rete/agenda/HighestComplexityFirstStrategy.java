package org.jamocha.rete.agenda;

import java.util.List;

public class HighestComplexityFirstStrategy extends ConflictResolutionStrategy {

	@Override
	public void addActivation(List<Activation> activations, Activation a) {
		int complexity = a.getRule().getTotalComplexity();
		for (int i = 0; i < activations.size(); ++i) {
			if(complexity > activations.get(i).getRule().getComplexity()) {
				activations.add(i,a);
				return;
			}
		}
		activations.add(a);
	}

	@Override
	public String getName() {
		return "HighestComplexityFirstStrategy";
	}

	@Override
	public void removeActivation(List<Activation> activations, Activation a) {
		activations.remove(a);
	}

	public static String getNameStatic() {
		return "HighestComplexityFirstStrategy";
	}

}
