/* Generated By:JJTree: Do not edit this line. SFPIntegerList.java */

package org.jamocha.languages.clips.parser;

public class SFPIntegerList extends SimpleNode {
  public SFPIntegerList(int id) {
    super(id);
  }

  public SFPIntegerList(SFPParser p, int id) {
    super(p, id);
  }


  /** Accept the visitor. **/
  public Object jjtAccept(SFPParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}