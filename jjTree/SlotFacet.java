/** Maanage a COOL class' facets
*/

import java.lang.String;

public class SlotFacet
{
	// Constants
	public final int SLOT=0;
	public final int SINGLE=1;
	public final int MULTI=2;

	public final int NONE=0;
	public final int DERIVE=1;
	public final int ACTION=2;
	public final int READ_WRITE=3;
	public final int READ=4;
	public final int WRITE=5;
	public final int INIT=6;

	// Slot type
	private int type=SLOT;

	// Default facet information	<<<<<<<<<<<<<<<<<<
	// Either dynamic or standard
	private boolean dynamic=false;
	private int deftype=NONE;
	Node actions=null;

	// Functions to Set/Get Default Attributes
	public void setDefaultAction(Node n)
	{	dynamic=false; deftype=ACTION; actions=n;	};
	
	public void setDefaultNone()
	{	dynamic=false; deftype=NONE;	};
	
	public void setDefaultDerive()
	{	dynamic=false; deftype=DERIVE;	};

	public int getDefault()
	{	return deftype;	};
	
	public void setDynamic(Node n)
	{
		dynamic=true;	
		deftype=ACTION;
		actions=n;
	};
	
	public boolean getDynamic()
	{	return  dynamic;	};

	// Default facet information	<<<<<<<<<<<<<<<<<<
	
	private boolean shared_mem=false;
	
	public void setStorageLocal(boolean sm)
	{ shared_mem=sm; };
	
	public boolean setStorageLocal()
	{ return shared_mem; };

	// Access facet information	<<<<<<<<<<<<<<<<<<
	private int access=INIT;
	
	public void setAccessReadWrite()
	{	access=READ_WRITE;	};	

	public void setAccessRead()
	{	access=READ;	};	

	public void setAccessInitialize()
	{	access=INIT;	};	
	
	public int getAccess()
	{	return access;	};	
	
	// Propagation facet information	<<<<<<<<<<<<<<<<<<
	private boolean inherit=false;
	
	public void setInherit(boolean in)
	{	inherit=in;	};

	public boolean getInherit()
	{	return inherit;	};
	// Source facet information	<<<<<<<<<<<<<<<<<<
	private boolean composite=false;
	
	/// true for composite, false for exclusive
	public void setSource(boolean src)
	{	composite=src;	};

	public boolean getSource()
	{	return composite;	};
	
	// Pattern match facet information	<<<<<<<<<<<<<<<<<<
	private boolean reactive=false;
	
	/// true for reactive
	public void setMatchReactive(boolean m)
	{	reactive=m;	};

	public boolean getMatchReactive()
	{	return reactive;	};

	// Visibility facet information	<<<<<<<<<<<<<<<<<<
	private boolean pub=false;
	
	/// true for public, false for private
	public void setPublic(boolean src)
	{	composite=src;	};

	public boolean getPublic()
	{	return composite;	};

	// Create Accessor facet information	<<<<<<<<<<<<<<<<<<
	private int accessor=NONE;
	
	public void setAccessorNone()
	{ accessor=NONE; }

	public void setAccessorRead()
	{ accessor=READ; }

	public void setAccessorWrite()
	{ accessor=WRITE; }

	public void setAccessorReadWrite()
	{ accessor=READ_WRITE; }
	
	public int getAccessor()
	{ return accessor; };

	// Override Message facet information	<<<<<<<<<<<<<<<<<<
	private boolean defmsg=true;
	String msg;
	
	public void setMessageDefault()
	{ defmsg=true;	};
	
	public void setMessageName(String n)
	{ msg=n; defmsg=false;	};

};
