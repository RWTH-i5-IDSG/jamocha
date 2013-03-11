package org.jamocha.engine.rating.tests;

import static org.jamocha.engine.rating.FilterState.FILTERED;
import static org.jamocha.engine.rating.FilterState.UNFILTERED;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.LeftInputAdaptorNode;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.QuantorBetaFilterNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.rating.inputvalues.InputValuesDictionary;
import org.jamocha.engine.rating.inputvalues.NodeContainer;
import org.jamocha.engine.rating.tests.RatingTestCase.DetailPublisher;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;

public class RTC_Negation {

	public static RatingTestCase newTest(final double rootSize,
			final double rootFInsert, final double rootFDelete,
			final double rootTPP, final double sel1, final double sel2,
			final double sel3, final double jsfA2N, final double jsfA1B)
			throws NodeException {
		return new RatingTestCase(rootSize, rootFInsert, rootFDelete, rootTPP,
				new DetailPublisher() {
					@Override
					protected void publishDetails(final Engine engine,
							final RootNode root,
							final InputValuesDictionary dict)
							throws NodeException {
						final ObjectTypeNode otn = publishAlpha(
								new ObjectTypeNode(engine), dict, root, 1);
						final AlphaSlotComparatorNode ascn1 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn, sel1);
						final AlphaSlotComparatorNode ascn2 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn, sel2);
						final AlphaSlotComparatorNode ascn3 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn, sel3);

						final LeftInputAdaptorNode lian2 = new LeftInputAdaptorNode(
								engine);
						ascn2.addChild(lian2);

						final QuantorBetaFilterNode qbfn = new QuantorBetaFilterNode(
								engine, true);

						lian2.addChild(qbfn);
						ascn3.addChild(qbfn);

						dict.setJSF(qbfn.getId(),
								new NodeContainer(lian2.getId(), FILTERED),
								new NodeContainer(ascn3.getId(), FILTERED),
								jsfA2N);

						dict.setJSF(qbfn.getId(),
								new NodeContainer(ascn3.getId(), FILTERED),
								new NodeContainer(qbfn.getId(), UNFILTERED),
								jsfA2N);

						final SimpleBetaFilterNode sbfn = new SimpleBetaFilterNode(
								engine);

						ascn1.addChild(sbfn);
						qbfn.addChild(sbfn);

						dict.setJSF(sbfn.getId(),
								new NodeContainer(ascn1.getId(), FILTERED),
								new NodeContainer(qbfn.getId(), FILTERED),
								jsfA1B);

						final TerminalNode tn = new TerminalNode(engine, null);
						sbfn.addChild(tn);
					}
				});
	}
}
