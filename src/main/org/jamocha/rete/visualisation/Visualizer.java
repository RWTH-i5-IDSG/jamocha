package org.jamocha.rete.visualisation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.JComponent;
import javax.swing.event.MouseInputListener;

import org.jamocha.rete.Rete;
import org.jamocha.rete.WorkingMemoryImpl;
import org.jamocha.rete.nodes.AlphaNode;
import org.jamocha.rete.nodes.BaseNode;
import org.jamocha.rete.nodes.TerminalNode;
 
public class Visualizer extends JComponent implements ComponentListener, MouseInputListener, ViewportChangedListener, MouseWheelListener{
	
	BaseNode rootNode;
	VisualizerSetup setup;

	protected final Color betaColor = new Color(0,0,255,120);
	protected final Color alphaColor = new Color(255,0,0,120);
	protected final Color betaColorDeselected = new Color(0,0,255,10);
	protected final Color alphaColorDeselected = new Color(255,0,0,10);
	protected Map<Point, BaseNode> point2node;
	protected Map<BaseNode,Boolean> isSelectedNode = new HashMap<BaseNode, Boolean>();
	protected int logicalWidth = 0;
	protected int logicalHeight = 0;
	protected List<ViewportChangedListener> viewportChangedListener; 
	protected boolean viewportChangeByClick = false;
	protected boolean autoScale = false;
	protected Map<BaseNode,Integer> rowHints;
	protected boolean showSelection = false;
	protected Visualizer selectionRelativeTo;
	protected boolean pressed;
	protected ClickListener clickListener;
	protected Map<BaseNode,List<String>> usedForRules; 
	protected List<String> selectedRules = new ArrayList<String>();
	protected List<BaseNode> selectedNodes;
	protected Point pressPos;
	protected Point offsetWhenPressed;
	protected boolean rightScroll;
	
	public void computeRowHints() {
		rowHints.clear();
		int actLvl = 0;
		Stack<BaseNode> activeLevel = new Stack<BaseNode>();
		Stack<BaseNode> nextLevel;
		activeLevel.add(rootNode);
		while (!activeLevel.isEmpty()) {
			nextLevel = new Stack<BaseNode>();
			for (BaseNode node : activeLevel) {
				rowHints.put(node, actLvl);
				for (BaseNode child : node.getChildNodes()) {
					nextLevel.push(child);
				}
			}
			actLvl+=1;
			logicalWidth = Math.max(logicalWidth,activeLevel.size());
			activeLevel = nextLevel;
		}
		logicalHeight = actLvl;
	}
	
	public void reload() {
		computeRowHints();
		calculateSelectedNodes();
	}
	
	public void addViewportChangedListener(ViewportChangedListener listener) {
		viewportChangedListener.add(listener);
	}
	
	protected void callViewportChangedListeners() {
		ViewportChangeEvent e = new ViewportChangeEvent();
		e.x=-setup.offsetX;
		e.y=-setup.offsetY;
		e.width=(int)(this.getWidth() / setup.scaleX);
		e.height=(int)(this.getHeight() / setup.scaleY);
		callViewportChangedListeners(e);
	}
	
	protected void callViewportChangedListeners(ViewportChangeEvent e) {
		for (ViewportChangedListener listener : viewportChangedListener)
			listener.viewportChanged(e);
	}
	
	public Visualizer(Rete e) {
		rootNode = ((WorkingMemoryImpl) e.getWorkingMemory()).getRootNode();
		setup = new VisualizerSetup();
		point2node = new HashMap<Point, BaseNode>();
		rowHints = new HashMap<BaseNode, Integer>();
		viewportChangedListener = new ArrayList<ViewportChangedListener>();
		usedForRules = new HashMap<BaseNode, List<String>>();
		reload();
		this.addComponentListener(this);
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
	}
	
	protected Point toPhysical(Point p, VisualizerSetup setup){
		Point result = new Point(p);
		result.x *= (BaseNode.shapeWidth + BaseNode.shapeGapWidth)/2;
		result.x += (BaseNode.shapeGapWidth+ BaseNode.shapeWidth)/2 ;
		result.y *= (BaseNode.shapeHeight+BaseNode.shapeGapHeight);
		result.y += (BaseNode.shapeGapHeight+BaseNode.shapeHeight) /2;
		result.x += setup.offsetX;
		result.y += setup.offsetY;
		result.x *= setup.scaleX;
		result.y *= setup.scaleY;
		return result;
	}
	
	protected int atan4(double y, double x) {
		double result = Math.atan2(y, x);
		result = (result * 180.0 ) / Math.PI;
		if (result<0) result += 360;
		if (result>360) result -=360;
		return (int)result;
	}
	
	protected List<String> calculateSelectedNodesHelper(BaseNode node) {
		List<String> result = new ArrayList<String>();
		
		if (node instanceof TerminalNode) {
			TerminalNode n = (TerminalNode)node;
			result.add( (String) n.getRule().getName() );
		} else {
			for (BaseNode child : node.getChildNodes()) {
				List<String> childResult = calculateSelectedNodesHelper(child);
				result.addAll( childResult );
			}
		}
		usedForRules.put(node, result);
		return result;
	}
	
	protected void calculateSelectedNodes() {
		calculateSelectedNodesHelper(rootNode);
		
		selectedNodes = new ArrayList<BaseNode>();
		
		Stack<BaseNode> nodes = new Stack<BaseNode>();
		nodes.add(rootNode);
		
		while (!nodes.isEmpty()) {
			BaseNode act = nodes.pop();
			
			for (BaseNode child : act.getChildNodes()) nodes.add(child);
			
			if (isNodeSelected(act)) selectedNodes.add(act);
			
		}
			
	}
	
	public boolean isNodeSelected(BaseNode node) {
		List<String> rules = usedForRules.get(node);
		for (String noderule : rules)
			for (String selected : selectedRules)
				if (noderule.equals(selected))  return true;
		return false;
	}

	protected void setSelectedRules(List<String> selected) {
		selectedRules = selected;
		calculateSelectedNodes();
		repaint();
	}
	
	protected void drawConnectionLines(BaseNode root, Map<BaseNode,Point> positions, Graphics2D canvas) {
		for (BaseNode child : root.getChildNodes() ) {
			Point rootPos = positions.get(root);
			Point childPos = positions.get(child);
			rootPos = toPhysical(rootPos,setup);
			childPos = toPhysical(childPos,setup);
			childPos = BaseNode.getLineEndPoint(rootPos, childPos, setup);
			rootPos = BaseNode.getLineEndPoint(childPos, rootPos, setup);
			
			if (isNodeSelected(child)) {
				if (root instanceof AlphaNode) {
					canvas.setColor( alphaColor );
				} else{
					canvas.setColor( betaColor );
				}
			} else {
				if (root instanceof AlphaNode) {
					canvas.setColor( alphaColorDeselected );
				} else{
					canvas.setColor( betaColorDeselected );
				}
			}
				
			
			int arcX, arcY,midX, midY, w, h;
			w = Math.abs(rootPos.x - childPos.x);
			if (rootPos.y < childPos.y) {
				h = childPos.y - rootPos.y;
				midX = rootPos.x;
				midY = childPos.y;
				arcX = midX-w;
				arcY = midY-h;
			} else {
				h = - childPos.y + rootPos.y;
				midY = rootPos.y;
				midX = childPos.x;
				arcX = midX-w;
				arcY = midY-h;
			}
			h *= 2;
			w *= 2;
			int originToRootX = rootPos.x - midX;
			int originToRootY = rootPos.y - midY;
			int originToChildX = childPos.x - midX;
			int originToChildY = childPos.y - midY;
			int startAngle = atan4(-originToRootY,originToRootX);
			int arcAngle = atan4(  -originToChildY,originToChildX) - startAngle;
			canvas.drawArc(arcX, arcY, w, h, startAngle, arcAngle);
			double angle = Math.atan2( (childPos.y-rootPos.y), (childPos.x-rootPos.x) );
			drawArrowHead(childPos.x, childPos.y, angle, canvas);
			drawConnectionLines(child, positions, canvas);
		}
	}
	
	protected void drawArrowHead(int x, int y, double angle, Graphics2D canvas) {
		int width = (int) (8 * setup.scaleX);
		int height = (int) (8 * setup.scaleY);
		canvas.fillOval(x - width/2, y - width/2, width+1, height+1);
	}

	public void paint(Graphics g) {
		Map<BaseNode,Point> node2point = new HashMap<BaseNode,Point>();
		point2node.clear();
		Graphics2D canvas = (Graphics2D) g;
		canvas.setColor(Color.white);
		canvas.fillRect(0,0,getWidth(), getHeight());
		
		BasicStroke widthOneStroke = new BasicStroke(1*setup.scaleX);
		canvas.setStroke(widthOneStroke);

		
		canvas.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		rootNode.drawNode(0,selectedNodes,canvas,setup,node2point, point2node,rowHints);
		
		BasicStroke widthThreeStroke = new BasicStroke(1*setup.scaleX);
		canvas.setStroke(widthThreeStroke);
		
		drawConnectionLines(rootNode, node2point, canvas);
		
		
		if (showSelection) {
			VisualizerSetup s = selectionRelativeTo.setup;
			int x = -(int)(s.offsetX * setup.scaleX) + (int)(selectionRelativeTo.getWidth() * setup.scaleX /2);
			int y = -(int)(s.offsetY * setup.scaleY) + (int)(selectionRelativeTo.getHeight() * setup.scaleY /2);
			int w = (int)((selectionRelativeTo.getWidth() / s.scaleX)*setup.scaleX);
			int h = (int)((selectionRelativeTo.getHeight() / s.scaleY)*setup.scaleY);
			g.setColor(new Color(100,100,255,100));
			g.fillRect(x-w/2, y-h/2, w, h);
		}
	}
	
	public void enableShowSelection(boolean enable){
		showSelection = enable;
		repaint();
	}

	
	protected Point getLogicalPosition(int x, int y) {
		Point result = new Point();
		
		
		x /= setup.scaleX;
		y /= setup.scaleY;
		
		x -= setup.offsetX;
		y -= setup.offsetY;
		
		result.y = y / (BaseNode.shapeGapHeight+BaseNode.shapeHeight);
		result.x = x / ((BaseNode.shapeGapWidth+BaseNode.shapeWidth)/2);
		
		return result;
	}
	
	public String getToolTipText(MouseEvent event) {
		int x = event.getX();
		int y = event.getY();
		BaseNode node = point2node.get(getLogicalPosition(x, y));
		if (node == null) return null;
		return "<html>"+node.toPPString().replace("\n", "<br>")+"</html>";
	}
	
	public void enableToolTips(boolean enable) {
		if (enable) {
			this.setToolTipText(" ");
		} else {
			this.setToolTipText(null);
		}
	}
	
	public void enableAutoScale(boolean enable) {
		autoScale = enable;
	}
	
	public void componentResized(ComponentEvent arg0) {
		if (autoScale) {
			int w = this.getWidth();
			int h = this.getHeight();
			int graphWidth = logicalWidth*(BaseNode.shapeWidth+BaseNode.shapeGapWidth);
			int graphHeight = logicalHeight*(BaseNode.shapeHeight+BaseNode.shapeGapHeight);
			double scaleW = ((double)w)/((double)graphWidth);
			double scaleH = ((double)h)/((double)graphHeight);
			setup.scaleX = setup.scaleY = (float) Math.min(scaleW, scaleH);
			repaint();
		}
		callViewportChangedListeners();
	}
	
	public void enableViewportByClick(boolean enable, Visualizer other) {
		viewportChangeByClick = enable;
		selectionRelativeTo = other;
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentShown(ComponentEvent e) {
	}

	
	protected void changeViewport(MouseEvent ev) {
		if (ev.getButton() != MouseEvent.BUTTON1) return;
		double x = ev.getX();
		double y = ev.getY();
		
		x /= setup.scaleX;
		y /= setup.scaleY;
		
		if (!autoScale) {
			x -= setup.offsetX;
			y -= setup.offsetY;
		}
		
		double normalizedWidth = selectionRelativeTo.getWidth() / selectionRelativeTo.setup.scaleX;
		double normalizedHeight = selectionRelativeTo.getHeight() / selectionRelativeTo.setup.scaleY;
		
		x -= normalizedWidth/2;
		y -= normalizedHeight/2;
		
		setup.offsetX = (int)x;
		setup.offsetY = (int)y;
		

		
	}
	
	protected void _changeVP(int x, int y){
		ViewportChangeEvent ev = new ViewportChangeEvent();

		x -= (selectionRelativeTo.getWidth() * setup.scaleX) /2;
		y -= (selectionRelativeTo.getHeight() * setup.scaleY) /2;
		
		ev.x = (int)-(x / setup.scaleX);
		ev.y = (int)-(y / setup.scaleY);
		callViewportChangedListeners(ev);
		repaint();
		
	}
	
	public void mouseClicked(MouseEvent arg0) {
		if (viewportChangeByClick) {
			_changeVP(arg0.getX(), arg0.getY());
		} else {
			
			int x = arg0.getX();
			int y = arg0.getY();
			BaseNode node = point2node.get(getLogicalPosition(x, y));
			String description = node.toPPString();
			clickListener.nodeClicked(description);
			
		}
	}

	
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void mousePressed(MouseEvent arg0) {
		pressPos = arg0.getPoint();
		offsetWhenPressed = new Point(setup.offsetX,setup.offsetY);
		if (!viewportChangeByClick && arg0.getButton()==MouseEvent.BUTTON3) rightScroll=true;
	}

	public void mouseReleased(MouseEvent arg0) {
		rightScroll=false;
	}

	public void mouseDragged(MouseEvent arg0) {
		if (viewportChangeByClick) {
			mouseClicked(arg0);
		} else {
			if (rightScroll) {
				int altOffX = offsetWhenPressed.x;
				int altOffY = offsetWhenPressed.y;
				int offsetOffsetX = (arg0.getPoint().x - pressPos.x);
				int offsetOffsetY = (arg0.getPoint().y - pressPos.y);
				setup.offsetX = altOffX + offsetOffsetX;
				setup.offsetY = altOffY + offsetOffsetY;
				repaint();
			}
		}
		
	}

	public void mouseMoved(MouseEvent arg0) {

	}

	
	public void viewportChanged(ViewportChangeEvent e) {
		
		if (!autoScale) {
		
			setup.offsetX = e.x;
			setup.offsetY = e.y;
			
	
			
			int maxOffsetX = -logicalWidth*(BaseNode.shapeGapWidth+BaseNode.shapeWidth)+getWidth();
			int maxOffsetY = -logicalHeight*(BaseNode.shapeGapHeight+BaseNode.shapeHeight)+getHeight();
			
			if (setup.offsetX < maxOffsetX) setup.offsetX = maxOffsetX;
			if (setup.offsetY < maxOffsetY) setup.offsetY = maxOffsetY;
			
			if (setup.offsetX >0) setup.offsetX = 0;
			if (setup.offsetY >0) setup.offsetY = 0;
		}
		repaint();
	}
	
	public void setClickListener(ClickListener cl) {
		this.clickListener = cl;
	}
	
	protected void zoom(double valueX, double valueY) {
		//is always absolute
		int midX = (int)( setup.offsetX + (getWidth() / setup.scaleX)/2.0 );
		int midY = (int)( setup.offsetY + (getHeight() / setup.scaleY)/2.0 );

		setup.scaleX=(float)valueX;
		setup.scaleY=(float)valueY;
		
		setup.offsetX = (int)(midX - (getWidth() / setup.scaleX)/2.0 );
		setup.offsetY = (int)(midY - (getHeight() / setup.scaleY)/2.0 );
	}
	
	protected void zoom(double valueX, double valueY, boolean relative){
		if (relative) {
			zoom(valueX*setup.scaleX, valueY*setup.scaleY);
		} else {
			zoom(valueX, valueY);
		}
		callViewportChangedListeners();
		repaint();
	}

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		int dir = arg0.getWheelRotation();
		double d = 1;
		if (dir > 0) {d = dir*1.1;} else { d = dir / -1.1; };
		zoom(d,d,true);
	}

}
