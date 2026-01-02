import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;

public class ControlPanelPage {

    private JPanel mainPanel;
    private JPanel contentArea; 
    private JLabel titleLabel;
    private Device currentDevice;
    private CardLayout cardLayout;
    private JPanel parentContainer;
    private MyDevicesPage listPageRef; 

    public ControlPanelPage(CardLayout cardLayout, JPanel parentContainer, MyDevicesPage listPageRef) {
        this.cardLayout = cardLayout;
        this.parentContainer = parentContainer;
        this.listPageRef = listPageRef;
        
        mainPanel = new BackgroundImagePanel(new BorderLayout(), "bg.png");
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        
        JButton backBtn = new JButton("← Back");
        backBtn.setFont(StyleUtils.LABEL_FONT);
        backBtn.setBackground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> {
            listPageRef.refreshList(); 
            IoTDashboard.navigateTo("List");
        });
        
        titleLabel = new JLabel("Device Control");
        titleLabel.setFont(StyleUtils.HEADER_FONT);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        topBar.add(backBtn, BorderLayout.WEST);
        topBar.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(topBar, BorderLayout.NORTH);

        contentArea = new JPanel(new GridLayout(1, 2, 20, 0)); 
        contentArea.setOpaque(false);
        mainPanel.add(contentArea, BorderLayout.CENTER);
    }

    public JPanel getPanel() { return mainPanel; }
    
    // ✅ NEW: Getter for IoTDashboard
    public Device getCurrentDevice() {
        return currentDevice;
    }

    // ✅ NEW: Refresh method for live updates
    public void refreshState() {
        if (currentDevice != null) {
            loadDevice(currentDevice);
        }
    }

    public void loadDevice(Device d) {
        this.currentDevice = d;
        titleLabel.setText(d.getName()); 
        contentArea.removeAll(); 

        Color glassWhite = new Color(255, 255, 255, 220);

        ModernShadowPanel leftPanel = new ModernShadowPanel();
        leftPanel.setBackground(glassWhite); 
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel controlTitle = new JLabel("Manual Control", SwingConstants.CENTER);
        controlTitle.setFont(StyleUtils.LABEL_FONT);
        controlTitle.setForeground(Color.GRAY);
        leftPanel.add(controlTitle, BorderLayout.NORTH);

        if (d.getCategory().equalsIgnoreCase("Input")) {
            leftPanel.add(createInputMonitor(), BorderLayout.CENTER);
        } else {
            if (d.getType().contains("Digital")) leftPanel.add(createDigitalControl(), BorderLayout.CENTER);
            else leftPanel.add(createAnalogControl(), BorderLayout.CENTER);
        }
        contentArea.add(leftPanel);

        ModernShadowPanel rightPanel = new ModernShadowPanel();
        rightPanel.setBackground(glassWhite); 
        rightPanel.setLayout(new GridBagLayout()); 
        rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.insets = new Insets(0, 0, 15, 0);     

        rightPanel.add(createIdPanel(d.getId()), gbc);

        gbc.gridy++;
        rightPanel.add(createInfoItem("Hardware Type", d.getType()), gbc);

        gbc.gridy++;
        JToggleButton pinBtn = new JToggleButton(d.isPinned() ? "★ Unpin from Home" : "☆ Pin to Home");
        pinBtn.setFont(StyleUtils.LABEL_FONT);
        pinBtn.setSelected(d.isPinned());
        pinBtn.setBackground(d.isPinned() ? new Color(255, 255, 200) : Color.WHITE);
        pinBtn.setPreferredSize(new Dimension(0, 45));
        pinBtn.setFocusPainted(false);
        pinBtn.addActionListener(e -> {
            d.setPinned(pinBtn.isSelected());
            pinBtn.setText(d.isPinned() ? "★ Unpin from Home" : "☆ Pin to Home");
            pinBtn.setBackground(d.isPinned() ? new Color(255, 255, 200) : Color.WHITE);
        });
        rightPanel.add(pinBtn, gbc);

        gbc.gridy++;
        JButton editBtn = new JButton("✎ Rename Device");
        editBtn.setFont(StyleUtils.LABEL_FONT);
        editBtn.setBackground(new Color(230, 240, 255));
        editBtn.setForeground(new Color(0, 100, 200));
        editBtn.setFocusPainted(false);
        editBtn.setPreferredSize(new Dimension(0, 45)); 
        editBtn.addActionListener(e -> showEditDialog());
        rightPanel.add(editBtn, gbc);

        gbc.gridy++;
        JButton deleteBtn = new JButton("🗑 Delete Device");
        deleteBtn.setFont(StyleUtils.LABEL_FONT);
        deleteBtn.setBackground(new Color(255, 230, 230));
        deleteBtn.setForeground(Color.RED);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setPreferredSize(new Dimension(0, 45)); 
        deleteBtn.addActionListener(e -> deleteDevice());
        rightPanel.add(deleteBtn, gbc);

        gbc.gridy++;
        gbc.weighty = 1.0; 
        rightPanel.add(new JLabel(), gbc);

        contentArea.add(rightPanel);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel createIdPanel(String id) {
        JPanel p = new JPanel(new BorderLayout(10, 0)); 
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        JPanel textP = new JPanel(new BorderLayout());
        textP.setOpaque(false);
        JLabel lbl = new JLabel("Device ID");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.GRAY);
        JLabel val = new JLabel(id);
        val.setFont(new Font("Monospaced", Font.PLAIN, 14));
        val.setForeground(StyleUtils.DARK_BG);
        textP.add(lbl, BorderLayout.NORTH);
        textP.add(val, BorderLayout.CENTER);
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 10));
        btnWrapper.setOpaque(false);
        JButton copyBtn = new JButton("Copy");
        copyBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        copyBtn.setBackground(Color.WHITE);
        copyBtn.setPreferredSize(new Dimension(60, 25)); 
        copyBtn.addActionListener(e -> {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(id), null);
            JOptionPane.showMessageDialog(mainPanel, "ID Copied!");
        });
        btnWrapper.add(copyBtn);
        p.add(textP, BorderLayout.CENTER);
        p.add(btnWrapper, BorderLayout.EAST);
        return p;
    }

    private JPanel createInfoItem(String label, String value) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY)); 
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.GRAY);
        JLabel val = new JLabel(value);
        val.setFont(new Font("Monospaced", Font.PLAIN, 14));
        val.setForeground(StyleUtils.DARK_BG);
        p.add(lbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    private JPanel createInputMonitor() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        String val = (currentDevice.getType().contains("Digital")) ? "HIGH" : (currentDevice.getValue() + "°C");
        JLabel display = new JLabel(val);
        display.setFont(new Font("Segoe UI", Font.BOLD, 50));
        display.setForeground(StyleUtils.DARK_BG);
        p.add(display);
        return p;
    }

    private JPanel createDigitalControl() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        ModernToggleSwitch toggle = new ModernToggleSwitch();
        toggle.setPreferredSize(new Dimension(140, 60));
        toggle.setOn(currentDevice.getValue() == 1);
        toggle.setOnToggleAction(isOn -> {
            int newVal = isOn ? 1 : 0;
            currentDevice.setValue(newVal);
            FirebaseHandler.updateDeviceValue(currentDevice.getId(), newVal);
        });
        p.add(toggle);
        return p;
    }

    private JPanel createAnalogControl() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel valDisplay = new JLabel(currentDevice.getValue() + "%", SwingConstants.CENTER);
        valDisplay.setFont(new Font("Segoe UI", Font.BOLD, 40));
        JSlider slider = new JSlider(0, 100, currentDevice.getValue());
        slider.setOpaque(false);
        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                currentDevice.setValue(slider.getValue());
                FirebaseHandler.updateDeviceValue(currentDevice.getId(), slider.getValue());
            }
            valDisplay.setText(slider.getValue() + "%");
        });
        p.add(valDisplay, BorderLayout.CENTER);
        p.add(slider, BorderLayout.SOUTH);
        return p;
    }

    private void showEditDialog() {
        String newName = JOptionPane.showInputDialog(mainPanel, "Enter new name:", currentDevice.getName());
        if (newName != null && !newName.trim().isEmpty()) {
            FirebaseHandler.updateDeviceName(currentDevice.getId(), newName);
            currentDevice.setName(newName);
            titleLabel.setText(newName);
        }
    }

    private void deleteDevice() {
        int confirm = JOptionPane.showConfirmDialog(mainPanel, 
            "Are you sure you want to delete " + currentDevice.getName() + "?",
            "Delete Device", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            FirebaseHandler.deleteDevice(currentDevice.getId());
            DeviceManager.removeDevice(currentDevice);
            listPageRef.refreshList();
            IoTDashboard.navigateTo("List");
        }
    }
}