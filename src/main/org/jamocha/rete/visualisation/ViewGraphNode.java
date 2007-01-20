/*
 * Copyright 2002-2006 Peter Lin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://ruleml-dev.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.rete.visualisation;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collection;

import org.jamocha.rete.BaseNode;
import org.jamocha.rete.RootNode;

import java.lang.Math;

/**
 * @author Josef Alexander Hahn
 *
 * ViewGraphNode represents a node in the visualisation graph
 */
public class ViewGraphNode {
	
	protected int subtreewidth;
	protected BaseNode reteNode;
	protected boolean parentsChecked;
	protected Shape shape;
	protected ArrayList<ViewGraphNode> childs;
	protected ArrayList<ViewGraphNode> parents;
	protected int x;
	protected int y;
	public BaseNode getReteNode() {return reteNode;}
	public void setReteNode(BaseNode n){ reteNode=n;}

	protected void checkForValidAlignment() {
		if (x==-1) {
			ViewGraphNode r=this;
			while (!r.parents.isEmpty()) r=parents.get(0);
			r.calculateAlignment(0,0);
		}
	}

	public int getX() {
		checkForValidAlignment();
		return x;
	}

	public int getHeight() {
		int h=0;
		for (Iterator<ViewGraphNode> it=childs.iterator();it.hasNext();){
			h=Math.max(h,it.next().getHeight());
		}
		return h+1;
	}

	public int getWidth() {
		return getSubtreeWidth();
	}

	public int getY() {
		checkForValidAlignment();
		return y;
	}

	public ArrayList<ViewGraphNode> getSuccessors() {
		return childs;
	}
	
	public ArrayList<ViewGraphNode> getParents() {
		return parents;
	}

	public void calculateAlignment(int offsetX, int offsetY) {
		x=getSubtreeWidth()/2 -1+offsetX;
		if (x<offsetX) x=offsetX;
		y=offsetY;
		
		offsetY++;
		for (Iterator<ViewGraphNode> it=childs.iterator();it.hasNext();){
			ViewGraphNode sub=it.next();
			sub.calculateAlignment(offsetX, offsetY);
			offsetX+=sub.getSubtreeWidth();
			if (sub.getSubtreeWidth()==0) offsetX+=2;
		}
	}

	/**
	 * Tells the node to recalculate the subtree width
	 */	
	protected void invalidateSubtreeWidth() {
		subtreewidth=-1;
		x=-1;
		y=-1;
		for (Iterator<ViewGraphNode> it=parents.iterator();it.hasNext();){
			it.next().invalidateSubtreeWidth();
		}
	}

	/**
	 * Yet another constructor ;)
	 */
	public ViewGraphNode(BaseNode n) {
		this();
		setReteNode(n);
	}

	/**
	 * Builds a complete Graph by traversing root
	 * @param root
	 */
	public static ViewGraphNode buildFromRete(RootNode root) {
		Collection firstLevel=root.getObjectTypeNodes().values();
		ViewGraphNode res=new ViewGraphNode();
		Hashtable<BaseNode,ViewGraphNode> ht=new Hashtable<BaseNode,ViewGraphNode>();
		for (Iterator iter = firstLevel.iterator(); iter.hasNext();) {
			BaseNode b=(BaseNode)iter.next();
			res.addToChilds(buildFromRete(b,ht));
		}
		return res;
	}

	public static ViewGraphNode buildFromRete(BaseNode root, Hashtable<BaseNode,ViewGraphNode> ht) {
		Object succ[]=root.getSuccessorNodes();
		ViewGraphNode foo=ht.get(root);
		ViewGraphNode res=null;
		if (foo==null) {
			res=new ViewGraphNode(root);
			for (int i=0;i<succ.length;i++) {
				res.addToChilds(buildFromRete((BaseNode)succ[i],ht));
			}
			ht.put(root, res);
		} else {
			res=foo;
		}
		return res;
	}



	/**
	 * Standard constructor
	 */
	public ViewGraphNode(){
		subtreewidth=-1;
		x=-1;
		parentsChecked=false;
		y=-1;
		shape=null;
		childs=new ArrayList<ViewGraphNode>();
		parents=new ArrayList<ViewGraphNode>();
	}	

	/**
	 * Add a Node to its childs
	 */
	public void addToChilds(BaseNode n) {
		ViewGraphNode node=new ViewGraphNode(n);
		childs.add(node);
		node.parents.add(this);
		invalidateSubtreeWidth();
	}

	public void addToChilds(ViewGraphNode n) {
		childs.add(n);
		n.parents.add(this);
		invalidateSubtreeWidth();
	}

	/**
	 * Remove a BaseNode from its childs
	 */
	public void removeFromChilds(BaseNode n) throws Exception{
		throw new Exception("Not yet implemented");
	}

	/**
	 * Returns the logical width of this node. It is 1 iff
	 * whichSubtree is the first parent. else 0
	 * @param whichSubtree
	 */
	protected int getMyWidth(ViewGraphNode whichSubtree) {
		if (parents.get(0)==whichSubtree) return 2;
		return 0;
	}

	/**
	 * Calculates the width of the subtree in a logical unit.
	 * (one node has width=2, two nodes have width=4 and so on;
	 * this is because we need "half node widths" for centering
	 * one node related to an even numbers of other nodes)
	 */
	protected int getSubtreeWidth() {
		int r=0;
		for (Iterator<ViewGraphNode> it=childs.iterator();it.hasNext();){
			ViewGraphNode nxt=it.next();
			int myWidth=nxt.getMyWidth(this);
			int subtreeWidth=nxt.getSubtreeWidth();
			if (myWidth>0) r+=Math.max(myWidth,subtreeWidth);
		}
		return r;
	}
	
	protected String toStringHelper(int indent) {
		String res="";
		for (int i=0;i<indent;i++) res+=" ";
		res+=stringRepresentation()+"\n";
		for (Iterator<ViewGraphNode> it=childs.iterator();it.hasNext();){
			res+=it.next().toStringHelper(indent+3);
		}
		return res;
	}

	protected String stringRepresentation() {
		String srep="";
		if (reteNode!=null){
			srep=reteNode.toString();
		} else {
			srep="RootNode";
		}
		srep+=" ("+getX()+","+getY()+")";
		return srep;
	}

	public String toString() {
		return toStringHelper(0);
	}
	
	
	public Shape getShape() {
		return shape;
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
	}
	public boolean isParentsChecked() {
		return parentsChecked;
	}
	public void setParentsChecked(boolean parentsChecked) {
		this.parentsChecked = parentsChecked;
	}


}
