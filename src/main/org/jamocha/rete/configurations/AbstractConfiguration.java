package org.jamocha.rete.configurations;

import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rete.wme.Fact;
import org.jamocha.rule.Rule;

public abstract class AbstractConfiguration implements Parameter{

	public void setFact(Fact[] facts) {
	}

	public void configure(Rete engine, Rule util) {
	}
	
	public Object clone() throws CloneNotSupportedException{
		throw new CloneNotSupportedException();
	}

}
