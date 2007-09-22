/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamocha.rete.eventhandling.EngineEvent;
import org.jamocha.rete.eventhandling.EngineEventListener;
import org.jamocha.rete.nodes.BaseNode;


/**
 * @author Peter Lin
 *
 * EventCounter is a simple utility class for counting and keeping track 
 * of events. It can be used for various purposes like keeping track of
 * statistics or unit tests.
 */
public class EventCounter implements EngineEventListener {

    private ArrayList<EngineEvent> asserts = new ArrayList<EngineEvent>();
    private ArrayList<EngineEvent> retracts = new ArrayList<EngineEvent>();
    private ArrayList<EngineEvent> profiles = new ArrayList<EngineEvent>();
    private Map<BaseNode,ArrayList<EngineEvent>> nodeFilter = new HashMap<BaseNode,ArrayList<EngineEvent>>();


	/* (non-Javadoc)
	 * @see woolfel.engine.rete.EngineEventListener#eventOccurred(woolfel.engine.rete.EngineEvent)
	 */
	public void eventOccurred(EngineEvent event) {
        if (event.getEventType() == EngineEvent.ASSERT_EVENT) {
            asserts.add(event);
        } else if (event.getEventType() == EngineEvent.ASSERT_PROFILE_EVENT) {
            asserts.add(event);
            profiles.add(event);
        } else if (event.getEventType() == EngineEvent.ASSERT_RETRACT_EVENT) {
            asserts.add(event);
            retracts.add(event);
        } else if (event.getEventType() == EngineEvent.ASSERT_RETRACT_PROFILE_EVENT) {
            asserts.add(event);
            profiles.add(event);
            retracts.add(event);
        } else if (event.getEventType() == EngineEvent.PROFILE_EVENT) {
            profiles.add(event);
        } else if (event.getEventType() == EngineEvent.RETRACT_EVENT) {
            retracts.add(event);
        }
        ArrayList<EngineEvent> val = this.nodeFilter.get(event.getSourceNode());
        if (val != null) {
            val.add(event);
        }
	}

    public int getAssertCount() {
        return this.asserts.size();
    }
    
    public int getProfileCount() {
        return this.profiles.size();
    }
    
    public int getRetractCount() {
        return this.retracts.size();
    }
    
    /**
     * To listen to a specific node, add the node to the filter
     * @param node
     */
    public void addNodeFilter(BaseNode node) {
        this.nodeFilter.put(node,new ArrayList<EngineEvent>());
    }
    
    public ArrayList<EngineEvent> getNodeEvents(BaseNode node) {
        return this.nodeFilter.get(node);
    }
}
