/* Generated By:JJTree: Do not edit this line. SFPDateTime.java Version 6.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=SFP,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.jamocha.languages.clips.parser.generated;

public
class SFPDateTime extends SimpleNode {
  public SFPDateTime(int id) {
    super(id);
  }

  public SFPDateTime(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=c2bd20c56e955aa81ba5f034e5362ded (do not edit this line) */