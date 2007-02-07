
import org.jamocha.rete.*;
import org.jamocha.parser.*;
import java.lang.String;

public class DefFunctionTree implements Function
{
	private Node actions=null;
	private String name,doc;
	
	public DefFunctionTree()
	{	
	};
	
	public JamochaValue executeFunction(Rete engine, Parameter[] params) throws EvaluationException
	{
		if (actions!=null) return actions.execute();
		else return JamochaValue.NIL;
	};

	public void setDocString(String d)
	{	doc=d;
	};

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
	};
	
	public void setAction (Node tree)
	{ actions=tree; };
};