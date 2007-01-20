/** Class representing a CLIPS constant type.
	CLIPS defines 8 datatypes:
	  -Integer
	  -Float
	  -Symbol
	  -String
	  -External Address
	  -Fact Address
	  -Instance Name
	  -Instance Address
	Derived Types should only be able to set the represented value through the Constructor.
	@author Ory Chowaw-Liebman
	
*/
import java.lang.*;

public interface CLIPSData
{
	// Functions to determine datatype. Derived classes should override one to return True.
	abstract public boolean isInteger() ;
	abstract public boolean isFloat();
	abstract public boolean isSymbol();
	abstract public boolean isString();
	abstract public boolean isExtAddress();
	abstract public boolean isFactAddress();
	abstract public boolean isInstance();
	abstract public boolean isInstAddress();
	abstract public boolean isBool();
	
	// Functions to to Data try to cast to type to return if not the one represented
	abstract public boolean toBool();
	abstract public long toInteger();
	abstract public double toFloat();
	abstract public String toSymbol();
	abstract public String toString();
	abstract public Object toExtAddress();
//	abstract public Object toFactAddress();
	abstract public String toInstance();
//	abstract public Object toInstAddress();
	
}