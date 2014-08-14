/*
 * Copyright 2002-2014 The Jamocha Team
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.jamocha.org/
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package test.jamocha.languages.common;

import static org.junit.Assert.*;

import org.jamocha.languages.common.ConditionalElement;
import org.jamocha.languages.common.ConditionalElement.AndFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.ExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.InitialFactConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NegatedExistentialConditionalElement;
import org.jamocha.languages.common.ConditionalElement.NotFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.OrFunctionConditionalElement;
import org.jamocha.languages.common.ConditionalElement.SharedConditionalElementWrapper;
import org.jamocha.languages.common.ConditionalElement.TestConditionalElement;
import org.jamocha.languages.common.ConditionalElementsVisitor;
import org.jamocha.languages.common.SharedConditionalElementsVisitor;
import org.junit.Test;

/**
 * @author Christoph Terwelp <christoph.terwelp@rwth-aachen.de>
 *
 */
public class ConditionalElementTest {

	@Test
	public void SharedConditionalElementsVisitorTest() {
		ConditionalElement.InitialFactConditionalElement initCe =
				new ConditionalElement.InitialFactConditionalElement();
		ConditionalElement.SharedConditionalElementWrapper sharedCe =
				new ConditionalElement.SharedConditionalElementWrapper(initCe);
		TestSharedConditionalElementsVisitor sharedVisitor =
				new TestSharedConditionalElementsVisitor();
		TestConditionalElementsVisitor visitor = new TestConditionalElementsVisitor();
		sharedCe.accept(visitor);
		initCe.accept(visitor);
		sharedCe.accept(sharedVisitor);
		initCe.accept(sharedVisitor);
		assertEquals(2, visitor.initialCounter);
		assertEquals(1, sharedVisitor.initialCounter);
		assertEquals(1, sharedVisitor.sharedCounter);
	}

	private class TestSharedConditionalElementsVisitor implements SharedConditionalElementsVisitor {

		public String toString() {
			return "and: " + andCounter + ", ex: " + exCounter + ", ini: " + initialCounter
					+ ", negex: " + negatedExCounter + ", not: " + notCounter + ", or: "
					+ orCounter + ", test: " + testCounter + ", shared: " + sharedCounter;
		}

		public int andCounter = 0;

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			andCounter++;
		}

		public int exCounter = 0;

		@Override
		public void visit(ExistentialConditionalElement ce) {
			exCounter++;
		}

		public int initialCounter = 0;

		@Override
		public void visit(InitialFactConditionalElement ce) {
			initialCounter++;
		}

		public int negatedExCounter = 0;

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
			negatedExCounter++;
		}

		public int notCounter = 0;

		@Override
		public void visit(NotFunctionConditionalElement ce) {
			notCounter++;
		}

		public int orCounter = 0;

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			orCounter++;
		}

		public int testCounter = 0;

		@Override
		public void visit(TestConditionalElement ce) {
			testCounter++;
		}

		public int sharedCounter = 0;

		@Override
		public void visit(SharedConditionalElementWrapper ce) {
			sharedCounter++;
		}
	}

	private class TestConditionalElementsVisitor implements ConditionalElementsVisitor {
		
		public String toString() {
			return "and: " + andCounter + ", ex: " + exCounter + ", ini: " + initialCounter
					+ ", negex: " + negatedExCounter + ", not: " + notCounter + ", or: "
					+ orCounter + ", test: " + testCounter;
		}

		public int andCounter = 0;

		@Override
		public void visit(AndFunctionConditionalElement ce) {
			andCounter++;
		}

		public int exCounter = 0;

		@Override
		public void visit(ExistentialConditionalElement ce) {
			exCounter++;
		}

		public int initialCounter = 0;

		@Override
		public void visit(InitialFactConditionalElement ce) {
			initialCounter++;
		}

		public int negatedExCounter = 0;

		@Override
		public void visit(NegatedExistentialConditionalElement ce) {
			negatedExCounter++;
		}

		public int notCounter = 0;

		@Override
		public void visit(NotFunctionConditionalElement ce) {
			notCounter++;
		}

		public int orCounter = 0;

		@Override
		public void visit(OrFunctionConditionalElement ce) {
			orCounter++;
		}

		public int testCounter = 0;

		@Override
		public void visit(TestConditionalElement ce) {
			testCounter++;
		}
	}

}
