/* Generated By:JJTree: Do not edit this line. SFPConstant.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=SFP,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.jamocha.languages.clips.parser;

public class SFPConstant extends SimpleNode {
	public SFPConstant(int id) {
		super(id);
	}

	public SFPConstant(SFPParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SFPParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=705b8ef3a334f5a2a4d204d2fdbb75eb (do not edit this
 * line)
 */
