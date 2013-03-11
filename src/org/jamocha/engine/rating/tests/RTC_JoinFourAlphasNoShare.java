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

public class RTC_JoinFourAlphasNoShare {

	public static RatingTestCase newTest(final double rootSize,
			final double rootFInsert, final double rootFDelete,
			final double rootTPP, final double sel1, final double sel2,
			final double sel3, final double sel4, final double jsfA1A2,
			final double jsfA1A3, final double jsfA2A3, final double jsfA2A4,
			final double jsfA3A4) throws NodeException {
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

						final MultiBetaJoinNode mbjn123 = new MultiBetaJoinNode(
								engine);

						ascn1.addChild(mbjn123);
						ascn2.addChild(mbjn123);
						ascn3.addChild(mbjn123);

						dict.setJSF(mbjn123.getId(),
								new NodeContainer(ascn1.getId(), FILTERED),
								new NodeContainer(ascn2.getId(), FILTERED),
								jsfA1A2);

						dict.setJSF(mbjn123.getId(),
								new NodeContainer(ascn1.getId(), FILTERED),
								new NodeContainer(ascn3.getId(), FILTERED),
								jsfA1A3);

						dict.setJSF(mbjn123.getId(),
								new NodeContainer(ascn2.getId(), FILTERED),
								new NodeContainer(ascn3.getId(), FILTERED),
								jsfA2A3);

						final MultiBetaJoinNode mbjn234 = new MultiBetaJoinNode(
								engine);

						ascn2.addChild(mbjn234);
						ascn3.addChild(mbjn234);
						ascn4.addChild(mbjn234);

						dict.setJSF(mbjn234.getId(),
								new NodeContainer(ascn2.getId(), FILTERED),
								new NodeContainer(ascn3.getId(), FILTERED),
								jsfA2A3);

						dict.setJSF(mbjn234.getId(),
								new NodeContainer(ascn2.getId(), FILTERED),
								new NodeContainer(ascn4.getId(), FILTERED),
								jsfA2A4);

						dict.setJSF(mbjn234.getId(),
								new NodeContainer(ascn3.getId(), FILTERED),
								new NodeContainer(ascn4.getId(), FILTERED),
								jsfA3A4);

						final TerminalNode tn1 = new TerminalNode(engine, null);
						mbjn123.addChild(tn1);
						final TerminalNode tn2 = new TerminalNode(engine, null);
						mbjn234.addChild(tn2);

						final AlphaSlotComparatorNode ascn5 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn, sel4);
						final MultiBetaJoinNode mbjn235 = new MultiBetaJoinNode(
								engine);

						ascn2.addChild(mbjn235);
						ascn3.addChild(mbjn235);
						ascn5.addChild(mbjn235);

						dict.setJSF(mbjn235.getId(),
								new NodeContainer(ascn2.getId(), FILTERED),
								new NodeContainer(ascn3.getId(), FILTERED),
								jsfA2A3);

						dict.setJSF(mbjn235.getId(),
								new NodeContainer(ascn2.getId(), FILTERED),
								new NodeContainer(ascn5.getId(), FILTERED),
								jsfA2A4);

						dict.setJSF(mbjn235.getId(),
								new NodeContainer(ascn3.getId(), FILTERED),
								new NodeContainer(ascn5.getId(), FILTERED),
								jsfA3A4);

						final TerminalNode tn3 = new TerminalNode(engine, null);
						mbjn235.addChild(tn3);
					}
				});
	}
}
