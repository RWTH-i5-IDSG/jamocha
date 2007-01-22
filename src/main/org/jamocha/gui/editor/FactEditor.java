package org.jamocha.gui.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Module;
import org.jamocha.rete.MultiSlot;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Slot;
import org.jamocha.rete.Template;

public class FactEditor extends AbstractJamochaEditor implements
		ActionListener, ListSelectionListener {

	private static final long serialVersionUID = 6037731034903564707L;

	private JPanel contentPanel;

	private JButton cancelButton;

	private JButton assertButton;

	private JButton backButton;

	private JButton nextButton;

	private int step = 0;

	private JList moduleList;

	private JList templateList;

	private JTextArea dumpArea = new JTextArea();

	private DefaultListModel moduleListModel = new DefaultListModel();

	private DefaultListModel templateListModel = new DefaultListModel();

	private StringChannel channel;

	public FactEditor(Rete engine) {
		super(engine);
		setLayout(new BorderLayout());
		setTitle("Assert new Fact");
		contentPanel = new JPanel(new CardLayout());
		add(contentPanel, BorderLayout.CENTER);
		cancelButton = new JButton("Cancel", IconLoader.getImageIcon("cancel"));
		cancelButton.addActionListener(this);
		assertButton = new JButton("Assert Fact", IconLoader
				.getImageIcon("database_add"));
		assertButton.addActionListener(this);
		assertButton.setVisible(false);
		backButton = new JButton("Back", IconLoader
				.getImageIcon("resultset_previous"));
		backButton.addActionListener(this);
		backButton.setVisible(false);
		nextButton = new JButton("Next", IconLoader
				.getImageIcon("resultset_next"));
		nextButton.setHorizontalTextPosition(SwingConstants.LEFT);
		nextButton.addActionListener(this);
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		buttonPanel.add(cancelButton);
		buttonPanel.add(backButton);
		buttonPanel.add(nextButton);
		buttonPanel.add(assertButton);
		add(buttonPanel, BorderLayout.PAGE_END);
	}

	public void setStringChannel(StringChannel channel) {
		this.channel = channel;
	}

	public void init() {
		// initialize the Panels
		initPreselectionPanel();
		initFactEditPanel();

		showCurrentStep();
		setVisible(true);
	}

	private void initPreselectionPanel() {
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel preselectionPanel = new JPanel(gridbag);
		preselectionPanel.setBorder(BorderFactory
				.createTitledBorder("Module and Template Selection"));
		c.fill = GridBagConstraints.BOTH;
		moduleList = new JList(moduleListModel);
		moduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		moduleList.getSelectionModel().addListSelectionListener(this);
		Collection modules = engine.getAgenda().getModules();
		for (Object obj : modules) {
			Module mod = (Module) obj;
			moduleListModel.addElement(mod.getModuleName());
		}
		JPanel modulePanel = new JPanel();
		modulePanel.setLayout(new BoxLayout(modulePanel, BoxLayout.Y_AXIS));
		modulePanel.add(new JLabel("Select a Module:"));
		modulePanel.add(new JScrollPane(moduleList));
		c.weightx = 0.5;
		c.gridx = c.gridy = 0;
		c.weighty = 1.0;
		// c.gridwidth = GridBagConstraints.RELATIVE;
		gridbag.setConstraints(modulePanel, c);
		preselectionPanel.add(modulePanel);
		templateList = new JList(templateListModel);
		templateList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		templateList.getSelectionModel().addListSelectionListener(this);
		initTemplateList();
		JPanel templatePanel = new JPanel();
		templatePanel.setLayout(new BoxLayout(templatePanel, BoxLayout.Y_AXIS));
		templatePanel.add(new JLabel("Select a Template:"));
		templatePanel.add(new JScrollPane(templateList));
		// c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridx = 1;
		gridbag.setConstraints(templatePanel, c);
		preselectionPanel.add(templatePanel);
		dumpArea.setEditable(false);
		dumpArea.setFont(new Font("Courier", Font.PLAIN, 12));
		JPanel dumpAreaPanel = new JPanel();
		dumpAreaPanel.setLayout(new BoxLayout(dumpAreaPanel, BoxLayout.Y_AXIS));
		dumpAreaPanel.add(new JLabel("Template Definition:"));
		dumpAreaPanel.add(new JScrollPane(dumpArea));
		c.weightx = 0.0;
		c.gridwidth = 2;
		c.gridy = 1;
		c.gridx = 0;
		c.fill = GridBagConstraints.BOTH;
		gridbag.setConstraints(dumpAreaPanel, c);
		preselectionPanel.add(dumpAreaPanel);
		contentPanel.add(preselectionPanel, "preselection");

	}

	private void initTemplateList() {
		templateListModel.clear();
		Module module = engine.getAgenda().findModule(
				String.valueOf(moduleList.getSelectedValue()));
		if (module != null) {
			Collection templates = module.getTemplates();
			for (Object obj : templates) {
				Template tmp = (Template) obj;
				if (!module.getModuleName().equals("MAIN")
						|| !tmp.getName().equals("_initialFact")) {
					templateListModel.addElement(tmp.getName());
				}
			}
		}
	}

	private void initFactEditPanel() {
		// (deftemplate wurst(slot name)(slot size)(multislot KŠufer))
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel factEditPanel = new JPanel(new BorderLayout());
		JPanel innerPanel = new JPanel(gridbag);
		factEditPanel.setBorder(BorderFactory
				.createTitledBorder("Set the Slots for the Fact"));
		if (templateList.getSelectedIndex() > -1) {
			Module module = engine.getAgenda().findModule(
					String.valueOf(moduleList.getSelectedValue()));

			Template tmp = module.getTemplate(String.valueOf(templateList
					.getSelectedValue()));

			c.fill = GridBagConstraints.BOTH;
			c.weightx = 1.0;
			Slot[] slots = tmp.getAllSlots();
			for (int i = 0; i < slots.length; ++i) {
				c.gridx = 0;
				c.gridy = i;
				c.anchor = GridBagConstraints.EAST;
				JLabel label = new JLabel(slots[i].getName());
				gridbag.setConstraints(label, c);
				innerPanel.add(label);
				c.gridx = 1;
				c.anchor = GridBagConstraints.WEST;
				if (slots[i] instanceof MultiSlot) {
					JList multislotEditor = new JList();
					gridbag.setConstraints(multislotEditor, c);
					innerPanel.add(multislotEditor);
				} else {
					JTextField textField = new JTextField();
					gridbag.setConstraints(textField, c);
					innerPanel.add(textField);
				}
			}
		}
		factEditPanel.add(new JScrollPane(innerPanel), BorderLayout.CENTER);
		contentPanel.add("factEdit", factEditPanel);
	}

	private void showCurrentStep() {
		switch (step) {
		case 0:
			((CardLayout) contentPanel.getLayout()).show(contentPanel,
					"preselection");
			if (templateList.getSelectedIndex() > -1) {
				nextButton.setEnabled(true);
			} else {
				nextButton.setEnabled(false);
			}
			nextButton.setVisible(true);
			backButton.setVisible(false);
			assertButton.setVisible(false);
			nextButton.requestFocus();
			break;
		case 1:
			initFactEditPanel();
			((CardLayout) contentPanel.getLayout()).show(contentPanel,
					"factEdit");
			nextButton.setVisible(false);
			backButton.setVisible(true);
			assertButton.setVisible(true);
			assertButton.requestFocus();
			break;
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(assertButton)) {
			// TODO do assertion
			StringBuilder buffer = new StringBuilder();
			channel.executeCommand(buffer.toString(), true);
		} else if (event.getSource().equals(backButton)) {
			if (step > 0) {
				step--;
				showCurrentStep();
			}
		} else if (event.getSource().equals(nextButton)) {
			if (step < 1) {
				step++;
				showCurrentStep();
			}
		} else if (event.getSource().equals(cancelButton)) {
			close();
		}
	}

	public void valueChanged(ListSelectionEvent event) {
		if (event.getSource() == moduleList.getSelectionModel()) {
			initTemplateList();
		} else if (event.getSource() == templateList.getSelectionModel()) {
			if (templateList.getSelectedIndex() > -1) {
				nextButton.setEnabled(true);
				Module module = engine.getAgenda().findModule(
						String.valueOf(moduleList.getSelectedValue()));
				Template tmp = module.getTemplate(String.valueOf(templateList
						.getSelectedValue()));

				dumpArea.setText("(deftemplate " + tmp.getName() + "\n");
				Slot[] slots = tmp.getAllSlots();
				for (Slot slot : slots) {
					dumpArea.append("    (");
					if (slot instanceof MultiSlot) {
						dumpArea.append("multislot");
					} else {
						dumpArea.append("slot");
					}
					dumpArea.append(" " + slot.getName() + ")\n");
				}
				dumpArea.append(")");
			} else {
				nextButton.setEnabled(false);
			}
		}
	}
}
