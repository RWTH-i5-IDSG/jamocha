/** Class representing a CLIPS Instance.
	Actually a string which is always between [ and ]
	
	@author Ory Chowaw-Liebman
	
*/
import java.lang.*;

public class CLIPSInstance implements CLIPSData
{
	protected String value;
	
	public CLIPSInstance(String val)
	{ value=val; };
	
	// Functions to determine datatype. Derived classes should override one to return True.
	public boolean isBool() { return false; };
	public boolean isInstance() { return true; };
	public boolean isInteger() { return false; };
	public boolean isFloat() { return false; };
	public boolean isSymbol() { return false; };
	public boolean isString() { return false; };
	public boolean isExtAddress() { return false; };
	public boolean isFactAddress() { return false; };
	public boolean isInstAddress() { return false; };
	
	// Functions to to Data try to cast to type to return if not the one represented .
	// (Or return NULL if cthe conversion is not sensible.)
	public boolean toBool() { return false; };
	public long toInteger() { return (long) value.length(); };
	public double toFloat() { return (double) value.length(); };
	public String toSymbol() { return value; }; // Should strip Surrounding braces
	public String toString() { return "\""+value+"\""; };
	public Object toExtAddress() { return this; };
//	public Object toFactAddress() { return NULL; };
	public String toInstance() { return value; };
//	public Object toInstAddress() { return NULL; };
	
}
