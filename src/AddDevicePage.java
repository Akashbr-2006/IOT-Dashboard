import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.security.SecureRandom;

public class AddDevicePage {

    private JLabel valId;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MyDevicesPage listPageRef; 

    public AddDevicePage(CardLayout cardLayout, JPanel mainPanel, MyDevicesPage listPageRef) {
        this.cardLayout = cardLayout;
        this.mainPanel = mainPanel;
        this.listPageRef = listPageRef;
    }

    public JPanel getPanel() {
        JPanel panel = new JPanel(new GridBagLayout()); 
        panel.setBackground(StyleUtils.CONTENT_BG);

        
        ModernShadowPanel formCard = new ModernShadowPanel();
        formCard.setLayout(new GridLayout(8, 1, 10, 10));
        formCard.setBorder(new EmptyBorder(20, 30, 20, 30)); 
        formCard.setPreferredSize(new Dimension(450, 550)); 

        
        JLabel title = new JLabel("New Hardware", StyleUtils.loadIcon("add.png"), SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(StyleUtils.DARK_BG);
        title.setIconTextGap(10);
        formCard.add(title);

        
        JPanel idPanel = new JPanel(new BorderLayout());
        idPanel.setOpaque(false);
        JLabel lblId = new JLabel("Auto-Generated API Key");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblId.setForeground(Color.GRAY);
        
        valId = new JLabel("PENDING..."); 
        valId.setFont(new Font("Monospaced", Font.BOLD, 14));
        valId.setForeground(Color.LIGHT_GRAY);
        
        idPanel.add(lblId, BorderLayout.NORTH);
        idPanel.add(valId, BorderLayout.CENTER);
        formCard.add(idPanel);

        
        JTextField nameField = new JTextField(); 
        formCard.add(createModernInput("Device Name", nameField));

        
        JPanel catPanel = new JPanel(new GridLayout(2, 1));
        catPanel.setOpaque(false);
        JLabel catLabel = new JLabel("I/O Configuration");
        catLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        catLabel.setForeground(Color.GRAY);
        
        JPanel radioGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        radioGroup.setOpaque(false);
        JRadioButton rbInput = new JRadioButton("Input (Sensor)");
        JRadioButton rbOutput = new JRadioButton("Output (Actuator)");
        rbInput.setOpaque(false);
        rbOutput.setOpaque(false);
        rbInput.setSelected(true); 
        
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbInput);
        bg.add(rbOutput);
        radioGroup.add(rbInput);
        radioGroup.add(rbOutput);
        catPanel.add(catLabel);
        catPanel.add(radioGroup);
        formCard.add(catPanel);

        
        JPanel typePanel = new JPanel(new BorderLayout(0, 5));
        typePanel.setOpaque(false);
        JLabel typeLabel = new JLabel("Signal Type");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typeLabel.setForeground(Color.GRAY);
        String[] types = { "Digital (0/1)", "Analog (Range)" };
        JComboBox<String> typeBox = new JComboBox<>(types);
        typeBox.setBackground(Color.WHITE);
        typePanel.add(typeLabel, BorderLayout.NORTH);
        typePanel.add(typeBox, BorderLayout.CENTER);
        formCard.add(typePanel);

        
        
        JButton saveBtn = new JButton("INITIALIZE DEVICE") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, StyleUtils.DARK_BG, getWidth(), 0, new Color(60, 64, 80));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        saveBtn.setForeground(Color.CYAN); 
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setContentAreaFilled(false);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        saveBtn.addActionListener(e -> {
            // 1. Validation
            if(nameField.getText().trim().isEmpty()) return;

            // 2. Generate Data
            String newID = generateSecureID();
            String mode = rbInput.isSelected() ? "INPUT" : "OUTPUT";
            String signal = (String) typeBox.getSelectedItem();
            
            // 3. Create Object
            Device newDevice = new Device(newID, nameField.getText(), mode, signal);
            
            // 4. Save to Local Memory (For the List View)
            DeviceManager.addDevice(newDevice);
            
            // 5. ✅ SAVE TO FIREBASE CLOUD (The new line)
            FirebaseHandler.saveDevice(newDevice);
            
            // 6. Visual Feedback
            valId.setText(newID);
            valId.setForeground(new Color(0, 180, 100));
            
            Timer t = new Timer(1000, x -> {
                 listPageRef.refreshList();
                 cardLayout.show(mainPanel, "List");
            });
            t.setRepeats(false);
            t.start();
        });

        formCard.add(new JLabel(" ")); 
        formCard.add(saveBtn);

        panel.add(formCard);
        return panel;
    }

    
    private JPanel createModernInput(String title, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 5)); 
        p.setOpaque(false);
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.GRAY);
        
        field.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY)); // Underline only
        field.setBackground(new Color(0,0,0,0)); // Transparent bg
        field.setOpaque(false);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private String generateSecureID() {
        String CHARS = "ABCDEF0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 8; i++) sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        return sb.toString();
    }
}