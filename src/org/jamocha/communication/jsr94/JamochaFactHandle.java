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

package org.jamocha.communication.jsr94;

import javax.rules.Handle;

public class JamochaFactHandle implements Handle {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	long id;
	
	public JamochaFactHandle(long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return (int) (id % Integer.MAX_VALUE);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JamochaFactHandle other = (JamochaFactHandle) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public long getId() {
		return id;
	}
	
	

}
