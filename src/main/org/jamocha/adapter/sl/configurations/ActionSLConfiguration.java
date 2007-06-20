package org.jamocha.adapter.sl.configurations;

public class ActionSLConfiguration implements SLConfiguration {

	private SLConfiguration agent;

	private SLConfiguration action;

	public SLConfiguration getAction() {
		return action;
	}

	public void setAction(SLConfiguration action) {
		this.action = action;
	}

	public SLConfiguration getAgent() {
		return agent;
	}

	public void setAgent(SLConfiguration agent) {
		this.agent = agent;
	}

	public String compile(SLCompileType compileType) {
		if (compileType == SLCompileType.ASSERT) {
			return action.compile(SLCompileType.ACTION_AND_ASSERT);
		}
		return action.compile(compileType);
	}

}
