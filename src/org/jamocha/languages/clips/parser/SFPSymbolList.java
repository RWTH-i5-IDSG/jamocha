/* Generated By:JJTree: Do not edit this line. SFPSymbolList.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=SFP,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.jamocha.languages.clips.parser;

public class SFPSymbolList extends SimpleNode {
	public SFPSymbolList(int id) {
		super(id);
	}

	public SFPSymbolList(SFPParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SFPParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=f68737a8a3cf7d8cffbf7e1768f803bc (do not edit this
 * line)
 */
