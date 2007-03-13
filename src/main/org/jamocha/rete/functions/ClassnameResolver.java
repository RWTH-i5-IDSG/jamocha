package org.jamocha.rete.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jamocha.rete.Rete;

public class ClassnameResolver {

	private List<String> packages = new ArrayList<String>();

	private List<String> classes = new ArrayList<String>();

	private static final Pattern classnamePattern = Pattern
			.compile("([\\w_][\\w_\\d]*\\.)*([\\w_][\\w_\\d]*)");

	private static final Pattern packagePattern = Pattern
			.compile("([\\w_][\\w_\\d]*\\.)+\\*");

	public ClassnameResolver(Rete engine) {
		packages.add("java.lang.*");
	}

	public void addImport(String s) throws ClassNotFoundException {
		if (classnamePattern.matcher(s).matches()) {
			classes.add(s);
		} else if (packagePattern.matcher(s).matches()) {
			packages.add(s);
		} else {
			throw new ClassNotFoundException("The import \"" + s
					+ "\" is neither a valid class nor package name.");
		}
	}

	public Class<?> resolveClass(String name) throws ClassNotFoundException {
		if (!isValidClassname(name)) {
			throw new ClassNotFoundException("\"" + name
					+ "\" is not a valid class name.");
		}
		List<String> possibleNames = new ArrayList<String>();
		possibleNames.add(name);
		if (!isQualifiedClassname(name)) {
			for (String className : classes) {
				Matcher matcher = classnamePattern.matcher(className);
				if (matcher.group(2).equals(name)) {
					possibleNames.add(className);
				}
			}
			for (String packageName : packages) {
				possibleNames.add(packageName.replace("*", name));
			}
		}
		for (String possibleClassName : possibleNames) {
			try {
				return Class.forName(possibleClassName);
			} catch (ClassNotFoundException e) {
				/* just try the next name */
			}
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
