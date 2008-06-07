/*
 * Copyright 2007 Sebastian Reinartz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.jamocha.org
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.tests.testcases;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.jamocha.tests.AbstractJamochaTest;

/**
 * @author Sebastian Reinartz
 */
public class JTCBeffyTests extends AbstractJamochaTest {

	public JTCBeffyTests(String arg0) {
		super(arg0);
	}
	
	private String getExpressionsString(String ext) throws IOException {
		StringBuilder result = new StringBuilder();
		InputStream istream = this.getClass().getResourceAsStream("JTCBeffyTests."+ext);
		BufferedReader reader = new BufferedReader(new InputStreamReader(istream,Charset.forName("UTF-8")));
		while (reader.ready()) result.append(reader.readLine()+"\n");
		reader.close();
		return result.toString();
	}

	@Override
	public void test() {
		try {
			executeTestEquals(getExpressionsString("clp"), getExpressionsString("result") );
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

}
