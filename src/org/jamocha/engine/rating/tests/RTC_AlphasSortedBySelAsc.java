package org.jamocha.engine.rating.tests;

import org.jamocha.Constants;
import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.TerminalNode;
import org.jamocha.engine.rating.inputvalues.InputValuesDictionary;
import org.jamocha.engine.rating.tests.RatingTestCase.DetailPublisher;
import org.jamocha.engine.workingmemory.elements.TemplateSlot;

public class RTC_AlphasSortedBySelAsc {

	public static RatingTestCase newTest(final double rootSize,
			final double rootFInsert, final double rootFDelete,
			final double rootTPP) throws NodeException {
		return new RatingTestCase(rootSize, rootFInsert, rootFDelete, rootTPP,
				new DetailPublisher() {
					@Override
					protected void publishDetails(Engine engine, RootNode root,
							InputValuesDictionary dict) throws NodeException {
						final ObjectTypeNode otn = publishAlpha(
								new ObjectTypeNode(engine), dict, root, 1);

						final double ascn4_1_sel = 0.8, ascn4_2_sel = 0.6, ascn4_3_sel = 0.4, ascn4_4_sel = 0.2;
						final AlphaSlotComparatorNode ascn4_1 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn,
								ascn4_1_sel);
						final AlphaSlotComparatorNode ascn4_2 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, ascn4_1,
								ascn4_2_sel);
						final AlphaSlotComparatorNode ascn4_3 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, ascn4_2,
								ascn4_3_sel);
						final AlphaSlotComparatorNode ascn4_4 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, ascn4_3,
								ascn4_4_sel);
						final TerminalNode tn4 = new TerminalNode(engine, null);
						ascn4_4.addChild(tn4);
					}
				});
	}
}
