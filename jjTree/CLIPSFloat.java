/** Class representing a CLIPS Float.
	Actually a double (64 Bits)
	
	@author Ory Chowaw-Liebman
	
*/
import java.lang.*;

public class CLIPSFloat implements CLIPSData
{
	protected double value;
	
	public CLIPSFloat(double val)
	{ value=val; };
	
	// Functions to determine datatype. Derived classes should override one to return True.
	public boolean isBool() { return false; };
	public boolean isFloat() { return true; };
	public boolean isInteger() { return false; };
	public boolean isSymbol() { return false; };
	public boolean isString() { return false; };
	public boolean isExtAddress() { return false; };
	public boolean isFactAddress() { return false; };
	public boolean isInstance() { return false; };
	public boolean isInstAddress() { return false; };
	
	// Functions to to Data try to cast to type to return if not the one represented .
	// (Or return NULL if cthe conversion is not sensible.)
	public boolean toBool() { return false; };
	public long toInteger() { return (long) value; };
	public double toFloat() { return value; };
	public String toSymbol() { return "number"+Double.toString(value); };
	public String toString() { return "\""+Double.toString(value) +"\""; };
	public Object toExtAddress() { return this; };
//	public Object toFactAddress() { return NULL; };
	public String toInstance() { return "["+Double.toString(value)+"]"; };
//	public Object toInstAddress() { return NULL; };
	
}