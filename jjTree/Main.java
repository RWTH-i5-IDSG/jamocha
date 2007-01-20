/** Main class for COOL grammar based parser: A test program for the parser just
 *	dumping all the nodes to stdout.
 *@author Ory Chowaw-Liebman
 *@author Ulrich Loup
 *@version 01-12-2007
 */

public class Main
{
	public static void main(String args[])
	{
		boolean verbose = (args != null && args.length == 1 && "verbose".equals(args[0]));
		System.out.println("+-----------------------------------+");
		System.out.println("|Simple COOL Parser Test Environment|");
		System.out.println("+-----------------------------------+\n");
		if(!verbose)System.out.println("Note: For verbose output type \"java Main verbose\".\n");
		System.out.print("COOL> ");
		COOL p = new COOL(System.in);
		p.initFunctions();
		try
		{
			while (true)
			{
				COOLStart n = p.Start();
				System.out.println("Parsed! Dumping Syntax-Tree:");
				n.dump("> ");
				System.out.print("\nCOOL> ");
				//System.out.println(n.execute().toString());
			}
		}
		catch (Exception e)
		{
			System.err.println("ERROR: " + e.getMessage());
			if(verbose)e.printStackTrace();
		}
	}
}
