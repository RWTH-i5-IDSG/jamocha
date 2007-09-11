package org.jamocha.rule;

import org.jamocha.rete.Template;


public abstract class AbstractConnectedConstraint extends AbstractConstraint {
	
	
	Constraint left;
	Constraint right;
	
	public Constraint getLeft() {
		return left;
	}
	
	public void setLeft(Constraint left) {
		this.left = left;
	}
	
	public Constraint getRight() {
		return right;
	}
	
	public void setRight(Constraint right) {
		this.right = right;
	}
	
	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append("{");
		result.append(super.toString());
		if (left != null) result.append(left.toString());
		if (right != null) result.append(right.toString());
		result.append("}");
		return result.toString();
	}

	public Template getTemplate() {
		return left.getTemplate();
	}

	public void setTemplate(Template template) {
		left.setTemplate(template);
		right.setTemplate(template);
	}
	
}
