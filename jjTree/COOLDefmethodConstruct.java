/* Generated By:JJTree: Do not edit this line. COOLDefmethodConstruct.java */

public class COOLDefmethodConstruct extends ConstructNode {

	/** Method Index (cf. clipsbasic programmers guide section 8.4.2)
	 */
	protected String index;

	/** Sets Method Index (cf. clipsbasic programmers guide section 8.4.2)
	 *@param index method index
	 */
	public void setIndex(String index) { this.index = index; }

	/** Gets Method Index (cf. clipsbasic programmers guide section 8.4.2)
	 *@return method index
	 */
	public String getIndex() { return this.index; }

	public COOLDefmethodConstruct(int id) {
		super(id);
		this.index = "";
	}

	public COOLDefmethodConstruct(COOL p, int id) {
		super(p, id);
		this.index = "";
	}
	public String toString() {
		String index = this.index == "" ? "" : this.index + " ";
		return "defmethod \"" + name + "\" " + index + "(" + doc + ")";
	}

}
