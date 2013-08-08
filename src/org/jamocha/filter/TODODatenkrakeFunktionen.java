package org.jamocha.filter;

import java.util.HashMap;

import lombok.EqualsAndHashCode;

import org.jamocha.filter.impls.Predicates;

public class TODODatenkrakeFunktionen {

	@EqualsAndHashCode
	public static class CombinedClipsAndParams {
		final String inClips;
		final SlotType[] params;

		public CombinedClipsAndParams(String inClips, SlotType[] params) {
			super();
			this.inClips = inClips;
			this.params = params;
		}

	}

	public static HashMap<CombinedClipsAndParams, Function> clipsFunctions = new HashMap<>();

	static {
		addImpl(Predicates.class);
		// hier weitere funktionspakete angeben
	}

	public static void addImpl(Class<?> clazz) {
		try {
			Class.forName(clazz.getName());
		} catch (ClassNotFoundException e) {
			System.err.println(e);
		}
	}

	public static void addImpl(final Function impl) {
		clipsFunctions.put(
				new CombinedClipsAndParams(impl.inClips(), impl.paramTypes()),
				impl);
	}
}
