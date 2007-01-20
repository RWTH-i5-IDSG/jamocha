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
package woolfel.rete;

import org.jamocha.rete.Constants;
import org.jamocha.rete.Rete;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ReteInitTest extends TestCase {

	/**
	 * 
	 */
	public ReteInitTest() {
		super();
	}

	/**
	 * @param arg0
	 */
	public ReteInitTest(String arg0) {
		super(arg0);
	}

    public void testInit() {
        Rete engine = new Rete();
        assertNotNull(engine);
    }
    
    public void testInitModule() {
        Rete engine = new Rete();
        assertNotNull(engine);
        assertNotNull(engine.getCurrentFocus());
        assertNotNull(engine.getCurrentFocus().getModuleName());
        assertEquals(engine.getCurrentFocus().getModuleName(),Constants.MAIN_MODULE);
        System.out.println("default module is " + engine.getCurrentFocus().getModuleName());
    }
    
    /**
     * Simple test to make sure the nodeId method work correctly
     *
     */
    public void testNodeId() {
        Rete engine = new Rete();
        assertNotNull(engine);
        assertEquals(2,engine.peakNextNodeId());
        assertEquals(2,engine.peakNextNodeId());
        assertEquals(2,engine.peakNextNodeId());
        System.out.println("we call peakNextNodeId() 3 times and it should return 1");
        assertEquals(2,engine.nextNodeId());
        assertEquals(3,engine.nextNodeId());
        assertEquals(4,engine.nextNodeId());
        int id = engine.nextNodeId();
        assertEquals(5,id);
        System.out.println("if the test passes, the last id should be 4. it is " + id);
    }
}
