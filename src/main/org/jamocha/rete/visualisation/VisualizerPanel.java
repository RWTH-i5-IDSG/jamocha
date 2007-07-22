package org.jamocha.rete.visualisation;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;

import org.jamocha.rete.Rete;

public class VisualizerPanel extends JPanel {

	protected Visualizer miniMap, mainVis;
	protected JTextPane dump;
	protected JPanel optionsPanel;
	
	public VisualizerPanel(Rete e) {
		
		miniMap = new Visualizer(e);
		miniMap.enableToolTips(false);
		miniMap.enableAutoScale(true);
		miniMap.enableViewportByClick(true);
		miniMap.enableShowSelection(true);
		miniMap.setPreferredSize(new Dimension(150,120));
		
		mainVis = new Visualizer(e);
		mainVis.enableToolTips(true);
		mainVis.enableAutoScale(false);
		
		mainVis.addViewportChangedListener(miniMap);
		miniMap.addViewportChangedListener(mainVis);

		
		dump = new JTextPane();
		
		optionsPanel = new JPanel();
		optionsPanel.setPreferredSize(new Dimension(150,120));
		
		
		JSplitPane splitMainAndOptionsToUpper = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, mainVis, optionsPanel);
		JSplitPane splitMiniAndDumpToBottom = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, miniMap, dump);
		
		splitMainAndOptionsToUpper.setResizeWeight(1.0);
		
		JSplitPane splitUpperAndLower = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitMainAndOptionsToUpper, splitMiniAndDumpToBottom);
		splitUpperAndLower.setResizeWeight(1.0);
		
		this.setLayout(new BorderLayout());
		this.add(splitUpperAndLower,BorderLayout.CENTER);
		
	}
	
	
}
