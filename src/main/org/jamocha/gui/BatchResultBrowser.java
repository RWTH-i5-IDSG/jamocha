/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jamocha.gui.icons.IconLoader;

/**
 * With this frame one can browse through all results of File->Batch File
 * processes.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public class BatchResultBrowser extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JPanel topPanel;

	private JButton batchResultsButton;

	private JButton removeButton;

	private JButton reloadButton;

	private JButton closeButton;

	private JTextArea aboutArea;

	private JComboBox resultsBox;

	private ResultBoxModel resultsBoxModel;

	private Map<String, String> batchResults;

	BatchResultBrowser(JButton batchResultsButton) {
		this.batchResultsButton = batchResultsButton;
		topPanel = new JPanel();
		setLocationByPlatform(true);
		setSize(650, 400);
		setLayout(new BorderLayout());
		resultsBoxModel = new ResultBoxModel();
		resultsBox = new JComboBox(resultsBoxModel);
		removeButton = new JButton(IconLoader.getImageIcon("delete"));
		removeButton.addActionListener(this);
		removeButton.setToolTipText("Remove this batch result");
		removeButton.setVisible(false);
		reloadButton = new JButton(IconLoader.getImageIcon("arrow_refresh"));
		reloadButton.addActionListener(this);
		reloadButton
				.setToolTipText("Reload the list of available batch results");
		topPanel.add(resultsBox);
		//topPanel.add(removeButton);
		//topPanel.add(reloadButton);
		add(topPanel, BorderLayout.NORTH);
		aboutArea = new JTextArea();
		aboutArea.setBorder(BorderFactory.createEmptyBorder());
		aboutArea.setLineWrap(true);
		aboutArea.setWrapStyleWord(true);
		aboutArea.setEditable(false);
		add(new JScrollPane(aboutArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);
		resultsBox.addActionListener(this);
		JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		closeButton = new JButton("close");
		closeButton.addActionListener(this);
		closePanel.add(reloadButton);
		closePanel.add(removeButton);
		closePanel.add(closeButton);
		add(closePanel, BorderLayout.SOUTH);
		batchResultsButton.setIcon(IconLoader.getImageIcon("lorry"));
	}

	void setResults(Map<String, String> batchResults) {
		this.batchResults = batchResults;
		resultsBoxModel.setItems(batchResults.keySet().toArray());
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(closeButton)) {
			dispose();
		} else if (event.getSource().equals(resultsBox)) {
			Object item = resultsBox.getSelectedItem();
			if (item != null) {
				aboutArea.setText(batchResults.get(item.toString()));
			}
			removeButton.setVisible(true);
			batchResultsButton.setIcon(IconLoader.getImageIcon("lorry"));
		} else if (event.getSource().equals(removeButton)) {
			Object item = resultsBox.getSelectedItem();
			if (item != null) {
				resultsBoxModel.removeItem(item);
				batchResults.remove(item);
				aboutArea.setText("");
				resultsBox.setSelectedIndex(-1);
				removeButton.setVisible(false);
				// if we removed the last result we hide the indicator button
				if (batchResults.isEmpty()) {
					batchResultsButton.setVisible(false);
				}
			}
		} else if (event.getSource().equals(reloadButton)) {
			resultsBoxModel.setItems(batchResults.keySet().toArray());
			aboutArea.setText("");
			resultsBox.setSelectedIndex(-1);
			removeButton.setVisible(false);
		}
	}

	private class ResultBoxModel extends DefaultComboBoxModel {

		private static final long serialVersionUID = 1L;

		private Object[] items;

		private void setItems(Object[] items) {
			this.items = items;
			if (items != null)
				fireContentsChanged(this, 0, items.length);
			else
				fireContentsChanged(this, 0, 0);
		}

		private void removeItem(Object item) {
			if (items != null) {
				List<Object> temp = new LinkedList<Object>();
				for (Object tmpItem : items) {
					if (!tmpItem.equals(item)) {
						temp.add(tmpItem);
					}
				}
				items = temp.toArray();
				fireContentsChanged(this, 0, items.length);
			}
		}

		public Object getElementAt(int index) {
			if (items != null) {
				if (index > -1 && index < items.length) {
					return items[index];
				}
			}
			return null;
		}

		public int getSize() {
			if (items == null)
				return 0;
			return items.length;
		}

	}

}
