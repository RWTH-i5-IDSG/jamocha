/*
 * Copyright 2002-2007 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.parser.JamochaType;
import org.jamocha.parser.JamochaValue;


/**
 * Defclass contains the introspection information for a single object type.
 * It takes a class and uses java introspection to get a list of the get/set
 * attributes. It also checks to see if the class implements java beans
 * propertyChangeListener support. If it does, the Method object for those
 * two are cached.
 */
public class Defclass implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Class<?> OBJECT_CLASS = null;

	private BeanInfo INFO = null;

	private PropertyDescriptor[] PROPS = null;

	private boolean ISBEAN = false;

	private Method addListener = null;

	private Method removeListener = null;

	private Map<String, PropertyDescriptor> methods = new HashMap<String, PropertyDescriptor>();

	/**
	 * 
	 */
	public Defclass(Class<?> obj) {
		super();
		this.OBJECT_CLASS = obj;
		init();
	}

	/**
	 * init is responsible for checking the class to make sure
	 * it implements addPropertyChangeListener(java.beans.PropertyChangeListener)
	 * and removePropertyChangeListener(java.beans.PropertyChangeListener).
	 * We don't require the classes extend PropertyChangeSupport.
	 */
	public void init() {
		try {
			this.INFO = Introspector.getBeanInfo(this.OBJECT_CLASS);
			// we have to filter out the class PropertyDescriptor
			PropertyDescriptor[] pd = this.INFO.getPropertyDescriptors();
			ArrayList<PropertyDescriptor> list = new ArrayList<PropertyDescriptor>();
			for (int idx = 0; idx < pd.length; idx++) {
				if (pd[idx].getName().equals("class")) {
					// don't add
				} else {
					// we map the methods using the PropertyDescriptor.getName for
					// the key and the PropertyDescriptor as the value
					methods.put(pd[idx].getName(), pd[idx]);
					list.add(pd[idx]);
				}
			}
			PropertyDescriptor[] newpd = new PropertyDescriptor[list.size()];
			this.PROPS = (PropertyDescriptor[]) list.toArray(newpd);
			// logic for filtering the PropertyDescriptors
			if (ObjectFilter.lookupFilter(this.OBJECT_CLASS) != null) {
				// remove the props that should be invisible
				BeanFilter bf = ObjectFilter.lookupFilter(this.OBJECT_CLASS);
				this.PROPS = bf.filter(this.PROPS);
			}
			if (this.checkBean()) {
				this.ISBEAN = true;
			}
			// we clean up the array and arraylist
			list.clear();
			pd = null;
		} catch (IntrospectionException e) {
			// we should log this and throw an exception
		}
	}

	/**
	 * method checks to see if the class implements addPropertyChangeListener
	 * @return
	 */
	protected boolean checkBean() {
		boolean add = false;
		boolean remove = false;
		MethodDescriptor[] methd = this.INFO.getMethodDescriptors();
		for (int idx = 0; idx < methd.length; idx++) {
			MethodDescriptor desc = methd[idx];
			if (desc.getName().equals(Constants.PCS_ADD)
					&& checkParameter(desc)) {
				// check the parameter
				add = true;
			}
			if (desc.getName().equals(Constants.PCS_REMOVE)
					&& checkParameter(desc)) {
				// check the parameter
				remove = true;
			}
		}
		if (add && remove) {
			getUtilMethods();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * method will try to look up add and remove property change listener.
	 */
	protected void getUtilMethods() {
		try {
			// since a class may inherit the addListener method from
			// a parent, we lookup all methods and not just the
			// declared methods.
			addListener = this.OBJECT_CLASS.getMethod(Constants.PCS_ADD,
					new Class[] { PropertyChangeListener.class });
			removeListener = this.OBJECT_CLASS.getMethod(Constants.PCS_REMOVE,
					new Class[] { PropertyChangeListener.class });
		} catch (NoSuchMethodException e) {
			// we should log this
		}
	}

	/**
	 * Method checks the MethodDescriptor to make sure it only
	 * has 1 parameter and that it is a propertyChangeListener
	 * @param desc
	 * @return
	 */
	public boolean checkParameter(MethodDescriptor desc) {
		boolean ispcl = false;
		if (desc.getMethod().getParameterTypes().length == 1) {
			if (desc.getMethod().getParameterTypes()[0] == PropertyChangeListener.class) {
				ispcl = true;
			}
		}
		return ispcl;
	}

	/**
	 * If the class has a method for adding propertyChangeListener,
	 * the method return true.
	 * @return
	 */
	public boolean isJavaBean() {
		return this.ISBEAN;
	}

	/**
	 * Return the PropertyDescriptors for the class
	 * @return
	 */
	public PropertyDescriptor[] getPropertyDescriptors() {
		return this.PROPS;
	}

	/**
	 * Get the BeanInfo for the class
	 * @return
	 */
	public BeanInfo getBeanInfo() {
		return this.INFO;
	}

	public Class<?> getClassObject() {
		return this.OBJECT_CLASS;
	}

	/**
	 * Note: haven't decided if the method should throw an exception
	 * or not. Assuming the class has been declared and the defclass
	 * exists for it, it normally shouldn't encounter an exception.
	 * Cases where it would is if the method is not public. We should
	 * do that at declaration time and not runtime.
	 * @param col
	 * @param data
	 * @return
	 */
	public JamochaValue getSlotValue(int col, Object data) {
		try {
			return JamochaValue.newValueAutoType(this.PROPS[col].getReadMethod().invoke(data, (Object[]) null));
		} catch (IllegalAccessException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (InvocationTargetException e) {
			return null;
		}
	}

	/**
	 * create the deftemplate for the defclass
	 * @param tempName
	 * @return
	 */
	public Template createDeftemplate(String tempName) {
	    TemplateSlot[] st = new TemplateSlot[this.PROPS.length];
		for (int idx = 0; idx < st.length; idx++) {
			st[idx] = new TemplateSlot(this.PROPS[idx].getName());
			st[idx].setValueType(JamochaType.getMappingType(this.PROPS[idx]
					.getPropertyType()));
			// set the column id for the slot
			st[idx].setId(idx);
			if (this.PROPS[idx].getPropertyType().isArray()) {
			    st[idx].setMultiSlot(true);
			}
		}
		Template temp = new Deftemplate(tempName, this.OBJECT_CLASS
				.getName(), st);
		return temp;
	}

	/**
	 * Create the Deftemplate for the class, but with a given parent. If a
	 * template has a parent, only call this method. If the other method is
	 * called, the template is not gauranteed to work correctly.
	 * @param tempName
	 * @param parent
	 * @return
	 */
	public Template createDeftemplate(String tempName, Template parent) {
		reOrderDescriptors(parent);
		return createDeftemplate(tempName);
	}

	/**
	 * the purpose of this method is to re-order the PropertyDescriptors, so
	 * that template inheritance works correctly.
	 * @param parent
	 */
	protected void reOrderDescriptors(Template parent) {
		List<String> desc = null;
		boolean add = false;
		Slot[] pslots = parent.getAllSlots();
		PropertyDescriptor[] newprops = new PropertyDescriptor[this.PROPS.length];
		// first thing is to make sure the existing slots from the parent
		// are in the same column
		// now check to see if the new class has more fields
		if (newprops.length > pslots.length) {
			desc = new ArrayList<String>();
			add = true;
		}
		for (int idx = 0; idx < pslots.length; idx++) {
			newprops[idx] = getDescriptor(pslots[idx].getName());
			if (add) {
				desc.add(pslots[idx].getName());
			}
		}
		if (add) {
			List<PropertyDescriptor> newfields = new ArrayList<PropertyDescriptor>();
			for (int idz = 0; idz < this.PROPS.length; idz++) {
				if (!desc.contains(this.PROPS[idz].getName())) {
					// we add it to the new fields
					newfields.add(this.PROPS[idz]);
				}
			}
			int c = 0;
			// now we start from where parent slots left off
			for (int n = pslots.length; n < newprops.length; n++) {
				newprops[n] = (PropertyDescriptor) newfields.get(c);
				c++;
			}
		}
		this.PROPS = newprops;
	}

	/**
	 * Find the PropertyDescriptor with the same name
	 * @param name
	 * @return
	 */
	protected PropertyDescriptor getDescriptor(String name) {
		PropertyDescriptor pd = null;
		for (int idx = 0; idx < this.PROPS.length; idx++) {
			if (this.PROPS[idx].getName().equals(name)) {
				pd = this.PROPS[idx];
				break;
			}
		}
		return pd;
	}

	/**
	 * Get the addPropertyChangeListener(PropertyChangeListener) method for
	 * the class.
	 * @return
	 */
	public Method getAddListenerMethod() {
		return this.addListener;
	}

	/**
	 * Get the removePropertyChangeListener(PropertyChangeListener) method for
	 * the class.
	 * @return
	 */
	public Method getRemoveListenerMethod() {
		return this.removeListener;
	}

	/**
	 * Return the write method using slot name for the key
	 * @param name
	 * @return
	 */
	public Method getWriteMethod(String name) {
		return ((PropertyDescriptor) this.methods.get(name)).getWriteMethod();
	}

	/**
	 * Return the read method using the slot name for the key
	 * @param name
	 * @return
	 */
	public Method getReadMethod(String name) {
		return ((PropertyDescriptor) this.methods.get(name)).getReadMethod();
	}

	/**
	 * Method will make a copy and return it. When a copy is made, the 
	 * Method classes are not cloned. Instead, just the HashMap is cloned.
	 * @return
	 */
	public Defclass cloneDefclass() {
		Defclass dcl = new Defclass(this.OBJECT_CLASS);
		dcl.addListener = this.addListener;
		dcl.INFO = this.INFO;
		dcl.ISBEAN = this.ISBEAN;
		dcl.PROPS = this.PROPS;
		dcl.removeListener = this.removeListener;
		dcl.methods = new HashMap<String, PropertyDescriptor>();
		dcl.methods.putAll(this.methods);
		return dcl;
	}
}
