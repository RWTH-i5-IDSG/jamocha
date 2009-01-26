/*
 * Copyright 2002-2008 The Jamocha Team
 * 
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

package org.jamocha.engine.nodes;

import java.util.logging.Logger;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.application.gui.retevisualisation.nodedrawers.ObjectTypeNodeDrawer;
import org.jamocha.communication.logging.Logging;
import org.jamocha.engine.Engine;
import org.jamocha.engine.ReteNet;
import org.jamocha.engine.workingmemory.WorkingMemory;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;
import org.jamocha.engine.workingmemory.elements.Deffact;
import org.jamocha.engine.workingmemory.elements.Deftemplate;
import org.jamocha.engine.workingmemory.elements.Fact;
import org.jamocha.engine.workingmemory.elements.JavaFact;
import org.jamocha.engine.workingmemory.elements.JavaTemplate;
import org.jamocha.engine.workingmemory.elements.Template;

/**
 * @author Josef Alexander Hahn <mail@josef-hahn.de> this node type filters by
 *         the object type (the deftemplate)
 */
public class ObjectTypeNode extends OneInputNode {

	protected Template template;

	@Deprecated
	private ObjectTypeNode(final int id, final WorkingMemory memory,
			final ReteNet net) {
		super(id, memory, net);
	}

	@Deprecated
	public ObjectTypeNode(final int id, final WorkingMemory memory,
			final ReteNet net, final Template templ) {
		this(id, memory, net);
		template = templ;
	}
	
	public ObjectTypeNode(Engine e) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet());
	}
	
	public ObjectTypeNode(Engine e, Template template) {
		this(e.getNet().nextNodeId(), e.getWorkingMemory(), e.getNet(), template);
	}

	//TODO: this logic should be in the template- and/or fact-classes
	private boolean eval(Fact f) {
		if (template instanceof Deftemplate) {
			Logging.logger(this.getClass()).debug("evaluating a "+f.getClass().getSimpleName()+" in a deftemplate situation");
			return (f.getTemplate().getName().equals(template.getName()));
		} else if (template instanceof JavaTemplate) {
			
			if (f instanceof Deffact) {
				Logging.logger(this.getClass()).debug("evaluating a "+f.getClass().getSimpleName()+" in a javatemplate/deffact situation");
				return (f.getTemplate().getName().equals(template.getName()));
			} else {
				Logging.logger(this.getClass()).debug("evaluating a "+f.getClass().getSimpleName()+" in a javatemplate/javafact situation");
				Class<? extends Object> concrete = ((JavaFact)f).getObject().getClass();
				Class<? extends Object> upper = ((JavaTemplate)template).getJavaClass();
				
				Logging.logger(this.getClass()).debug(upper.getCanonicalName()+" is"+(upper.isAssignableFrom(concrete)? "": " not")+" assignable from "+concrete.getCanonicalName());
				
				return upper.isAssignableFrom(concrete);
			}
			
		} else {
			Logging.logger(this.getClass()).debug("evaluating a "+f.getClass().getSimpleName()+" in an unknown situation");
			return false;
		}
	}

	@Override
	public void addWME(Node sender, final WorkingMemoryElement newElem) throws NodeException {
		if (!isActivated())
			return;
		final Template t = newElem.getFirstFact().getTemplate();
		if ( eval(newElem.getFirstFact()) ) addAndPropagate(newElem);
	}

	@Override
	public void removeWME(Node sender, final WorkingMemoryElement oldElem)
			throws NodeException {
		final Template t = oldElem.getFirstFact().getTemplate();
		if ( eval(oldElem.getFirstFact()) ) removeAndPropagate(oldElem);
	}

	/**
	 * returns the template, which this node will let pass
	 */
	public Template getTemplate() {
		return template;
	}

	@Override
	public boolean outputsBeta() {
		return false;
	}

	@Override
	protected NodeDrawer newNodeDrawer() {
		return new ObjectTypeNodeDrawer(this);
	}

	@Override
	public void getDescriptionString(final StringBuilder sb) {
		super.getDescriptionString(sb);
		sb.append("|template:").append(template.getName());
	}

}
