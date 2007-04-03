/* Generated By:JJTree: Do not edit this line. COOLDefTemplateConstruct.java */
package org.jamocha.parser.cool;

import org.jamocha.rete.*;
import org.jamocha.rete.functions.DeftemplateFunction;
import org.jamocha.parser.*;
import java.util.ArrayList;

public class COOLDeftemplateConstruct extends ConstructNode {
	private ArrayList<AbstractSlot> slots;
	
	private final static DeftemplateFunction deftemplateFunction = new DeftemplateFunction();
		
	public COOLDeftemplateConstruct(int id) {
		super(id);
		slots=new ArrayList<AbstractSlot>();
	}

	public COOLDeftemplateConstruct(COOLParser p, int id) {
		super(p, id);
		slots=new ArrayList<AbstractSlot>();
	}

	public String toString() {
		String str="deftemplate \"" + name + "\"" + "(" + doc + "): ";
		int i;
		for (i=0;i<slots.size();i++) str=str+slots.get(i).getName()+" ";
		return str;
	}

	public void addSlot(AbstractSlot s)
	{	
		s.setId(slots.size());
		slots.add(s);
	}
	public JamochaValue getValue(Rete engine) throws EvaluationException
	{
		TemplateSlot [] s = new TemplateSlot[slots.size()];
		slots.toArray(s);
		Deftemplate tpl = new Deftemplate(name,null,s);
		engine.addTemplate(tpl);
		return JamochaValue.TRUE;
	};
}
