/* Generated By:JJTree: Do not edit this line. SFPCardinalityAttribute.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=SFP,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.jamocha.languages.clips.parser;

public class SFPCardinalityAttribute extends SimpleNode {
	public SFPCardinalityAttribute(int id) {
		super(id);
	}

	public SFPCardinalityAttribute(SFPParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SFPParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=5c6e36eee983e9c064c101c9588c4477 (do not edit this
 * line)
 */
