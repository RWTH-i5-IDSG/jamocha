
import org.jamocha.rete.*;
import org.jamocha.parser.*;
import java.lang.String;

public class DefFunctionTree implements Function
{
	private Node actions=null;
	private String name,doc;
	private DeffunctionParams [] myparams=null;
	private boolean varargs=false;
	
	public DefFunctionTree()
	{	
	}
	
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException
	{
		int i,j;
/*		if (varargs)
		{
			if (params.length<myparams.length-1) return JamochaValue.FALSE;
			for (i=0;i<myparams.length-1;i++) myparams[i].value=params[i].getValue(engine);
			j=params.length-myparams.length+1;
			JamochaValue [] v=new JamochaValue[j];
			for (i=myparams.length;i<params.length;i++) v[i-myparams.length]=params[i].getValue(engine);
			myparams[myparams.length-1].value=JamochaValue.newList(v);
		} else 
		{
			if (params.length>myparams.length) return JamochaValue.FALSE;
			for (i=0;i<myparams.length;i++) myparams[i].value=params[i].getValue(engine);
		}
		
*/
		// Multifield variables seem to by tricky in Jamocha
		if (params.length!=myparams.length) return JamochaValue.FALSE;
		for (i=0;i<myparams.length;i++) myparams[i].value=params[i].getValue(engine);
		if (actions!=null) return actions.execute();
		else return JamochaValue.NIL;
	}

	public void setDocString(String d)
	{	doc=d;
	}

	public String getDocString()
	{	return doc;	};

	public void  setName(String n)
	{	name=n;	};

	public String getName()
	{	return name;	};

	public String toPPString(Parameter[] params, int indents)
	{	return "Not yet implemented pretty print deffunction";	};
	
	public JamochaType getReturnType()
	{
		return JamochaType.UNDEFINED;
	}
	
	public void setAction (Node tree)
	{ 
		actions=tree; 
		if (myparams!=null) actions.bindLocals(myparams); 
	}

	public void setLocals(String [] locals,boolean hasMultifield)
	{ 
		int i;
		myparams=new DeffunctionParams[locals.length];
		varargs=hasMultifield;
		for (i=0;i<myparams.length;i++)
			myparams[i]=new DeffunctionParams(locals[i]);
		if (actions!=null) actions.bindLocals(myparams); 
	}
	
	// Call this once to hang the parameters to their nodes
//	public void bindLocals(void)
//	{ actions.binLocals(myparams);	}
};