import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AutomationPage {

    private JPanel mainPanel;
    private JPanel rulesListPanel;
    
    private static List<AutomationRule> rules = new ArrayList<>(); 
    
    private JTabbedPane typeTabs;
    private JTextField nameField;
    private JComboBox<String> triggerCombo;
    private JComboBox<String> operatorCombo;
    private JTextField valueField;
    private JTextField delayField;
    private JTextField repeatField;
    private JComboBox<String> targetCombo;
    private JPanel dynamicActionPanel; 
    private JRadioButton rbOn, rbOff;
    private JSlider actionSlider;
    private JLabel sliderValLabel;
    
    private boolean isTargetAnalog = false; 

    // ✅ NEW: Getter for the engine
    public static List<AutomationRule> getRules() {
        return rules;
    }

    public JPanel getPanel() {
        mainPanel = new BackgroundImagePanel(new BorderLayout(), "bg.png");
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Smart Automation");
        title.setFont(StyleUtils.HEADER_FONT);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);
        
        ModernShadowPanel listCard = new ModernShadowPanel();
        listCard.setBackground(new Color(255, 255, 255, 200));
        listCard.setLayout(new BorderLayout());
        
        JLabel listTitle = new JLabel("Active Tasks", SwingConstants.CENTER);
        listTitle.setFont(StyleUtils.LABEL_FONT);
        listTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        listCard.add(listTitle, BorderLayout.NORTH);

        rulesListPanel = new JPanel();
        rulesListPanel.setLayout(new BoxLayout(rulesListPanel, BoxLayout.Y_AXIS));
        rulesListPanel.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(rulesListPanel);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        listCard.add(scroll, BorderLayout.CENTER);

        content.add(listCard);

        ModernShadowPanel createCard = new ModernShadowPanel();
        createCard.setBackground(new Color(255, 255, 255, 240)); 
        createCard.setLayout(new BorderLayout());
        
        initCreatorForm(createCard);
        content.add(createCard);

        mainPanel.add(content, BorderLayout.CENTER);
        return mainPanel;
    }

    private void initCreatorForm(JPanel p) {
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        JLabel title = new JLabel("Create New Task");
        title.setFont(StyleUtils.LABEL_FONT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        nameField = new JTextField("My Rule");
        nameField.setBorder(BorderFactory.createTitledBorder("Task Name"));
        
        header.add(title);
        header.add(nameField);
        p.add(header, BorderLayout.NORTH);

        typeTabs = new JTabbedPane();
        typeTabs.addTab("If/Then", createConditionPanel());
        typeTabs.addTab("Delay Timer", createDelayPanel());
        typeTabs.addTab("Repeat Loop", createRepeatPanel());
        p.add(typeTabs, BorderLayout.CENTER);

        p.add(createActionPanel(), BorderLayout.SOUTH);
        
        refreshDropdowns();
    }

    private JPanel createConditionPanel() {
        JPanel p = new JPanel(new GridLayout(4, 1, 5, 5));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 20, 10, 20));

        p.add(new JLabel("IF Device..."));
        triggerCombo = new JComboBox<>();
        p.add(triggerCombo);

        p.add(new JLabel("Compare Value:"));
        JPanel logicRow = new JPanel(new BorderLayout(5, 0));
        logicRow.setOpaque(false);
        
        String[] ops = { "Greater Than (>)", "Less Than (<)", "Equals (=)" };
        operatorCombo = new JComboBox<>(ops);
        valueField = new JTextField("1");
        
        logicRow.add(operatorCombo, BorderLayout.CENTER);
        logicRow.add(valueField, BorderLayout.EAST);
        valueField.setPreferredSize(new Dimension(50, 0));
        
        p.add(logicRow);
        return p;
    }

    private JPanel createDelayPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel("Wait (Seconds):");
        delayField = new JTextField("60");
        delayField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        delayField.setHorizontalAlignment(SwingConstants.CENTER);
        
        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.gridy=0; g.insets = new Insets(10, 10, 10, 10);
        p.add(lbl, g);
        g.gridy++;
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(delayField, g);
        return p;
    }
    
    private JPanel createRepeatPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        JLabel lbl = new JLabel("Repeat Every (Seconds):");
        repeatField = new JTextField("30");
        repeatField.setFont(new Font("Segoe UI", Font.BOLD, 18));
        repeatField.setHorizontalAlignment(SwingConstants.CENTER);
        
        GridBagConstraints g = new GridBagConstraints();
        g.gridx=0; g.gridy=0; g.insets = new Insets(10, 10, 10, 10);
        p.add(lbl, g);
        g.gridy++;
        g.fill = GridBagConstraints.HORIZONTAL;
        p.add(repeatField, g);
        return p;
    }

    private JPanel createActionPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setOpaque(false);
        top.add(new JLabel("THEN Set Device..."));
        targetCombo = new JComboBox<>();
        
        targetCombo.addActionListener(e -> updateDynamicControls());
        
        top.add(targetCombo);
        p.add(top, BorderLayout.NORTH);

        dynamicActionPanel = new JPanel(new BorderLayout());
        dynamicActionPanel.setOpaque(false);
        dynamicActionPanel.setBorder(BorderFactory.createTitledBorder("Action Settings"));
        p.add(dynamicActionPanel, BorderLayout.CENTER);

        JButton saveBtn = new JButton("SAVE AUTOMATION");
        saveBtn.setBackground(StyleUtils.DARK_BG);
        saveBtn.setForeground(Color.CYAN);
        saveBtn.addActionListener(e -> saveRule());
        p.add(saveBtn, BorderLayout.SOUTH);
        
        return p;
    }

    private void updateDynamicControls() {
        if (targetCombo.getSelectedItem() == null) return;
        
        String raw = (String) targetCombo.getSelectedItem();
        
        if (raw.contains("No Devices")) {
            dynamicActionPanel.removeAll();
            dynamicActionPanel.revalidate();
            dynamicActionPanel.repaint();
            return;
        }

        String id = raw.substring(raw.lastIndexOf("(") + 1, raw.lastIndexOf(")"));
        Device d = findDeviceById(id);
        
        dynamicActionPanel.removeAll(); 

        if (d != null && d.getType().contains("Analog")) {
            isTargetAnalog = true;
            actionSlider = new JSlider(0, 100, 50);
            actionSlider.setOpaque(false);
            sliderValLabel = new JLabel("50%", SwingConstants.CENTER);
            actionSlider.addChangeListener(e -> sliderValLabel.setText(actionSlider.getValue() + "%"));
            dynamicActionPanel.add(sliderValLabel, BorderLayout.NORTH);
            dynamicActionPanel.add(actionSlider, BorderLayout.CENTER);
        } else {
            isTargetAnalog = false;
            JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            radioPanel.setOpaque(false);
            
            rbOn = new JRadioButton("Turn ON"); 
            rbOff = new JRadioButton("Turn OFF");
            rbOn.setOpaque(false); rbOff.setOpaque(false);
            rbOn.setSelected(true);
            
            ButtonGroup bg = new ButtonGroup(); 
            bg.add(rbOn); bg.add(rbOff);
            
            radioPanel.add(rbOn); radioPanel.add(rbOff);
            dynamicActionPanel.add(radioPanel, BorderLayout.CENTER);
        }
        
        dynamicActionPanel.revalidate();
        dynamicActionPanel.repaint();
    }

    private Device findDeviceById(String id) {
        for(Device d : DeviceManager.getAllDevices()) {
            if(d.getId().equals(id)) return d;
        }
        return null;
    }

    public void refreshDropdowns() {
        if(triggerCombo == null) return;
        triggerCombo.removeAllItems();
        targetCombo.removeAllItems();
        
        List<Device> allDevices = DeviceManager.getAllDevices();
        if (allDevices.isEmpty()) {
            triggerCombo.addItem("No Devices");
            targetCombo.addItem("No Devices");
            return;
        }

        for (Device d : allDevices) {
            String item = d.getName() + " (" + d.getId() + ")";
            triggerCombo.addItem(item); 
            if (!d.getCategory().equalsIgnoreCase("Input")) {
                targetCombo.addItem(item); 
            }
        }
        updateDynamicControls();
    }

    private void saveRule() {
        try {
            int tabIndex = typeTabs.getSelectedIndex();
            String name = nameField.getText();
            String targetRaw = (String) targetCombo.getSelectedItem();
            String targetId = targetRaw.substring(targetRaw.lastIndexOf("(") + 1, targetRaw.lastIndexOf(")"));
            
            int actionVal = 0;
            if (isTargetAnalog) {
                actionVal = actionSlider.getValue();
            } else {
                actionVal = rbOn.isSelected() ? 1 : 0;
            }
            
            AutomationRule rule = null;

            if (tabIndex == 0) { 
                String trigRaw = (String) triggerCombo.getSelectedItem();
                String trigId = trigRaw.substring(trigRaw.lastIndexOf("(") + 1, trigRaw.lastIndexOf(")"));
                int thresh = Integer.parseInt(valueField.getText());
                String op = ">";
                if (operatorCombo.getSelectedIndex() == 1) op = "<";
                if (operatorCombo.getSelectedIndex() == 2) op = "=";
                rule = new AutomationRule(name, trigId, op, thresh, targetId, actionVal, 0, false, 0);
            
            } else if (tabIndex == 1) { 
                int sec = Integer.parseInt(delayField.getText());
                rule = new AutomationRule(name, "", "", 0, targetId, actionVal, sec, false, 0);
            
            } else if (tabIndex == 2) { 
                int sec = Integer.parseInt(repeatField.getText());
                rule = new AutomationRule(name, "", "", 0, targetId, actionVal, 0, true, sec);
            }
            
            rules.add(rule);
            addRuleToUI(rule);
            JOptionPane.showMessageDialog(mainPanel, "Rule Created!");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel, "Check your inputs!");
        }
    }

    private void addRuleToUI(AutomationRule r) {
        JPanel card = new JPanel(new BorderLayout());
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            new EmptyBorder(10, 5, 10, 5)
        ));
        
        JLabel name = new JLabel(r.getName());
        name.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel desc = new JLabel("<html>" + r.getDescription() + "</html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        desc.setForeground(Color.DARK_GRAY);
        
        JPanel textP = new JPanel(new GridLayout(2, 1));
        textP.setOpaque(false);
        textP.add(name);
        textP.add(desc);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        btnP.setOpaque(false);
        
        JToggleButton toggle = new JToggleButton(r.isActive() ? "ON" : "OFF");
        toggle.setPreferredSize(new Dimension(55, 25));
        toggle.setBackground(r.isActive() ? new Color(0, 200, 100) : Color.LIGHT_GRAY);
        toggle.setForeground(Color.WHITE);
        toggle.addActionListener(e -> {
            boolean state = toggle.isSelected();
            r.setActive(state);
            toggle.setText(state ? "ON" : "OFF");
            toggle.setBackground(state ? new Color(0, 200, 100) : Color.LIGHT_GRAY);
            name.setForeground(state ? Color.BLACK : Color.GRAY); 
        });
        
        JButton del = new JButton("x");
        del.setPreferredSize(new Dimension(30, 25));
        del.setBackground(new Color(255, 200, 200));
        del.setForeground(Color.RED);
        del.setBorder(null);
        del.addActionListener(e -> {
            rules.remove(r);
            rulesListPanel.remove(card);
            rulesListPanel.revalidate();
            rulesListPanel.repaint();
        });

        btnP.add(toggle);
        btnP.add(del);

        card.add(textP, BorderLayout.CENTER);
        card.add(btnP, BorderLayout.EAST);
        
        rulesListPanel.add(card);
        rulesListPanel.revalidate();
        rulesListPanel.repaint();
    }
}