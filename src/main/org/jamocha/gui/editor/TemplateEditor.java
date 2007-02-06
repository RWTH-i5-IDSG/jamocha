package org.jamocha.gui.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.parser.JamochaType;
import org.jamocha.rete.Rete;

public class TemplateEditor extends AbstractJamochaEditor implements
		ActionListener {

	private static final long serialVersionUID = 6037731034903564707L;

	private JPanel contentPanel;

	private JPanel templatePanel;

	private JButton addSlotButton;

	private JButton cancelButton;

	private JButton assertButton;

	private JButton reloadButtonDumpAreaTemplate;

	private JTextArea dumpAreaTemplate = new JTextArea();

	private StringChannel channel;

	private int slotCount = 0;

	private GridBagLayout gridbag;

	private GridBagConstraints gridbagConstraints;
	
	private List<EditorRow> rows = new LinkedList<EditorRow>();
	
	public TemplateEditor(Rete engine) {
		super(engine);
		setLayout(new BorderLayout());
		setTitle("Create new Template");
		contentPanel = new JPanel(new BorderLayout());
		add(contentPanel, BorderLayout.CENTER);
		cancelButton = new JButton("Cancel", IconLoader.getImageIcon("cancel"));
		cancelButton.addActionListener(this);
		assertButton = new JButton("Create Template", IconLoader
				.getImageIcon("table_add"));
		assertButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		buttonPanel.add(cancelButton);
		buttonPanel.add(assertButton);
		add(buttonPanel, BorderLayout.SOUTH);

		addSlotButton = new JButton("Add Slot", IconLoader.getImageIcon("add"));
		addSlotButton.addActionListener(this);
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
		topPanel.add(addSlotButton);
		add(topPanel, BorderLayout.NORTH);

		dumpAreaTemplate.setEditable(false);
		dumpAreaTemplate.setFont(new Font("Courier", Font.PLAIN, 12));
		dumpAreaTemplate.setRows(5);

	}

	public void setStringChannel(StringChannel channel) {
		this.channel = channel;
	}

	public void init() {
		// initialize the Panels
		initTemplatePanel();
		setVisible(true);
	}

	private void initTemplatePanel() {
		templatePanel = new JPanel();
		contentPanel.add(new JScrollPane(templatePanel), BorderLayout.CENTER);

		contentPanel.add(dumpAreaTemplate, BorderLayout.SOUTH);

		gridbag = new GridBagLayout();
		gridbagConstraints = new GridBagConstraints();
		gridbagConstraints.weightx = 1.0;
		templatePanel.setLayout(gridbag);
		gridbagConstraints.anchor = GridBagConstraints.WEST;
		gridbagConstraints.gridx = 0;
		gridbagConstraints.gridy = 0;
		templatePanel.add(new JLabel());
		gridbagConstraints.gridx = 1;
		templatePanel.add(new JLabel());
		gridbagConstraints.gridy = 2;
		templatePanel.add(new JLabel("Type:"));
		gridbagConstraints.gridy = 3;
		templatePanel.add(new JLabel("Name:"));
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == assertButton) {
			channel.executeCommand(getCurrentDeftemplateString(false));
			JOptionPane.showMessageDialog(this,
					"Assertion done.\nPlease check the log for Messages.");
		} else if (event.getSource() == cancelButton) {
			close();
		} else if (event.getSource() == reloadButtonDumpAreaTemplate) {
			dumpAreaTemplate.setText(getCurrentDeftemplateString(true));
		} else if (event.getSource() == addSlotButton) {
			EditorRow row = new EditorRow(new DeleteButton(IconLoader
					.getImageIcon("delete"),slotCount), new JLabel("Slot "
					+ (slotCount + 1)), getNewTypesCombo(), new JTextField());
			row.deleteButton.addActionListener(this);
			++slotCount;
			addRemoveButton(templatePanel, row.deleteButton, gridbag,
					gridbagConstraints, slotCount);
			addLabel(templatePanel, row.rowLabel, gridbag, gridbagConstraints,
					slotCount);
			addTypesCombo(templatePanel, row.typeBox, gridbag,
					gridbagConstraints, slotCount);
			addNameField(templatePanel, row.nameField, gridbag,
					gridbagConstraints, slotCount);
			rows.add(row);
			templatePanel.revalidate();
		}
		else if( event.getSource() instanceof DeleteButton) {
			DeleteButton deleteButton = (DeleteButton) event.getSource();
			int row = deleteButton.getRow() + 1;
			int rowStart = (row*4);
			int rowEnd = rowStart + 4;
			System.out.println(row+" "+rowStart+" "+rowEnd);
			for(int i = rowEnd-1; i >= rowStart; --i) {
				templatePanel.remove(i);
			}
			rows.remove(deleteButton.getRow());
			for(int i = deleteButton.getRow(); i < rows.size(); ++i) {
				EditorRow editorRow = rows.get(i);
				editorRow.deleteButton.setRow(i);
				editorRow.rowLabel.setText("Slot "+(i+1));
			}
			--slotCount;
			templatePanel.revalidate();
		}
	}

	private String getCurrentDeftemplateString(boolean print) {
		StringBuilder res = new StringBuilder("(deftemplate");
		res.append(")");
		return res.toString();
	}

	private void addRemoveButton(JPanel parent, JButton button,
			GridBagLayout gridbag, GridBagConstraints c, int row) {
		c.gridx = 0;
		c.gridy = row;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(button, c);
		parent.add(button);
	}

	private void addLabel(JPanel parent, JLabel label, GridBagLayout gridbag,
			GridBagConstraints c, int row) {
		c.gridx = 1;
		c.gridy = row;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		gridbag.setConstraints(label, c);
		parent.add(label);
	}

	private void addTypesCombo(JPanel parent, JComboBox combo,
			GridBagLayout gridbag, GridBagConstraints c, int row) {
		c.gridx = 2;
		c.gridy = row;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		gridbag.setConstraints(combo, c);
		parent.add(combo);
	}

	private void addNameField(JPanel parent, JTextField field,
			GridBagLayout gridbag, GridBagConstraints c, int row) {
		c.gridx = 3;
		c.gridy = row;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		field.setColumns(30);
		gridbag.setConstraints(field, c);
		parent.add(field);
	}

	private JComboBox getNewTypesCombo() {
		JamochaType[] types = JamochaType.values();
		JComboBox box = new JComboBox(types);
		return box;
	}

	private class EditorRow {

		private DeleteButton deleteButton;

		private JLabel rowLabel;

		private JComboBox typeBox;

		private JTextField nameField;

		private EditorRow(DeleteButton deleteButton, JLabel rowLabel,
				JComboBox typeBox, JTextField nameField) {
			this.deleteButton = deleteButton;
			this.rowLabel = rowLabel;
			this.typeBox = typeBox;
			this.nameField = nameField;
		}

	}
	
	private class DeleteButton extends JButton {

		private static final long serialVersionUID = 1L;
		
		private int row;
		
		private DeleteButton(ImageIcon icon, int row) {
			super(icon);
			this.row = row;
		}
		
		private int getRow() {
			return row;
		}
		
		private void setRow(int row) {
			this.row = row;
		}
	}

}
