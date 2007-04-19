package org.jamocha.rete.configurations;

public class WildcardConstraintConfiguration extends ConstraintConfiguration {

	
    public enum WildcardType {
    	SFWILDCARD, MFWILDCARD
    }
	
    private WildcardType wildcardType = null;

	public WildcardType getWildcardType() {
		return wildcardType;
	}

	public void setWildcardType(WildcardType wildcardType) {
		this.wildcardType = wildcardType;
	}
    
    
    
}
