/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jamocha.dn.memory.javaimpl;

import lombok.Value;

import org.jamocha.dn.memory.MemoryHandlerMain;
import org.jamocha.dn.memory.PathFilterElementToCounterColumn;

/**
 * Implementation of the {@link org.jamocha.dn.memory.MemoryHandlerMainAndCounterColumnMatcher}
 * interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */
@Value
public class MemoryHandlerMainAndCounterColumnMatcher implements
		org.jamocha.dn.memory.MemoryHandlerMainAndCounterColumnMatcher {
	MemoryHandlerMain memoryHandlerMain;
	PathFilterElementToCounterColumn filterElementToCounterColumn;
}
