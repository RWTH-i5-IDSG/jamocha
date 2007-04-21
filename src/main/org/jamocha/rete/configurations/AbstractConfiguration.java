package org.jamocha.rete.configurations;

import org.jamocha.rete.Fact;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;
import org.jamocha.rule.Rule;

public abstract class AbstractConfiguration  implements Parameter{

	public void setFact(Fact[] facts) {
	}

	public void configure(Rete engine, Rule util) {
	}

}
