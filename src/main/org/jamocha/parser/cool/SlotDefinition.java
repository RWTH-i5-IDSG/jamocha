package org.jamocha.parser.cool;

import java.util.ArrayList;
import java.lang.String;

public class SlotDefinition
{
	private boolean multi=false;
	String name;
	ArrayList<SlotFacet> facets=null;
	
	SlotDefinition()
	{
		facets=new ArrayList<SlotFacet>();
	};
	
	public void setName(String n)
	{ 	name=n; };

	public String getName()
	{ 	return name; };

	public void setMultiSlot(boolean ms)
	{	multi=ms; };

	public boolean getMultiSlot()
	{	return multi; };
	
	public void addAttribute(SlotFacet a)
	{
		facets.add(a);
	};
	
	public ArrayList<SlotFacet> getAttributes()
	{
		return facets;
	};

}
