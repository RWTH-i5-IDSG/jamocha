package org.jamocha.engine.rating.tests;

import static org.jamocha.engine.rating.FilterState.FILTERED;
import static org.jamocha.engine.rating.FilterState.UNFILTERED;

import java.io.FileWriter;
import java.io.IOException;

import org.jamocha.engine.Engine;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.NodeException;
import org.jamocha.engine.nodes.OneInputNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.rating.Converter;
import org.jamocha.engine.rating.inputvalues.InputValuesDictionary;
import org.jamocha.engine.rating.inputvalues.NodeContainer;

public class RatingTestCase {

	private final RatingResult result;
	private final InputValuesDictionary dict;

	class RatingResult {
		private final double memoryCost, runtimeCost;

		private RatingResult(double memoryCost, double runtimeCost) {
			this.memoryCost = memoryCost;
			this.runtimeCost = runtimeCost;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			long temp;
			temp = Double.doubleToLongBits(memoryCost);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			temp = Double.doubleToLongBits(runtimeCost);
			result = prime * result + (int) (temp ^ (temp >>> 32));
			return result;
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			RatingResult other = (RatingResult) obj;
			if (Double.doubleToLongBits(memoryCost) != Double
					.doubleToLongBits(other.memoryCost))
				return false;
			if (Double.doubleToLongBits(runtimeCost) != Double
					.doubleToLongBits(other.runtimeCost))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "RatingResult [memoryCost=" + memoryCost + ", runtimeCost="
					+ runtimeCost + "]";
		}

		public double getMemoryCost() {
			return memoryCost;
		}

		public double getRuntimeCost() {
			return runtimeCost;
		}

	}

	static final Engine engine = new Engine();
	static final RootNode root = engine.getNet().getRoot();
	static int instanceCount = 0;

	public RatingTestCase(final double rootSize, final double rootFInsert,
			final double rootFDelete, final double rootTPP,
			final DetailPublisher detailPublisher) throws NodeException {
		dict = new InputValuesDictionary();
		dict.setSize(new NodeContainer(root.getId(), UNFILTERED), rootSize);
		dict.setFInsert(new NodeContainer(root.getId(), FILTERED), rootFInsert);
		dict.setFDelete(new NodeContainer(root.getId(), FILTERED), rootFDelete);
		dict.setTuplesPerPage(rootTPP);
		detailPublisher.publishDetails(engine, root, dict);
		final Converter converter = new Converter(dict);
		converter.convert(root);
		result = new RatingResult(converter.getMemCost(),
				converter.getRunCost());
		try {
			final FileWriter file = new FileWriter("temp/"
					+ detailPublisher.getClass().getEnclosingClass()
							.getCanonicalName() + ".dat", true);
			// final FileWriter file = new FileWriter("temp/"
			// + detailPublisher.getClass().getEnclosingClass()
			// .getCanonicalName() + "_rootSize_"
			// + (int) Math.floor(Math.log(rootSize)) + "_instance_"
			// + ++instanceCount + ".dat");
			// file.write("rootSize " + rootSize + "\n");
			file.write(converter.getDetails());
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		root.unmount();
	}

	public RatingResult getRatingResult() {
		return result;
	}

	protected static abstract class DetailPublisher {

		/*
		 * create new nodes and append them to ROOT, publish values to DICT
		 */
		protected abstract void publishDetails(final Engine engine,
				final RootNode root, final InputValuesDictionary dict)
				throws NodeException;

		protected <T extends OneInputNode> T publishAlpha(final T node,
				final InputValuesDictionary dict, final Node parentNode,
				final double sel) throws NodeException {
			parentNode.addChild(node);
			final int parentId = node.getAlphaInput().getId();
			final double size = sel
					* dict.getSize(new NodeContainer(parentId, UNFILTERED));
			dict.addCalculatedSize(new NodeContainer(node.getId(), UNFILTERED),
					size);
			final NodeContainer parent = new NodeContainer(parentId, FILTERED);
			final NodeContainer cont = new NodeContainer(node.getId(), FILTERED);
			dict.addCalculatedFInsert(cont, sel * dict.getFInsert(parent));
			dict.addCalculatedFDelete(cont, sel * dict.getFDelete(parent));
			return node;
		}
	}

}
