package org.jamocha.rete.visualisation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.jamocha.rete.Rete;

public class VisualizerPanel extends JPanel implements ClickListener {

	protected Visualizer miniMap, mainVis;
	protected JTextPane dump;
	protected JPanel optionsPanel;
	
	protected SimpleAttributeSet actAttributes, even, odd;
	
	public VisualizerPanel(Rete e) {
		
		miniMap = new Visualizer(e);
		miniMap.enableToolTips(false);
		miniMap.enableAutoScale(true);
		miniMap.enableShowSelection(true);
		miniMap.setPreferredSize(new Dimension(150,120));
		
		mainVis = new Visualizer(e);
		mainVis.enableToolTips(true);
		mainVis.enableAutoScale(false);
		
		mainVis.addViewportChangedListener(miniMap);
		miniMap.addViewportChangedListener(mainVis);
		miniMap.enableViewportByClick(true,mainVis);
		
		dump = new JTextPane();
		JScrollPane scrollDump = new JScrollPane(dump);
		even = new SimpleAttributeSet();
		odd = new SimpleAttributeSet();
		StyleConstants.setForeground(even, Color.blue);
		StyleConstants.setForeground(odd, Color.green.darker());
		actAttributes = even;
		
		mainVis.setClickListener(this);
		
		optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(150,120));
		
		
		JSplitPane splitMainAndOptionsToUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainVis, optionsPanel);
		JSplitPane splitMiniAndDumpToBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, miniMap, scrollDump);
		
		splitMainAndOptionsToUpper.setResizeWeight(1.0);
		
		JSplitPane splitUpperAndLower = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitMainAndOptionsToUpper, splitMiniAndDumpToBottom);
		splitUpperAndLower.setResizeWeight(1.0);
		
		this.setLayout(new BorderLayout());
		this.add(splitUpperAndLower,BorderLayout.CENTER);
		
	}

	@Override
	public void nodeClicked(String description) {
		try {
			dump.getDocument().insertString(dump.getDocument().getLength(), description + "\n", actAttributes);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (actAttributes == even) {
			actAttributes = odd;
		} else {
			actAttributes = even;
		}
		
	}
	
	
}
