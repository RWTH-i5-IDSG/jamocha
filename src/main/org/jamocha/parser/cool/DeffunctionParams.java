// Simple class to keep a parameter and a name
package org.jamocha.parser.cool;

import org.jamocha.parser.*;
import java.lang.String;

public class DeffunctionParams
{
	public String name;
	public JamochaValue value=JamochaValue.NIL;
	
	public DeffunctionParams(String n)
	{ name=n; }
}