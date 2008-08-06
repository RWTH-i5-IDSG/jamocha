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

package org.jamocha.engine.workingmemory.elements.tags;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 * Tags are used to attach additional informations to facts.
 * For example, you can attach a tag which stores the
 * java class, which corresponds to the templase (for jsr94) or
 * a datasource, if the fact is generated from a jdbc source.
 */
public interface Tag {
	
}