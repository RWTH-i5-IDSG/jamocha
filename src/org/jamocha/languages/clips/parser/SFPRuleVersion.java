/* Generated By:JJTree: Do not edit this line. SFPRuleVersion.java */

package org.jamocha.languages.clips.parser;

public class SFPRuleVersion extends SimpleNode {
  public SFPRuleVersion(int id) {
    super(id);
  }

  public SFPRuleVersion(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}