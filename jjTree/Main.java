/** Main class for CLIPS-SL grammar based parser.
*/

import java.io.*;
import org.jamocha.parser.*;

public class Main
{
	public static void main(String args[]) {
		System.out.println("Reading from standard input...");
		COOL p = new COOL(System.in);
//		p.initialize();
		try {
			while (true)
			{
				COOLStart n = p.Start();
				if (n==null) System.exit(0);
				n.dump("");
				//System.out.println(n.execute().toString());
			}
		} catch (Exception e) {
		System.err.println("Oops.");
		System.err.println(e.getMessage());
		e.printStackTrace();
    }
  }
}
