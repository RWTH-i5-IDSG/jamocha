/* Generated By:JJTree: Do not edit this line. COOLCEList.java */

public class ConstructNode extends SimpleNode {
	String doc;	// Optional Docstring

	public ConstructNode(int id) {
		super(id);
		doc="";
	}
	
	public ConstructNode(COOL p, int id) {
		super(p, id);
		doc="";
	}

	public void setDocString(String ds)
	{ doc=ds; }

	public String getDocString(String ds)
	{ return doc; }
}
