package org.jamocha.rete.configurations;

import java.util.ArrayList;
import java.util.List;

public class TemplateConditionConfiguration extends ConditionConfiguration {

    protected String templateName = null;

    protected String slotName = null;

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

}
