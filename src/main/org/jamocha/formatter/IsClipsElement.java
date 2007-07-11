package org.jamocha.formatter;

/**
 * an interface which should be implemented by all classes,
 * which shapes a clips language element. it is used for getting
 * their string representation.
 * @author jupp
 *
 */
public interface IsClipsElement {

	int blanksPerIndent = 3;
	public String toClipsFormat(int indent);
	
	
}
