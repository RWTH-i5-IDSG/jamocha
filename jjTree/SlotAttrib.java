import java.util.ArrayList;

public class SlotAttrib
{
	// Default Attributes
	public final int NONE=0;
	public final int DERIVE=1;
	public final int ACTION=2;
	
	private boolean dynamic=false;
	private int deftype=NONE;
	private Node actions=null;
	
	public void setDefaultAction(Node n)
	{	dynamic=false; deftype=ACTION; actions=n;	};
	
	public void setDefaultNone()
	{	deftype=NONE;	};
	
	public void setDefaultDerive()
	{	deftype=DERIVE;	};

	public int getDefault()
	{	return deftype;	};
	
	public void setDynamic(Node n)
	{
		dynamic=true;	
		if (dynamic) deftype=ACTION;
	};
	
	public boolean getDynamic()
	{	return  dynamic;	};
	
	public Node getActions()
	{	return actions;	};
}