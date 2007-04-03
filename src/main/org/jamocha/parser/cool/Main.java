/** Main class for CLIPS-SL grammar based parser.
*/
package org.jamocha.parser.cool;

import java.io.*;
import org.jamocha.parser.*;

public class Main
{
	public static void main(String args[]) {
		System.out.println("Reading from standard input...");
		COOLParser p = new COOLParser(System.in);
//		p.initialize();
		try {
			while (true)
			{
				COOLStart n = p.Start();
				if (n==null) System.exit(0);
				n.dump("");
				System.out.println(n.getExpression().toString());
			}
		} catch (Exception e) {
		System.err.println("Oops.");
		//System.err.println(e.getMessage());
		e.printStackTrace();
    }
  }
}
