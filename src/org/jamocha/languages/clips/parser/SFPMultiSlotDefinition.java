/* Generated By:JJTree: Do not edit this line. SFPMultiSlotDefinition.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=SFP,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.jamocha.languages.clips.parser;

public class SFPMultiSlotDefinition extends SimpleNode {
	public SFPMultiSlotDefinition(int id) {
		super(id);
	}

	public SFPMultiSlotDefinition(SFPParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SFPParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=b27ba5cc42754fe835deca6b3524e7c8 (do not edit this
 * line)
 */
