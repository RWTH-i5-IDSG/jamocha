/** Class representing a CLIPS Integer.
	Actually a long (64 Bits)
	
	@author Ory Chowaw-Liebman
	
*/
import java.lang.*;

public class CLIPSInteger implements CLIPSData
{
	protected long value;
	
	public CLIPSInteger(long val)
	{ value=val; };
	
	// Functions to determine datatype. Derived classes should override one to return True.
	public boolean isBool() { return false; };
	public boolean isInteger() { return true; };
	public boolean isFloat() { return false; };
	public boolean isSymbol() { return false; };
	public boolean isString() { return false; };
	public boolean isExtAddress() { return false; };
	public boolean isFactAddress() { return false; };
	public boolean isInstance() { return false; };
	public boolean isInstAddress() { return false; };
	
	// Functions to to Data try to cast to type to return if not the one represented .
	// (Or return NULL if cthe conversion is not sensible.)
	public boolean toBool() { return false; };
	public long toInteger() { return value; };
	public double toFloat() { return (double) value; };
	public String toSymbol() { return "number"+Long.toString(value); };
	public String toString() { return "\""+Long.toString(value) +"\""; };
	public Object toExtAddress() { return this; };
//	public Object toFactAddress() { return NULL; };
	public String toInstance() { return "["+Long.toString(value) +"]"; };
//	public Object toInstAddress() { return NULL; };
	
}