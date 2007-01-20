/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.io.Serializable;

/**
 * @author Peter Lin</p>
 * 
 * Query interface is designed to execute queries to external
 * data sources. The design of the interface expects the system will
 * attempt to load an object matching the given parameters.
 */
public interface Query extends Serializable {
	/**
	 * Since the query is triggered by backward chaining nodes, the
	 * method takes the template name, slot name and values.
	 * @param template
	 * @param params
	 * @return
	 */
	Object executeQuery(Template template, Slot[] params);
	/**
	 * Query adapters that implement the method need to declare which
	 * classes it supports.
	 * @return
	 */
	Template[] supportedTemplates();
}
