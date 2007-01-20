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

import org.jamocha.rete.util.HashMap;
import org.jamocha.rete.util.Iterator;

import junit.framework.TestCase;

/**
 * @author Peter Lin
 * 
 * A simple test of the HashMap
 */
public class StringDataTest extends TestCase {

	/**
	 * 
	 */
	public StringDataTest() {
		super();
	}

	public void testIntegerData1() {
		int count = 10000;
		HashMap map = new HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			Integer key = new Integer(idx);
			Integer val = new Integer(idx);
			map.put(key,val);
			assertEquals(val,map.get(key));
		}
	}

	public void testStringData1() {
		int count = 10000;
		HashMap map = new HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
			assertEquals(strval,map.get(key));
		}
	}
	
	public void testIterate() {
		int count = 10000;
		HashMap map = new HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		int counter = 0;
		Iterator itr = map.keyIterator();
		Object val = null;
		while ( (val = itr.next()) != null) {
			assertNotNull(val);
			// System.out.println("value=" + obj);
			counter++;
		}
		System.out.println("count = " + counter);
	}
	
	public void testStringData2() {
		int count = 100000;
		HashMap map = new HashMap();
		assertNotNull(map);
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long end = System.currentTimeMillis();
		System.out.println("Custom HashMap put ET - " + (end-start));
	}
	
	public void testJUHashMap1() {
		int count = 100000;
		java.util.HashMap map = new java.util.HashMap();
		assertNotNull(map);
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long end = System.currentTimeMillis();
		System.out.println("java.util.HashMap put ET - " + (end-start));
	}
	
	public void testStringData3() {
		int count = 100000;
		HashMap map = new HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			map.containsKey(key);
		}
		long end = System.currentTimeMillis();
		System.out.println("Custom HashMap containsKey ET - " + (end-start));
	}
	
	public void testJUHashMap2() {
		int count = 100000;
		java.util.HashMap map = new java.util.HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			map.containsKey(key);
		}
		long end = System.currentTimeMillis();
		System.out.println("java.util.HashMap containsKey ET - " + (end-start));
	}
	
	public void testStringData4() {
		int count = 100000;
		HashMap map = new HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			map.get(key);
		}
		long end = System.currentTimeMillis();
		System.out.println("Custom HashMap get(key) ET - " + (end-start));
	}
	
	public void testJUHashMap3() {
		int count = 100000;
		java.util.HashMap map = new java.util.HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			map.get(key);
		}
		long end = System.currentTimeMillis();
		System.out.println("java.util.HashMap get(key) ET - " + (end-start));
	}

	public void testStringData5() {
		int count = 100000;
		HashMap map = new HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		Iterator itr = map.keyIterator();
		Object val = null;
		while ( (val = itr.next()) != null) {
			val.hashCode();
		}
		long end = System.currentTimeMillis();
		System.out.println("Custom HashMap iterate ET - " + (end-start));
	}
	
	public void testJUHashMap4() {
		int count = 100000;
		java.util.HashMap map = new java.util.HashMap();
		assertNotNull(map);
		for (int idx=0; idx < count; idx++) {
			String key = "key" + idx;
			String strval = "value" + idx;
			map.put(key,strval);
		}
		long start = System.currentTimeMillis();
		java.util.Iterator itr = map.values().iterator();
		while (itr.hasNext()) {
			Object val = itr.next();
			val.hashCode();
		}
		long end = System.currentTimeMillis();
		System.out.println("java.util.HashMap iterate ET - " + (end-start));
	}

	public static void main(String[] args) {
		StringDataTest test = new StringDataTest();
		int loop = 5;
		for (int idx=0; idx < loop; idx++) {
			test.testIntegerData1();
			test.testStringData1();
			test.testIntegerData1();
			test.testStringData2();
			test.testJUHashMap1();
			test.testStringData3();
			test.testJUHashMap2();
			test.testStringData4();
			test.testJUHashMap3();
			test.testStringData5();
			test.testJUHashMap4();
			System.out.println(" ------------ ");
		}
	}
}
