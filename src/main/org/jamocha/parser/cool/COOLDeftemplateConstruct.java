/* Generated By:JJTree: Do not edit this line. COOLDefTemplateConstruct.java */
package org.jamocha.parser.cool;

import java.util.ArrayList;

import org.jamocha.parser.JamochaValue;
import org.jamocha.rete.AbstractSlot;
import org.jamocha.rete.Deftemplate;
import org.jamocha.rete.FunctionParam2;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.TemplateSlot;
import org.jamocha.rete.functions.DeftemplateFunction;

public class COOLDeftemplateConstruct extends ConstructNode {
    private ArrayList<AbstractSlot> slots;

    public COOLDeftemplateConstruct(int id) {
	super(id);
	slots = new ArrayList<AbstractSlot>();
    }

    public COOLDeftemplateConstruct(COOLParser p, int id) {
	super(p, id);
	slots = new ArrayList<AbstractSlot>();
    }

    public String toString() {
	String str = "(deftemplate " + name + " \"" + doc + "\" ";
	int i;
	for (i = 0; i < slots.size(); i++)
	    str = str + "(slot " + slots.get(i).getName() + ") ";
	str = str + ")";
	return str;
    }

    public void addSlot(AbstractSlot s) {
	s.setId(slots.size());
	slots.add(s);
    }

    public Parameter getExpression() {
	TemplateSlot[] s = new TemplateSlot[slots.size()];
	slots.toArray(s);
	Deftemplate tpl = new Deftemplate(name, null, s);
	FunctionParam2 defTemplate = new FunctionParam2();
	defTemplate.setFunctionName(DeftemplateFunction.NAME);
	defTemplate.setParameters(new Parameter[] { JamochaValue.newObject(tpl) });
	return defTemplate;
    }
}
