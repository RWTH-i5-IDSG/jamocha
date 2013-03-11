package org.jamocha.engine.rating.tests;

import static org.jamocha.engine.rating.FilterState.FILTERED;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.MultiBetaJoinNode;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.rating.inputvalues.InputValuesDictionary;
import org.jamocha.engine.rating.inputvalues.NodeContainer;
import org.jamocha.engine.rating.tests.RatingTestCase.DetailPublisher;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;

public class RTC_JoinThreeAlphasMulti {

	public static RatingTestCase newTest(final double rootSize,
			final double rootFInsert, final double rootFDelete,
			final double rootTPP, final double sel1, final double sel2,
			final double sel3, final double jsfA1A2, final double jsfA1A3,
			final double jsfA2A3) throws NodeException {
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

						final MultiBetaJoinNode mbjn = new MultiBetaJoinNode(
								engine);

						ascn1.addChild(mbjn);
						ascn2.addChild(mbjn);
						ascn3.addChild(mbjn);

						dict.setJSF(mbjn.getId(),
								new NodeContainer(ascn1.getId(), FILTERED),
								new NodeContainer(ascn2.getId(), FILTERED),
								jsfA1A2);

						dict.setJSF(mbjn.getId(),
								new NodeContainer(ascn1.getId(), FILTERED),
								new NodeContainer(ascn3.getId(), FILTERED),
								jsfA1A3);

						dict.setJSF(mbjn.getId(),
								new NodeContainer(ascn2.getId(), FILTERED),
								new NodeContainer(ascn3.getId(), FILTERED),
								jsfA2A3);

						final TerminalNode tn = new TerminalNode(engine, null);
						mbjn.addChild(tn);
					}
				});
	}
}
