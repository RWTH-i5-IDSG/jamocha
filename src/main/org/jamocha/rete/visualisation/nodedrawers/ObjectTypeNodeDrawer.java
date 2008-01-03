package org.jamocha.rete.visualisation.nodedrawers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.util.List;

import javax.swing.ImageIcon;

import org.jamocha.Constants;
import org.jamocha.gui.icons.IconLoader;
import org.jamocha.rete.nodes.Node;
import org.jamocha.rete.nodes.ObjectTypeNode;

public class ObjectTypeNodeDrawer extends AbstractNodeDrawer {

	private Image icon = null;
	
	public ObjectTypeNodeDrawer(Node owner) {
		super(owner);
		
		String templname = ((ObjectTypeNode)owner).getTemplate().getName();
		if (templname.equals(Constants.INITIAL_FACT)) templname="initialFact";
		ImageIcon ii = IconLoader.getImageIcon(templname,this.getClass());
		if (ii != null) {
			while (ii.getImageLoadStatus() == MediaTracker.LOADING)
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
				}
				icon = ii.getImage();
		}
	}
	
	protected void drawNode(int x, int y, int height, int width, int halfLineHeight, List<Node> selected, Graphics2D canvas){
		boolean isSelected=selected.contains(this.node);
		int alpha = (isSelected) ? 255 : 20;
		canvas.setColor( new Color(255,215,15,alpha) );
		canvas.fillRect(x, y, width, height);
		canvas.setColor(  new Color(208,181,44,alpha) );
		canvas.drawRect(x, y, width, height);
		canvas.setColor( new Color(0,0,0,alpha) );
		drawId(x,y,height,width, halfLineHeight,canvas);
		if (icon != null && isSelected) {
			float aspectRatio = icon.getWidth(null) / icon.getHeight(null);
			int w = (int) (height * aspectRatio);
			int h = height;
			int x1 = x + width - w/2;
			int y1 = y;
			canvas.drawImage(icon, x1, y1, w, h, null);
		}
		
		
	}
	


}
