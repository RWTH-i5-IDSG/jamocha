package org.jamocha.gui.editor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
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
import org.jamocha.rete.Module;
import org.jamocha.rete.Rete;

public class RuleEditor extends AbstractJamochaEditor implements ActionListener {

	private static final long serialVersionUID = 1L;

	private JButton cancelButton;

	private JButton addButton;

	private JTextField nameField;

	private JTextField commentField;

	private JTextArea ruleLhsArea;

	private JTextArea ruleRhsArea;

	private JComboBox moduleBox;

	private StringChannel channel;

	public RuleEditor(Rete engine) {
		super(engine);
		setSize(600, 500);
		setLayout(new BorderLayout());
		setTitle("Add new Rule");
		cancelButton = new JButton("Cancel", IconLoader.getImageIcon("cancel"));
		cancelButton.addActionListener(this);
		addButton = new JButton("Add Rule", IconLoader.getImageIcon("car_add"));
		addButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		buttonPanel.add(cancelButton);
		buttonPanel.add(addButton);
		add(buttonPanel, BorderLayout.SOUTH);

		nameField = new JTextField(15);
		commentField = new JTextField(15);
		Collection modules = engine.getAgenda().getModules();
		String[] moduleNames = new String[modules.size()];
		int i = 0;
		for (Object obj : modules) {
			moduleNames[i++] = ((Module) obj).getModuleName();
		}
		moduleBox = new JComboBox(moduleNames);

		JPanel topPanel = new JPanel();
		topPanel.setBorder(BorderFactory
				.createTitledBorder("General Rule Settings"));
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 1));
		JPanel innerTopPanel = new JPanel(new GridLayout(3, 2));
		innerTopPanel.add(new JLabel("Name:"));
		innerTopPanel.add(nameField);
		innerTopPanel.add(new JLabel("Comment:"));
		innerTopPanel.add(commentField);
		innerTopPanel.add(new JLabel("Module:"));
		innerTopPanel.add(moduleBox);
		topPanel.add(innerTopPanel, BorderLayout.WEST);
		add(topPanel, BorderLayout.NORTH);

		JPanel centerPanel = new JPanel(new GridLayout(2, 1));
		// rule left hand side
		JPanel ruleLhsPanel = new JPanel(new BorderLayout());
		ruleLhsPanel.setBorder(BorderFactory
				.createTitledBorder("Left Hand Side (Premisse)"));
		ruleLhsArea = new JTextArea();
		ruleLhsPanel.add(new JScrollPane(ruleLhsArea));

		// rule right hand side
		JPanel ruleRhsPanel = new JPanel(new BorderLayout());
		ruleRhsPanel.setBorder(BorderFactory
				.createTitledBorder("Right Hand Side (Action)"));
		ruleRhsArea = new JTextArea();
		ruleRhsPanel.add(new JScrollPane(ruleRhsArea));

		centerPanel.add(ruleLhsPanel);
		centerPanel.add(ruleRhsPanel);

		add(centerPanel, BorderLayout.CENTER);
	}

	public void setStringChannel(StringChannel channel) {
		this.channel = channel;
	}

	@Override
	public void init() {
		setVisible(true);
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(addButton)) {
			StringBuilder buffer = new StringBuilder();
			buffer.append("(defrule " + nameField.getText() + " \""
					+ commentField.getText() + "\"");
			String ruleLhs = ruleLhsArea.getText().trim();
			if (!ruleLhs.startsWith("(")) {
				ruleLhs = "(" + ruleLhs + ")";
			}
			String ruleRhs = ruleRhsArea.getText().trim();
			if (!ruleRhs.startsWith("(")) {
				ruleRhs = "(" + ruleRhs + ")";
			}
			buffer.append("\n" + ruleLhs + "\n => \n" + ruleRhs + "\n");
			buffer.append(")");
			channel.executeCommand(buffer.toString());
			JOptionPane.showMessageDialog(this,
			"Rule defined.\nPlease check the log for Messages.");
		} else if (event.getSource() == cancelButton) {
			close();
		}
	}

}
