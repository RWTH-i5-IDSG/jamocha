package org.jamocha.parser.cool;

import java.util.ArrayList;
import java.lang.String;

import org.jamocha.rete.TemplateSlot;

public class SlotDefinition
{
	private boolean multi=false;
	String name;
	ArrayList<TemplateSlot> facets=null;
	
	SlotDefinition()
	{
		facets=new ArrayList<TemplateSlot>();
	};
	
	public void setName(String n)
	{ 	name=n; };

	public String getName()
	{ 	return name; };

	public void setMultiSlot(boolean ms)
	{	multi=ms; };

	public boolean getMultiSlot()
	{	return multi; };
	
	public void addAttribute(TemplateSlot a)
	{
		facets.add(a);
	};
	
	public ArrayList<TemplateSlot> getAttributes()
	{
		return facets;
	};

}
