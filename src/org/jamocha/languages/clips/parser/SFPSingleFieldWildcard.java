/* Generated By:JJTree: Do not edit this line. SFPSingleFieldWildcard.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=SFP,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.jamocha.languages.clips.parser;

public class SFPSingleFieldWildcard extends SimpleNode {
	public SFPSingleFieldWildcard(int id) {
		super(id);
	}

	public SFPSingleFieldWildcard(SFPParser p, int id) {
		super(p, id);
	}

	/** Accept the visitor. **/
	public Object jjtAccept(SFPParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
}
/*
 * JavaCC - OriginalChecksum=52b9c39fbb5eea3037654edca9fe7036 (do not edit this
 * line)
 */
