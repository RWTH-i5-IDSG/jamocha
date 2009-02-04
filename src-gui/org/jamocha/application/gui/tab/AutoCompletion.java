/*
 * Copyright 2002-2008 The Jamocha Team
 * 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.application.gui.tab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AutoCompletion {

	protected final int cacheSize = 20;

	protected Map<String, String> fullText;

	protected Vector<String> tokens;

	protected Vector<String>[] cache;

	protected String[] cachePrefixes;

	protected int[] useCounter;

	@SuppressWarnings("unchecked")
	public AutoCompletion() {
		tokens = new Vector<String>();
		fullText = new HashMap<String, String>();
		cache = new Vector[cacheSize];
		cachePrefixes = new String[cacheSize];
		useCounter = new int[cacheSize];
		for (int i = 0; i < cacheSize; i++) {
			useCounter[i] = 0;
		}

	}

	public void addToken(final String token, final String fullText) {
		addToken(token);
		this.fullText.put(token, fullText);
	}

	public void addToken(final String token) {
		int i = 0;
		while (i < tokens.size()) {
			if (token.compareTo(tokens.get(i)) < 0) {
				break;
			}
			i++;
		}
		tokens.add(i, token);
	}

	protected Vector<String> getAllBeginningWith(final List<String> s,
			final String prefix) {
		final Vector<String> result = new Vector<String>();
		for (final String str : s) {
			if (str.startsWith(prefix)) {
				result.add(str);
			}
		}
		return result;
	}

	public String getFullText(final String token) {
		final String res = fullText.get(token);
		if (res == null) {
			return token;
		}
		return res;
	}

	public Vector<String> getAllBeginningWith(final String prefix) {
		// search in cache
		int found = -1;
		String pf = prefix;
		List<String> weUseList = null;
		while (found < 0 && pf.length() > 0) {
			for (int i = 0; i < cacheSize; i++) {
				if (cachePrefixes[i] != null && cachePrefixes[i].equals(pf)) {
					found = i;
					break;
				}
			}
			pf = pf.substring(0, pf.length() - 1);
		}

		if (found > 0) {
			useCounter[found]++;
			weUseList = cache[found];
		} else {
			weUseList = tokens;
		}

		final Vector<String> result = getAllBeginningWith(weUseList, prefix);

		// cache the result
		int minuse = Integer.MAX_VALUE;
		int minuseindex = -1;
		for (int i = 0; i < cacheSize; i++) {
			if (useCounter[i] < minuse) {
				minuse = useCounter[i];
				minuseindex = i;
			}
		}

		cache[minuseindex] = result;
		cachePrefixes[minuseindex] = prefix;
		useCounter[minuseindex] = 1;

		return result;
	}
}
