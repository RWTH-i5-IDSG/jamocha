/* Generated By:JJTree: Do not edit this line. SFPAnyFunction.java Version 6.1 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=true,VISITOR=true,TRACK_TOKENS=false,NODE_PREFIX=SFP,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.jamocha.languages.clips.parser.generated;

public
class SFPAnyFunction extends SimpleNode {
  public SFPAnyFunction(int id) {
    super(id);
  }

  public SFPAnyFunction(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {

    return
    visitor.visit(this, data);
  }
}
/* JavaCC - OriginalChecksum=fe958a8221e1d31a84469f70599db990 (do not edit this line) */
