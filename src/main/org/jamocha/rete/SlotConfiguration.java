/**
 * 
 */
package org.jamocha.rete;

import org.jamocha.parser.EvaluationException;
import org.jamocha.parser.JamochaValue;

/**
 * @author Karl-Heinz Krempels, Sebastian Reinartz
 *
 */
public class SlotConfiguration implements Parameter {

	private String slotName = null;
	
	private Parameter[] slotValues = null;
	
	/* (non-Javadoc)
	 * @see org.jamocha.rete.Parameter#isObjectBinding()
	 */
	public boolean isObjectBinding() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jamocha.parser.Expression#getExpressionString()
	 */
	public String getExpressionString() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jamocha.parser.Expression#getValue(org.jamocha.rete.Rete)
	 */
	public JamochaValue getValue(Rete engine) throws EvaluationException {
		//multislot:
		if (isMultislot()){
			JamochaValue[] jvs= new JamochaValue[slotValues.length];
			for (int i=0 ; i< slotValues.length; i++){
				jvs[i] = slotValues[i].getValue(engine);
			}
			return JamochaValue.newList(jvs);
			
		//single slot:	
		}else {
			return slotValues[0].getValue(engine);
		}
		
	}

	public String getSlotName() {
		return slotName;
	}

	public void setSlotName(String slotName) {
		this.slotName = slotName;
	}

	public Parameter[] getSlotValues() {
		return slotValues;
	}

	public void setSlotValues(Parameter[] slotValues) {
		this.slotValues = slotValues;
	}

	public Boolean isMultislot(){
		return (slotValues.length >1);
	}
}
