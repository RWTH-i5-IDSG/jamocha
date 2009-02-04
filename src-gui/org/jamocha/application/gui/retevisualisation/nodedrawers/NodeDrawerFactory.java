package org.jamocha.application.gui.retevisualisation.nodedrawers;

import java.util.HashMap;
import java.util.Map;

import org.jamocha.application.gui.retevisualisation.NodeDrawer;
import org.jamocha.engine.nodes.AlphaQuantorDistinctionNode;
import org.jamocha.engine.nodes.AlphaSlotComparatorNode;
import org.jamocha.engine.nodes.DummyNode;
import org.jamocha.engine.nodes.LeftInputAdaptorNode;
import org.jamocha.engine.nodes.MultiBetaJoinNode;
import org.jamocha.engine.nodes.Node;
import org.jamocha.engine.nodes.ObjectTypeNode;
import org.jamocha.engine.nodes.RootNode;
import org.jamocha.engine.nodes.SimpleBetaFilterNode;
import org.jamocha.engine.nodes.SlotFilterNode;
import org.jamocha.engine.nodes.TerminalNode;

public class NodeDrawerFactory {

	private Map<Class<? extends Node>, NodeDrawer> map;

	private static NodeDrawerFactory instance;
	
	public static NodeDrawerFactory getInstance() {
		if (instance == null) {
			instance = new NodeDrawerFactory();
		}
		return instance;
	}
	
	private NodeDrawerFactory() {
		map = new HashMap<Class<? extends Node>, NodeDrawer>();
	}
	
	public NodeDrawer getDrawer(Node node) {
		return getDrawer(node.getClass());
	}
	
	public NodeDrawer getDrawer(Class<? extends Node> nodeClass) {
		NodeDrawer result = map.get(nodeClass);
		if (result == null) {
			result = constructDrawer(nodeClass);
			map.put(nodeClass, result);
		}
		return result;
	}

	private NodeDrawer constructDrawer(Class<? extends Node> nodeClass) {
		if (nodeClass.equals(AlphaQuantorDistinctionNode.class))
			return new QuantorBetaFilterNodeDrawer();
		if (nodeClass.equals(AlphaSlotComparatorNode.class)) 
			return new SlotFilterNodeDrawer();
		if (nodeClass.equals(DummyNode.class)) 
			return new SlotFilterNodeDrawer();
		if (nodeClass.equals(LeftInputAdaptorNode.class)) 
			return new LIANodeDrawer();
		if (nodeClass.equals(ObjectTypeNode.class)) 
			return new ObjectTypeNodeDrawer();
		if (nodeClass.equals(SlotFilterNode.class)) 
			return new SlotFilterNodeDrawer();
		if (nodeClass.equals(TerminalNode.class)) 
			return new TerminalNodeDrawer();
		if (nodeClass.equals(RootNode.class)) 
			return new RootNodeDrawer();
		if (nodeClass.equals(MultiBetaJoinNode.class)) 
			return new MultiBetaFilterNodeDrawer();
		if (nodeClass.equals(SimpleBetaFilterNode.class)) 
			return new SimpleBetaFilterNodeDrawer();
		return null;
	}
	
}
