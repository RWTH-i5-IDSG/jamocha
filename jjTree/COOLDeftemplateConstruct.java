/* Generated By:JJTree: Do not edit this line. COOLDefTemplateConstruct.java */

import org.jamocha.rete.*;
import org.jamocha.parser.*;
import java.util.ArrayList;

public class COOLDeftemplateConstruct extends ConstructNode {
	private ArrayList<AbstractSlot> slots;
		
	public COOLDeftemplateConstruct(int id) {
		super(id);
		slots=new ArrayList<AbstractSlot>();
	}

	public COOLDeftemplateConstruct(COOL p, int id) {
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
	public JamochaValue execute() //throws EvaluationException
	{
	    Slot [] s = new Slot[slots.size()];
		slots.toArray(s);
		Deftemplate tpl = new Deftemplate(name,null,s);
		Rete engine=parser.getRete();
		Module mod = tpl.checkName(engine);
		if (mod == null) mod = engine.getCurrentFocus();
		mod.addTemplate(tpl, engine, engine.getWorkingMemory());
		return JamochaValue.TRUE;
	};
}
