/*
 * Copyright 2002-2008 Peter Lin & The Jamocha Team
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

package org.jamocha.engine.functions.java;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClassnameResolver {

	private final List<String> packages = new ArrayList<String>();

	private final List<String> classes = new ArrayList<String>();

	private static final Pattern classnamePattern = Pattern
			.compile("([\\w_][\\w_\\d]*\\.)*([\\w_][\\w_\\d]*)");

	private static final Pattern packagePattern = Pattern
			.compile("([\\w_][\\w_\\d]*\\.)+\\*");

	public ClassnameResolver() {
		packages.add("java.lang.*");
	}

	public void addImport(String s) throws ClassNotFoundException {
		if (classnamePattern.matcher(s).matches())
			classes.add(s);
		else if (packagePattern.matcher(s).matches())
			packages.add(s);
		else
			throw new ClassNotFoundException("The import \"" + s
					+ "\" is neither a valid class nor package name.");
	}

	public Class<?> resolveClass(String name) throws ClassNotFoundException {
		if (!isValidClassname(name))
			throw new ClassNotFoundException("\"" + name
					+ "\" is not a valid class name.");
		List<String> possibleNames = new ArrayList<String>();
		possibleNames.add(name);
		if (!isQualifiedClassname(name)) {
			for (String className : classes) {
				Matcher matcher = classnamePattern.matcher(className);
				if (matcher.group(2).equals(name))
					possibleNames.add(className);
			}
			for (String packageName : packages)
				possibleNames.add(packageName.replace("*", name));
		}
		for (String possibleClassName : possibleNames)
			try {
				return Class.forName(possibleClassName);
			} catch (ClassNotFoundException e) {
				/* just try the next name */
			}
		throw new ClassNotFoundException("Class \"" + name
				+ "\" could ot be found.");
	}

	public boolean isValidClassname(String s) {
		Matcher matcher = classnamePattern.matcher(s);
		return matcher.matches();
	}

	public boolean isQualifiedClassname(String s) {
		return isValidClassname(s) && s.contains(".");
	}

}
