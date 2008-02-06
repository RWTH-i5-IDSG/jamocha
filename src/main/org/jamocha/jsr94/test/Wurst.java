package org.jamocha.jsr94.test;

/**
 * @author Josef Alexander Hahn <http://www.josef-hahn.de>
 */
public class Wurst {

	private String name; //bei matze an der lampe hängt auch ein private string ;)
	
	private int gewicht;

	public Wurst(String name, int gewicht) {
		super();
		this.name = name;
		this.gewicht = gewicht;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getGewicht() {
		return gewicht;
	}

	public void setGewicht(int gewicht) {
		this.gewicht = gewicht;
	}
	

}
