/* Generated By:JJTree: Do not edit this line. SimpleNode.java */
/** AST Node representing a Symbol.
	Created for Agententechnologiepraktikum, Chair CS4, RWTH Aachen
	@author jjTree
	@author Ory Chowaw-Liebman
*/

import org.jamocha.parser.*;

/** Base implementation of the Node interface used by the nodes generated by jjTree.
	This class provides the basic functionality needed by all nodes. This is mostly
	managing children, but also dump and compare functions. However, nodes with 
	additional data ned to override the compare function. All nodes must override
	the execute function. As a rule, if a production in the grammar does not provide
	a meaningfull sense for an execute function for the node, no node should be 
	generated...
	Note that all nodes have names. This is used by rules which accept a broad range 
	of tokens (e.g. numbers, strings, instances, function/class/template/rule names,
	variables,...). Lists/Sets of strictly defined content should generate only one 
	node as well. Rules which only set some flags (e.g. auto-focus and salience for 
	rules) just set flags in the 'base node', in this case COOLDefruleConstruct.
	The construct classes are responsible for creating the appropriate information
	in the rete engine (e.g. adding rules and facts).
	Syntax trees (collections of Nodes) are directly responsible for interpreting 
	an expression or construct they represent. This is done by the execute function
	every node must provide, and uses a combination of recursion and polymorphism
	do to it's job. Trees can be saved and executed multiple times, usefull for
	rule and function actions. 
	SimpleNode has a memory usage of 2 references, 1 int, 1 array of references and
	one string. Node scan have quite some children. if we average 5, and an string
	of 8 bytes in average, we use averagely 76 bytes (assuming 64bit addresses, 32bit 
	ints and 8bit characters). One megabyte RAM would then hold well over 10000 nodes.
	However, constructs usually contain an instance of the engine data structure
	they represent, so need more memory.
	
	@todo Could speed up parsing by using faster datastructure for managing children.
*/
public class SimpleNode implements Node {
	protected Node parent;
	protected Node[] children;
	protected int id;
	protected COOL parser;

	protected String name;

	public void setName(String n) { name = n; }
	public String getName() { return name; }

	public SimpleNode(int i) {
		id = i;
		name="";
	}

	public SimpleNode(COOL p, int i) {
		this(i);
		parser = p;
		name="";
	}

	public void jjtOpen() {
	}

	public void jjtClose() {
	}
  
  // Tree Management Functions
	public void jjtSetParent(Node n) { parent = n; }
	public Node jjtGetParent() { return parent; }

	public void jjtAddChild(Node n, int i) {
		if (children == null) {
			children = new Node[i + 1];
		} else if (i >= children.length) {
			Node c[] = new Node[i + 1];
			System.arraycopy(children, 0, c, 0, children.length);
			children = c;
		}
		children[i] = n;
	}

	public Node jjtGetChild(int i) {
		return children[i];
	}

	public int jjtGetNumChildren() {
		return (children == null) ? 0 : children.length;
	}

  /* You can override these two methods in subclasses of SimpleNode to
     customize the way the node appears when the tree is dumped.  If
     your output uses more than one line you should override
     toString(String), otherwise overriding toString() is probably all
     you need to do. */
	public String toString() { return COOLTreeConstants.jjtNodeName[id]; }
	public String toString(String prefix) { return prefix + toString(); }

  /* Override this method if you want to customize how the node dumps
     out its children. */

	public void dump(String prefix) {
		System.out.println(toString(prefix));
		if (children != null) {
		for (int i = 0; i < children.length; ++i) {
			SimpleNode n = (SimpleNode)children[i];
			if (n != null) {
					n.dump(prefix + " ");
				}
			}
		}
	}
	
	// Some nodes may have local data (e.g. classes and functions)
	public void bindLocals(DeffunctionParams [] locals)
	{
		int i;
		for (i=0;i<jjtGetNumChildren();i++)
			jjtGetChild(i).bindLocals(locals);
	}


	/** Get this node's ID, as defined by jjTree.
	*/
	public int getId()
	{
		return id;
	}

	/** Compare two syntax trees, returns true if both trees have the
		same structure build from the same nodes.
	*/
	public boolean compareTree(Node n)
	{
		int i;
		// Do both nodes have the same id?
		if (id!=n.getId()) return false;
		// Do both nodes have the same name? (All nodes have a name, it defaults to the empty string. 
		// This can differentiate numbers and such...)
		if (getName()!=n.getName()) return false;
		// Do both nodes have the same number of children?
		if (jjtGetNumChildren()!=n.jjtGetNumChildren()) return false;
		// Do both nodes have the same children?
		for (i=0;i<jjtGetNumChildren();i++)
			if (!jjtGetChild(i).compareTree(n.jjtGetChild(i))) return false;
		// Yes, they do
		return true;
	}

	/** Execute a node. Node execution performs the operation of the language it 
		represents, thus interpreting the code.
	*/
	public JamochaValue execute() throws EvaluationException
	{
		// Just execute first child. More comples Nodes have to override this anyways.
		return JamochaValue.FALSE;		
	};
	
}

