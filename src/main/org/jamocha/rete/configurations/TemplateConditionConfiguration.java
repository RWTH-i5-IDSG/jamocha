package org.jamocha.rete.configurations;

public class TemplateConditionConfiguration extends ConditionConfiguration {

    protected String templateName = null;

    protected String slotName = null;
    
    protected ConstraintConfiguration[] constraints = null;

    public String getTemplateName() {
        return templateName;
    }

    public String getSlotName() {
        return slotName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

	public ConstraintConfiguration[] getConstraints() {
		return constraints;
	}

	public void setConstraints(ConstraintConfiguration[] constraints) {
		this.constraints = constraints;
	}

}
