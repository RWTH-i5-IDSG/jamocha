/** Class representing a CLIPS Symbol.
	Actually a long String
	
	@author Ory Chowaw-Liebman
	
*/
import java.lang.*;

public class CLIPSBool implements CLIPSData
{
	protected boolean value;
	
	public CLIPSBool(boolean val)
	{ value=val; };
	
	// Functions to determine datatype. Derived classes should override one to return True.
	public boolean isBool() 
	{ 
		return true; 
	};
	public boolean isSymbol() { return true; };
	public boolean isInteger() { return false; };
	public boolean isFloat() { return false; };
	public boolean isString() { return false; };
	public boolean isExtAddress() { return false; };
	public boolean isFactAddress() { return false; };
	public boolean isInstance() { return false; };
	public boolean isInstAddress() { return false; };
	
	// Functions to to Data try to cast to type to return if not the one represented .
	// (Or return NULL if cthe conversion is not sensible.)
	public boolean toBool() 
	{ 
		return value; 
	};
	public long toInteger() 
	{ 
		if (value) return 1;
		else return 0;
	};
	public double toFloat()
	{
		if (value) return 1.0;
		else return 0.0;
	};
	public String toSymbol() 
	{
		if (value) return "true";
		else return "false";
	};
	public String toString() { return"\"" + toSymbol() +"\""; };
	public Object toExtAddress() { return this; };
//	public Object toFactAddress() { return NULL; };
	public String toInstance() { return "["+toString() +"]"; };
//	public Object toInstAddress() { return NULL; };
	
}