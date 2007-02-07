/** Simple Parameter Class
*/

import java.lang.String;
import org.jamocha.parser.*;
import org.jamocha.rete.Parameter;
import org.jamocha.rete.Rete;

public class COOLParameter implements Parameter
{

	private JamochaValue value=null;

	public String getParameterString() 
	{
		return value.toString();
	};

	public boolean isObjectBinding() { return false; };
	
	public JamochaValue getValue(Rete engine) 
	{	return value; };
	
	public void setValue(JamochaValue v)
	{ 
		value=v; 
	};
};
