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

import org.jamocha.dn.memory.MemoryHandler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Implementation of the {@link org.jamocha.dn.memory.CounterColumn} interface.
 * 
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 * @see org.jamocha.dn.memory.CounterColumn
 */
@Getter
@RequiredArgsConstructor
@ToString
public class CounterColumn implements org.jamocha.dn.memory.CounterColumn {
	/**
	 * Index of the {@link Counter} column in the storing {@link MemoryHandler}.
	 */
	final int index;
}
