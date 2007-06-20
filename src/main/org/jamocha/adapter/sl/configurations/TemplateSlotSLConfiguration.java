package org.jamocha.adapter.sl.configurations;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TemplateSlotSLConfiguration implements SLConfiguration {

	private SLConfiguration templateName;

	private Map<SLConfiguration, SLConfiguration> slots = new HashMap<SLConfiguration, SLConfiguration>();

	public Map<SLConfiguration, SLConfiguration> getSlots() {
		return slots;
	}

	public void addSlot(SLConfiguration name, SLConfiguration value) {
		this.slots.put(name, value);
	}

	public SLConfiguration getTemplateName() {
		return templateName;
	}

	public void setTemplateName(SLConfiguration templateName) {
		this.templateName = templateName;
	}

	public String compile(SLCompileType compileType) {
		StringBuilder res = new StringBuilder();
		Set<SLConfiguration> keys = slots.keySet();
		switch (compileType) {
		case ACTION_AND_ASSERT:
			res.append("(").append(templateName.compile(compileType));
			for (SLConfiguration key : keys) {
				if (slots.get(key) != null) {
					res.append(" ");
					res.append(slots.get(key).compile(SLCompileType.ASSERT));
				}
			}
			res.append(")");
			break;
		case ASSERT:
			res.append("(assert (").append(templateName.compile(compileType));
			for (SLConfiguration key : keys) {
				res.append(" (").append(key.compile(compileType)).append(" ");
				if (slots.get(key) != null)
					res.append(slots.get(key).compile(compileType));
				res.append(")");
			}
			res.append("))");
			break;
		case RULE_LHS:
			res.append("(").append(templateName.compile(compileType));
			for (SLConfiguration key : keys) {
				res.append(" (").append(key.compile(compileType)).append(" ");
				if (slots.get(key) != null)
					res.append(slots.get(key).compile(compileType));
				else
					res.append("NIL");
				res.append(")");
			}
			res.append(")");
			break;
		}
		return res.toString();
	}

}
