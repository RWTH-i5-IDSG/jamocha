import java.util.ArrayList;
import java.lang.String;

public class SlotDefinition
{
	private boolean multi=false;
	String name;
	ArrayList<SlotAttrib> attributes=null;
	
	SlotDefinition()
	{
		attributes=new ArrayList();
	};
	
	public void setName(String n)
	{ 	name=n; };

	public String getName()
	{ 	return name; };

	public void setMultiSlot(boolean ms)
	{	multi=ms; };

	public boolean getMultiSlot()
	{	return multi; };
	
	public void addAttribute(SlotAttrib a)
	{
		attributes.add(a);
	};
	
	public ArrayList<SlotAttrib> getAttributes()
	{
		return attributes;
	};

}
