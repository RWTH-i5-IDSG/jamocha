/* Generated By:JJTree: Do not edit this line. SFPAttributes.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=SFP,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.jamocha.languages.clips.parser;

public class SFPAttributes extends SimpleNode {
	public SFPAttributes(int id) {
		super(id);
	}

	public SFPAttributes(SFPParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SFPParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=48086fc4010ca9d5b0b6b0ba069fb23a (do not edit this
 * line)
 */
