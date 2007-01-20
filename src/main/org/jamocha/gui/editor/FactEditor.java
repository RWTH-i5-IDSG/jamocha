package org.jamocha.gui.editor;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jamocha.gui.icons.IconLoader;
import org.jamocha.messagerouter.StringChannel;
import org.jamocha.rete.Module;
import org.jamocha.rete.Rete;
import org.jamocha.rete.Template;

public class FactEditor extends AbstractJamochaEditor implements ActionListener {

	private static final long serialVersionUID = 6037731034903564707L;

	private JPanel contentPanel;

	private JButton cancelButton;

	private JButton assertButton;

	private int step = 0;

	private JComboBox moduleCombo;

	private JComboBox templateCombo;

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
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 1));
		buttonPanel.add(cancelButton);
		buttonPanel.add(assertButton);
		add(buttonPanel, BorderLayout.PAGE_END);
	}

	public void setStringChannel(StringChannel channel) {
		this.channel = channel;
	}

	public void init() {
		// initialize the Panels
		initPreselectionPanel();
		// initTemplatePanel();

		showCurrentStep();
		setVisible(true);
	}

	private void initPreselectionPanel() {
		JPanel preselectionPanel = new JPanel();
		preselectionPanel.setLayout(new BoxLayout(preselectionPanel,
				BoxLayout.Y_AXIS));
		moduleCombo = new JComboBox();
		moduleCombo.addItem(new String());
		Collection modules = engine.getAgenda().getModules();
		for (Object obj : modules) {
			Module mod = (Module) obj;
			moduleCombo.addItem(mod.getModuleName());
		}
		preselectionPanel.setBorder(BorderFactory
				.createTitledBorder("Module and Template Selection"));
		preselectionPanel.add(new JLabel("Select a Module:"));
		preselectionPanel.add(moduleCombo);
		templateCombo = new JComboBox();
		templateCombo.addActionListener(this);
		initTemplateComboBox();
		preselectionPanel.add(new JLabel("Select a Template:"));
		preselectionPanel.add(templateCombo);
		contentPanel.add("moduleSelection", preselectionPanel);
	}

	private void initTemplateComboBox() {
		templateCombo.removeAllItems();
		templateCombo.addItem(new String());
		Module module = engine.getAgenda().findModule(
				moduleCombo.getSelectedItem().toString());
		if (module != null) {
			Collection templates = module.getTemplates();
			for (Object obj : templates) {
				Template tmp = (Template) obj;
				templateCombo.addItem(tmp.getName());
			}
		}
		templateCombo.repaint();
	}

	private void initTemplatePanel() {
		JPanel templatePanel = new JPanel(new FlowLayout());
		templateCombo = new JComboBox();
		Module module = engine.getAgenda().findModule(
				moduleCombo.getSelectedItem().toString());
		Collection templates = module.getTemplates();
		for (Object obj : templates) {
			Template tmp = (Template) obj;
			templateCombo.addItem(tmp.getName());
		}
		templatePanel.add(templateCombo);
		contentPanel.add("templateSelection", templatePanel);
	}

	private void initFactEditPanel() {
		JPanel factEditPanel = new JPanel(new FlowLayout());

		contentPanel.add("factEdit", factEditPanel);
	}

	private void showCurrentStep() {
		switch (step) {
		case 0:
			((CardLayout) contentPanel.getLayout()).show(contentPanel,
					"moduleSelection");
			break;
		case 1:
			((CardLayout) contentPanel.getLayout()).show(contentPanel,
					"templateSelection");
			break;
		case 2:
			initFactEditPanel();
			((CardLayout) contentPanel.getLayout()).show(contentPanel,
					"factEdit");
			break;
		}
	}

	public void actionPerformed(ActionEvent event) {
		if (event.getSource().equals(assertButton)) {
			// TODO do assertion
			StringBuilder buffer = new StringBuilder();
			channel.executeCommand(buffer.toString(), true);
		} else if (event.getSource().equals(cancelButton)) {
			close();
		}
		else if( event.getSource().equals(moduleCombo)) {
			System.out.println("bla");
			initTemplateComboBox();
		}
	}
}
