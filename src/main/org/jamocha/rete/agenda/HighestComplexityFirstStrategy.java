package org.jamocha.rete.agenda;

public class HighestComplexityFirstStrategy extends ConflictResolutionStrategy {

	public int compare(Activation act1, Activation act2) {
		int sal1 = act1.getRule().getSalience();
		int sal2 = act2.getRule().getSalience();
		if (sal1 == sal2) {
			long compl1 = act1.getRule().getTotalComplexity();
			long compl2 = act2.getRule().getTotalComplexity();
			if (compl1 > compl2) {
				return -1;
			} else if (compl1 < compl2) {
				return 1;
			} else {
				return 0;
			}
		} else if (sal1 < sal2) {
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
		return "HighestComplexityFirstStrategy";
	}

}
