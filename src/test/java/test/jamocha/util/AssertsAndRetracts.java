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
package test.jamocha.util;

import lombok.Value;
import org.jamocha.dn.ConflictSet;
import org.jamocha.dn.ConflictSet.RuleAndToken;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetractVisitor;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;

import java.util.ArrayList;
import java.util.List;

@Value
public class AssertsAndRetracts {
    int asserts, retracts;

    public static AssertsAndRetracts countAssertsAndRetractsInConflictSet(final ConflictSet cs) {
        final List<Assert> asserts = new ArrayList<>();
        final List<Retract> retracts = new ArrayList<>();
        for (final RuleAndToken nat : cs.getAllRulesAndTokens()) {
            nat.getToken().accept(new AssertOrRetractVisitor() {
                @Override
                public void visit(final Retract mem) {
                    retracts.add(mem);
                }

                @Override
                public void visit(final Assert mem) {
                    asserts.add(mem);
                }
            });
        }
        return new AssertsAndRetracts(asserts.size(), retracts.size());
    }
}
