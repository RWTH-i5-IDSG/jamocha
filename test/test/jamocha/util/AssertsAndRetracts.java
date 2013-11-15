package test.jamocha.util;

import java.util.ArrayList;
import java.util.List;

import lombok.Value;

import org.jamocha.dn.ConflictSet;
import org.jamocha.dn.ConflictSet.NodeAndToken;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Assert;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetract;
import org.jamocha.dn.memory.MemoryHandlerTerminal.AssertOrRetractVisitor;
import org.jamocha.dn.memory.MemoryHandlerTerminal.Retract;
import org.jamocha.dn.nodes.TerminalNode;

@Value
public class AssertsAndRetracts {
	int asserts, retracts;

	public static AssertsAndRetracts countAssertsAndRetractsInConflictSet(final ConflictSet cs) {
		final List<Assert> asserts = new ArrayList<>();
		final List<Retract> retracts = new ArrayList<>();
		for (final NodeAndToken nat : cs) {
			final AssertOrRetract<?> assertOrRetract = nat.getToken();
			final TerminalNode terminalNode = nat.getTerminal();
			assertOrRetract.accept(terminalNode, new AssertOrRetractVisitor() {

				@Override
				public void visit(TerminalNode node, Retract mem) {
					retracts.add(mem);
				}

				@Override
				public void visit(TerminalNode node, Assert mem) {
					asserts.add(mem);
				}
			});
		}
		return new AssertsAndRetracts(asserts.size(), retracts.size());
	}
}