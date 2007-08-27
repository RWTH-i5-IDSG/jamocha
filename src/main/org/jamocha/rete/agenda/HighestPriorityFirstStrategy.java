package org.jamocha.rete.agenda;

public class HighestPriorityFirstStrategy extends ConflictResolutionStrategy {

	public int compare(Activation act1, Activation act2) {
		int sal1 = act1.getRule().getSalience();
		int sal2 = act2.getRule().getSalience();
		if (sal1 == sal2) {
			// for same salience the decision is arbitrary
			return 0;
		} else if (sal1 > sal2) {
			return -1;
		} else {
			return 1;
		}
	}

	@Override
	public String getName() {
		return getNameStatic();
	}

	public static String getNameStatic() {
		return "HighestPriorityFirstStrategy";
	}

}
