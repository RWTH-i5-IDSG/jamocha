package org.jamocha.engine.rating.tests;

import static org.jamocha.engine.rating.FilterState.FILTERED;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.LeftInputAdaptorNode;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.rating.inputvalues.InputValuesDictionary;
import org.jamocha.engine.rating.inputvalues.NodeContainer;
import org.jamocha.engine.rating.tests.RatingTestCase.DetailPublisher;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;

public class RTC_JoinFourAlphasShare {

	public static RatingTestCase newTest(final double rootSize,
			final double rootFInsert, final double rootFDelete,
			final double rootTPP, final double sel1, final double sel2,
			final double sel3, final double sel4, final double jsfA2A3,
			final double jsfA1A23, final double jsfA4A23) throws NodeException {
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
						final AlphaSlotComparatorNode ascn4 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn, sel4);

						final LeftInputAdaptorNode lian2 = new LeftInputAdaptorNode(
								engine);
						ascn2.addChild(lian2);

						final SimpleBetaFilterNode sbfn23 = new SimpleBetaFilterNode(
								engine);

						lian2.addChild(sbfn23);
						ascn3.addChild(sbfn23);

						dict.setJSF(sbfn23.getId(),
								new NodeContainer(lian2.getId(), FILTERED),
								new NodeContainer(ascn3.getId(), FILTERED),
								jsfA2A3);

						final SimpleBetaFilterNode sbfn1_23 = new SimpleBetaFilterNode(
								engine);
						ascn1.addChild(sbfn1_23);
						sbfn23.addChild(sbfn1_23);
						dict.setJSF(sbfn1_23.getId(),
								new NodeContainer(ascn1.getId(), FILTERED),
								new NodeContainer(sbfn23.getId(), FILTERED),
								jsfA1A23);

						final SimpleBetaFilterNode sbfn4_23 = new SimpleBetaFilterNode(
								engine);
						sbfn23.addChild(sbfn4_23);
						ascn4.addChild(sbfn4_23);
						dict.setJSF(sbfn4_23.getId(),
								new NodeContainer(sbfn23.getId(), FILTERED),
								new NodeContainer(ascn4.getId(), FILTERED),
								jsfA4A23);

						final TerminalNode tn1 = new TerminalNode(engine, null);
						sbfn1_23.addChild(tn1);
						final TerminalNode tn2 = new TerminalNode(engine, null);
						sbfn4_23.addChild(tn2);

						final AlphaSlotComparatorNode ascn5 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn, sel4);
						final SimpleBetaFilterNode sbfn5_23 = new SimpleBetaFilterNode(
								engine);
						ascn5.addChild(sbfn5_23);
						sbfn23.addChild(sbfn5_23);
						dict.setJSF(sbfn5_23.getId(),
								new NodeContainer(ascn5.getId(), FILTERED),
								new NodeContainer(sbfn23.getId(), FILTERED),
								jsfA1A23);
						final TerminalNode tn3 = new TerminalNode(engine, null);
						sbfn5_23.addChild(tn3);
					}
				});
	}
}
