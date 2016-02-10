/*
 * Copyright 2002-2016 The Jamocha Team
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.jamocha.org/
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the License.
 */

package test.jamocha.dn.nodes;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test Suite for the node package. <p> Please add new package test suites or single tests to the SuiteClasses
 * annotation. </p>
 *
 * @author Kai Schwarz <kai.schwarz@rwth-aachen.de>
 */
@RunWith(Suite.class)
@SuiteClasses({AlphaNodeTest.class, BetaNodeTest.class, ObjectTypeNodeTest.class, TokenProcessingTest.class})
public class AllNodeTests {

}
