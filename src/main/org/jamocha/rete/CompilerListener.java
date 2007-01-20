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
 * @author Peter Lin
 *
 * In the event we want to listen to events in the RuleCompiler, a class
 * should implement the interface and add itself to the RuleCompiler. When
 * a rule is added/removed/updated, an event will be fired.
 */
public interface CompilerListener extends Serializable {
    void ruleAdded(CompileEvent event);
    void ruleRemoved(CompileEvent event);
    void compileError(CompileEvent event);
}
