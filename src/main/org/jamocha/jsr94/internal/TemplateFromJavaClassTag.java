package org.jamocha.jsr94.internal;

import org.jamocha.rete.wme.tags.Tag;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 * This tag can hold additional information in the case,
 * that the template is generated from a java class.
 * This is important for re-converting facts to java-objects
 * after evaluating the rules.
 */
public class TemplateFromJavaClassTag implements Tag {

	private Class javaClass;
	
	private Template2JavaClassAdaptor adaptor;
	
	public TemplateFromJavaClassTag(Class javaClass, Template2JavaClassAdaptor adaptor) {
		this.javaClass = javaClass;
		this.adaptor = adaptor;
	}

	public Class getJavaClass() {
		return javaClass;
	}

	public Template2JavaClassAdaptor getAdaptor() {
		return adaptor;
	}
	
}
