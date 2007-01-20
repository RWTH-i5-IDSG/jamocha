/* Generated By:JJTree: Do not edit this line. CLIPS_SLMultiVariable.java */
/** AST Node representing a Symbol.
	@author jjTree
	@author Ory Chowaw-Liebman
*/

public class COOLMultiVariable extends SimpleNode {

	public COOLMultiVariable(int id) {
		super(id);
	}

	public COOLMultiVariable(COOL p, int id) {
		super(p, id);
	}
	
	public String toString() {
		return "Multi Variable: " + name;
	}

	public boolean compareTree(SimpleNode n)
	{
		int i;
		// Do both nodes have the same id?
		if (id!=n.getId()) return false;
		// Do both nodes have the same name?
		if (getName()!=n.getName()) return false;
		// Yes, they do
		return true;
	}

	public CLIPSData execute() {
		// ???
		return null;
	}
}
