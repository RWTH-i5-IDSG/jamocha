package org.jamocha.communication.jsr94;

import javax.rules.Handle;

import org.jamocha.parser.JamochaValue;

public class JamochaFactHandle implements Handle {

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
