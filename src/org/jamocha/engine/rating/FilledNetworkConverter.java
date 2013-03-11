package org.jamocha.engine.rating;

import static org.jamocha.engine.rating.FilterState.FILTERED;
import static org.jamocha.engine.rating.FilterState.UNFILTERED;

import java.util.Collection;

import org.jamocha.engine.nodes.AlphaQuantorDistinctionNode;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.MultiBetaJoinNode;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.QuantorBetaFilterNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.SlotFilterNode;
import org.jamocha.engine.rating.inputvalues.InputValuesSink;
import org.jamocha.engine.rating.inputvalues.InputValuesSource;
import org.jamocha.engine.rating.inputvalues.NodeContainer;
import org.jamocha.engine.workingmemory.WorkingMemoryElement;

public class FilledNetworkConverter extends Converter {
	final InputValuesSink sink;

	// TODO use ROOT NODE values to determine SEL and with that EVERYthing else
	// ;)

	public FilledNetworkConverter(final InputValuesSource source,
			final InputValuesSink sink) {
		super(source);
		this.sink = sink;
	}

	/**
	 * negatable<br/>
	 * <ul>
	 * <li>positive: only beta-tuple for every alpha-fact matching</li>
	 * <li>negative: negated beta-node with positive beta input, negative alpha
	 * input</li>
	 * </ul>
	 * cost: beta-node Var2 (both cases)
	 */
	@Override
	public void visit(final QuantorBetaFilterNode node) {
		final RatingNode alpha = getAlphaInput(node);
		final RatingNode beta = getBetaInput(node);
		final double alphaSize = alpha.getFiltered().getSize();
		final double betaSize = beta.getFiltered().getSize();
		final int memSize = getMemorySize(node.memory());
		final double jsfAB = (alphaSize == 0 || betaSize == 0) ? 0 : memSize
				/ (alphaSize * betaSize);
		this.sink.addCalculatedJSF(node.getId(),
				new NodeContainer(alpha.getId(), FILTERED), new NodeContainer(
						beta.getId(), FILTERED), jsfAB);
		if (node.isNegated()) {
			final double jsfBaseA = (alphaSize == 0 || betaSize == 0) ? 0
					: (1 - memSize / betaSize) / alphaSize;
			this.sink.addCalculatedJSF(node.getId(),
					new NodeContainer(node.getId(), UNFILTERED),
					new NodeContainer(alpha.getId(), FILTERED), jsfBaseA);
		}
		super.visit(node);
	}

	/**
	 * filters facts having the same values in distinctSlots<br/>
	 * cost: alpha-node
	 */
	@Override
	public void visit(final AlphaQuantorDistinctionNode node) {
		this.sink.addCalculatedSize(
				new NodeContainer(node.getId(), UNFILTERED),
				getMemorySize(node.memory()));
		super.visit(node);
	}

	/**
	 * alpha-node<br/>
	 * cost: alpha-node
	 */
	@Override
	public void visit(final AlphaSlotComparatorNode node) {
		this.sink.addCalculatedSize(
				new NodeContainer(node.getId(), UNFILTERED),
				getMemorySize(node.memory()));
		super.visit(node);
	}

	/**
	 * Gator-node<br/>
	 * cost: simple beta-node
	 */
	@Override
	public void visit(final MultiBetaJoinNode node) {
		final RatingNode[] inputs = getInputs(node);

		if (inputs.length > 1) {
			double joinSize = ((double) getMemorySize(node.memory()))
					/ (inputs.length - 1);

			for (final RatingNode input : inputs) {
				final double inputSize = input.getFiltered().getSize();
				if (0 == inputSize) {
					joinSize = 0;
					break;
				}
				joinSize /= inputSize;
			}

			for (int outer = 0; outer < inputs.length; ++outer) {
				final int outerId = inputs[outer].getId();
				for (int inner = outer + 1; inner < inputs.length; ++inner) {
					final int innerId = inputs[inner].getId();
					this.sink.addCalculatedJSF(node.getId(), new NodeContainer(
							innerId, FILTERED), new NodeContainer(outerId,
							FILTERED), joinSize);
				}
			}
		}
		super.visit(node);
	}

	/**
	 * object type filter<br/>
	 * cost: alpha-node
	 */
	@Override
	public void visit(final ObjectTypeNode node) {
		this.sink.addCalculatedSize(
				new NodeContainer(node.getId(), UNFILTERED),
				getMemorySize(node.memory()));
		super.visit(node);
	}

	/**
	 * RETE-node<br/>
	 * cost: simple beta-node
	 */
	@Override
	public void visit(final SimpleBetaFilterNode node) {
		final RatingNode alpha = getAlphaInput(node);
		final RatingNode beta = getBetaInput(node);

		final double alphaSize = alpha.getFiltered().getSize();
		final double betaSize = beta.getFiltered().getSize();
		final int joinSize = getMemorySize(node.memory());

		final double jsf = (0 == alphaSize || 0 == betaSize) ? 0 : joinSize
				/ (alphaSize * betaSize);
		this.sink.addCalculatedJSF(node.getId(),
				new NodeContainer(alpha.getId(), FILTERED), new NodeContainer(
						beta.getId(), FILTERED), jsf);
		super.visit(node);
	}

	/**
	 * alpha-node<br/>
	 * cost: alpha-node
	 */
	@Override
	public void visit(final SlotFilterNode node) {
		this.sink.addCalculatedSize(
				new NodeContainer(node.getId(), UNFILTERED),
				getMemorySize(node.memory()));
		super.visit(node);
	}

	protected int getMemorySize(final Iterable<WorkingMemoryElement> memory) {
		if (memory instanceof Collection<?>) {
			return ((Collection<?>) memory).size();
		}
		int size = 0;
		for (@SuppressWarnings("unused")
		final WorkingMemoryElement wme : memory) {
			++size;
		}
		return size;
	}

}
