package org.jamocha.filter;

import java.util.HashMap;
import lombok.EqualsAndHashCode;

public class TODODatenkrakeFunktionen {

	@EqualsAndHashCode
	class CombinedClipsAndParams{
		final String inClips;
		final SlotType[] params;
		public CombinedClipsAndParams(String inClips, SlotType[] params) {
			super();
			this.inClips = inClips;
			this.params = params;
		}
		
	}
	
	HashMap<CombinedClipsAndParams, Predicate> clipsFunctions = new HashMap<>();
	
}
