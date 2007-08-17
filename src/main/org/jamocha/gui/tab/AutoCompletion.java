package org.jamocha.gui.tab;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AutoCompletion {
	
	protected final int cacheSize = 20;
	protected Vector<String> tokens;
	protected Vector<String>[] cache;
	protected String[] cachePrefixes;
	protected int[] useCounter;
	
	public AutoCompletion() {
		tokens = new Vector<String>();
		cache = new Vector[cacheSize];
		cachePrefixes = new String[cacheSize];
		useCounter = new int[cacheSize];
		for (int i=0; i<cacheSize;i++) useCounter[i]=0;
		
	}
	
	public void addToken(String token) {
		int i = 0;
		while (i < tokens.size()) {
			if (token.compareTo(tokens.get(i)) <0) break;
			i++;
		}
		tokens.add(i,token);
	}
	
	
	protected Vector<String> getAllBeginningWith(List<String> s, String prefix) {
		Vector<String> result = new Vector<String>();
		for (String str : s) {
			if (str.startsWith(prefix)) result.add(str);
		}
		return result;
	}

	public Vector<String> getAllBeginningWith(String prefix) {
		// search in cache
		int found = -1;
		String pf = prefix;
		List<String> weUseList = null;
		while (found < 0 && pf.length() > 0) {
			for (int i=0; i<cacheSize;i++) {
				if (cachePrefixes[i] != null && cachePrefixes[i].equals(pf)) {
					found = i;
					break;
				}
			}
			pf = pf.substring(0,pf.length()-1);
		}
		
		if (found > 0) {
			useCounter[found]++;
			weUseList = cache[found];
		} else {
			weUseList = tokens;
		}
		
		Vector<String> result = getAllBeginningWith(weUseList,prefix);
		
		// cache the result
		int minuse=Integer.MAX_VALUE;
		int minuseindex=-1;
		for (int i=0; i<cacheSize;i++) {
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
