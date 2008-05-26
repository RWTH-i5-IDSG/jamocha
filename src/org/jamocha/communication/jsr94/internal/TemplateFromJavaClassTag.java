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
package org.jamocha.communication.jsr94.internal;

import org.jamocha.engine.workingmemory.elements.tags.Tag;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de> This tag can hold
 *         additional information in the case, that the template is generated
 *         from a java class. This is important for re-converting facts to
 *         java-objects after evaluating the rules.
 */
public class TemplateFromJavaClassTag implements Tag {

	private final Class<? extends Object> javaClass;

	private final Template2JavaClassAdaptor adaptor;

	public TemplateFromJavaClassTag(final Class<? extends Object> javaClass,
			final Template2JavaClassAdaptor adaptor) {
		this.javaClass = javaClass;
		this.adaptor = adaptor;
	}

	public Class<? extends Object> getJavaClass() {
		return javaClass;
	}

	public Template2JavaClassAdaptor getAdaptor() {
		return adaptor;
	}

}
