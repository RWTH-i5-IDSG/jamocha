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

public class RTC_JoinThreeAlphasR2L {

	public static RatingTestCase newTest(final double rootSize,
			final double rootFInsert, final double rootFDelete,
			final double rootTPP, final double sel1, final double sel2,
			final double sel3, final double jsfA2A3, final double jsfB1A1)
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
						final LeftInputAdaptorNode lian3 = new LeftInputAdaptorNode(
								engine);
						ascn3.addChild(lian3);

						final SimpleBetaFilterNode sbfn1 = new SimpleBetaFilterNode(
								engine);
						final SimpleBetaFilterNode sbfn2 = new SimpleBetaFilterNode(
								engine);

						lian3.addChild(sbfn1);
						ascn2.addChild(sbfn1);

						dict.setJSF(sbfn1.getId(),
								new NodeContainer(lian3.getId(), FILTERED),
								new NodeContainer(ascn2.getId(), FILTERED),
								jsfA2A3);

						sbfn1.addChild(sbfn2);
						ascn1.addChild(sbfn2);

						dict.setJSF(sbfn2.getId(),
								new NodeContainer(sbfn1.getId(), FILTERED),
								new NodeContainer(ascn1.getId(), FILTERED),
								jsfB1A1);

						final TerminalNode tn = new TerminalNode(engine, null);
						sbfn2.addChild(tn);
					}
				});
	}
}
