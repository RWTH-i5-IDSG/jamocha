/* Generated By:JJTree: Do not edit this line. CLIPS_SLSymbol.java */

public class COOLSymbol extends SimpleNode {

	public COOLSymbol(int id) {
		super(id);
	}

	public COOLSymbol(COOL p, int id) {
		super(p, id);
	}
	
	public String toString() {
		return "Symbol: " + name;
	}

	public boolean compareTree(SimpleNode n)
	{
		int i;
		// Do both nodes have the same id?
		if (id!=n.getId()) return false;
		// Do both nodes have the same name (which is always the same as the symbol)?
		if (getName()!=n.getName()) return false;
		// Yes, they do
		return true;
	}

	public CLIPSData execute()
	{
		return new CLIPSSymbol(name);
	}
}
