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

package org.jamocha.communication.events;

/**
 * @author Josef Alexander Hahn
 */
public interface ModuleChangedListener {

	public void ruleAdded(ModuleChangedEvent ev);

	public void ruleRemoved(ModuleChangedEvent ev);

	public void templateAdded(ModuleChangedEvent ev);

	public void templateRemoved(ModuleChangedEvent ev);

	public void factAdded(ModuleChangedEvent ev);

	public void factRemoved(ModuleChangedEvent ev);

}
