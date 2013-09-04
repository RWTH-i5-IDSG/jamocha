/*
 * Copyright 2002-2013 The Jamocha Team
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

package org.jamocha.dn.nodes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.jamocha.dn.memory.FactAddress;
import org.jamocha.dn.nodes.Node.Edge;

/**
 * @author Fabian Ohler <fabian.ohler1@rwth-aachen.de>
 */

@Getter
@RequiredArgsConstructor
public class AddressPredecessor {
	final Edge edge;
	final FactAddress address;
}
