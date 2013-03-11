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

public class RTC_TwoBetasDontShareOneAlpha {

	public static RatingTestCase newTest(double rootSize, double rootFInsert,
			double rootFDelete, final double rootTPP) throws NodeException {
		return new RatingTestCase(rootSize, rootFInsert, rootFDelete, rootTPP,
				new DetailPublisher() {
					@Override
					protected void publishDetails(Engine engine, RootNode root,
							InputValuesDictionary dict) throws NodeException {
						final ObjectTypeNode otn1 = publishAlpha(
								new ObjectTypeNode(engine), dict, root, 0.9);

						final AlphaSlotComparatorNode ascn1 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn1, 0.6);
						final LeftInputAdaptorNode lian1 = new LeftInputAdaptorNode(
								engine);
						ascn1.addChild(lian1);
						final AlphaSlotComparatorNode ascn2_1 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn1, 0.5);
						final AlphaSlotComparatorNode ascn2_2 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn1, 0.5);
						final AlphaSlotComparatorNode ascn3 = publishAlpha(
								new AlphaSlotComparatorNode(engine,
										Constants.EQUAL, new TemplateSlot(),
										new TemplateSlot()), dict, otn1, 0.4);
						final LeftInputAdaptorNode lian3 = new LeftInputAdaptorNode(
								engine);
						ascn3.addChild(lian3);

						final SimpleBetaFilterNode sbfn1 = new SimpleBetaFilterNode(
								engine);
						final SimpleBetaFilterNode sbfn2 = new SimpleBetaFilterNode(
								engine);

						lian1.addChild(sbfn1);
						ascn2_1.addChild(sbfn1);

						dict.setJSF(sbfn1.getId(),
								new NodeContainer(lian1.getId(), FILTERED),
								new NodeContainer(ascn2_1.getId(), FILTERED),
								0.5);

						ascn2_2.addChild(sbfn2);
						lian3.addChild(sbfn2);

						dict.setJSF(sbfn2.getId(),
								new NodeContainer(lian3.getId(), FILTERED),
								new NodeContainer(ascn2_2.getId(), FILTERED),
								0.5);

						final TerminalNode tn2 = new TerminalNode(engine, null);
						final TerminalNode tn3 = new TerminalNode(engine, null);

						sbfn1.addChild(tn2);
						sbfn2.addChild(tn3);
					}
				});
	}
}
