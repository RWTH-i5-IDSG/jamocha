/* Generated By:JJTree: Do not edit this line. SFPTemplateAttribute.java */

package org.jamocha.parser.sfp;

public class SFPTemplateAttribute extends SimpleNode {
  public SFPTemplateAttribute(int id) {
    super(id);
  }

  public SFPTemplateAttribute(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}
