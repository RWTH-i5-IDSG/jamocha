/** Class representing a CLIPS Symbol.
	Actually a long String
	
	@author Ory Chowaw-Liebman
	
*/
import java.lang.*;

public class CLIPSSymbol implements CLIPSData
{
	protected String value;
	
	public CLIPSSymbol(String val)
	{ value=val; };
	
	// Functions to determine datatype. Derived classes should override one to return True.
	public boolean isBool() 
	{ 
		if (value.toLowerCase()=="false" || value.toLowerCase()=="true") return true;
		return false; 
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
		if (value.toLowerCase()=="true") return true;
		return false; 
	};
	public long toInteger() { return (long) value.length(); };
	public double toFloat() { return (double) value.length(); };
	public String toSymbol() { return value; }; // Should strip Surrounding quotes
	public String toString() { return "\""+value+"\""; };
	public Object toExtAddress() { return this; };
//	public Object toFactAddress() { return NULL; };
	public String toInstance() { return "["+value+"]"; };
//	public Object toInstAddress() { return NULL; };
	
}