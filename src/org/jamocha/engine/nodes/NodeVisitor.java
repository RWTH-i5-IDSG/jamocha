package org.jamocha.engine.nodes;

public interface NodeVisitor {
	void visit(final QuantorBetaFilterNode node);

	void visit(final AlphaQuantorDistinctionNode node);

	void visit(final AlphaSlotComparatorNode node);

	void visit(final DummyNode node);

	void visit(final LeftInputAdaptorNode node);

	void visit(final MultiBetaJoinNode node);

	void visit(final ObjectTypeNode node);

	void visit(final RootNode node);

	void visit(final SimpleBetaFilterNode node);

	void visit(final SlotFilterNode node);

	void visit(final TerminalNode node);
}
