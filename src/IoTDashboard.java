import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class IoTDashboard {

    private static CardLayout cardLayout = new CardLayout();
    private static JPanel mainContentPanel = new JPanel();
    private static JPanel sidebarPanel;
    private static boolean isSidebarExpanded = true;
    private static javax.swing.Timer sidebarTimer; 
    private static JButton currentActiveBtn = null;
    private static Map<String, JButton> navButtons = new HashMap<>();
    private static AutomationPage autoPageRef;
    private static JPanel homeCardContainer;
    private static ControlPanelPage controlPageRef;

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        JFrame frame = new JFrame("Student Smart Home System");
        // Ensure StyleUtils.java is updated and saved!
        ImageIcon appIcon = StyleUtils.loadIcon("iot.png");
        if (appIcon != null) frame.setIconImage(appIcon.getImage());

        frame.setSize(1200, 750);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        JPanel header = createHeader();

        mainContentPanel.setLayout(cardLayout);
        mainContentPanel.add(createHomePanel(), "Home");

        MyDevicesPage listPage = new MyDevicesPage(cardLayout, mainContentPanel, null);
        controlPageRef = new ControlPanelPage(cardLayout, mainContentPanel, listPage);
        listPage.setControlPage(controlPageRef);

        AddDevicePage addPage = new AddDevicePage(cardLayout, mainContentPanel, listPage);
        autoPageRef = new AutomationPage();

        mainContentPanel.add(listPage.getPanel(), "List");
        mainContentPanel.add(controlPageRef.getPanel(), "Control");
        mainContentPanel.add(addPage.getPanel(), "Add");
        mainContentPanel.add(autoPageRef.getPanel(), "Auto");
        mainContentPanel.add(createPlaceholder("Analytics"), "Analytics");
        mainContentPanel.add(createPlaceholder("Settings"), "Settings");

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(header, BorderLayout.NORTH);
        rightContainer.add(mainContentPanel, BorderLayout.CENTER);

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(rightContainer, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);

        LoadingOverlay overlay = new LoadingOverlay();
        frame.setGlassPane(overlay);
        overlay.setVisible(true);
        frame.setVisible(true);

        startConnectionProcess(overlay, listPage);
    }

    // --- Polling Logic ---
    private static void startPollingLoop() {
        System.out.println("Starting Polling Loop (No JARs required)...");
        
        java.util.Timer pollingTimer = new java.util.Timer(true);
        pollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Check all Analog/Input devices for changes
                List<Device> devices = DeviceManager.getAllDevices();
                boolean updated = false;

                for (Device d : devices) {
                    // Only poll sensors (Inputs) to save bandwidth
                    if (d.getCategory().equalsIgnoreCase("INPUT") || d.getName().toLowerCase().contains("lidar")) {
                        
                        // NOTE: ensure FirebaseHandler.java is updated with getDeviceValue() method
                        int cloudVal = FirebaseHandler.getDeviceValue(d.getId());
                        
                        // If value changed
                        if (cloudVal != -1 && cloudVal != d.getValue()) {
                            d.setValue(cloudVal);
                            updated = true;
                            System.out.println("POLLING: Update detected for " + d.getName() + " -> " + cloudVal);
                        }
                    }
                }

                if (updated) {
                    // 1. Run Automation Rules
                    // Ensure AutomationPage.java has getRules() method
                    AutomationEngine.checkRules(AutomationPage.getRules());

                    // 2. Refresh UI
                    SwingUtilities.invokeLater(() -> {
                        refreshHomeCards();
                        if (controlPageRef != null && controlPageRef.getCurrentDevice() != null) {
                            controlPageRef.refreshState();
                        }
                    });
                }
            }
        }, 2000, 2000); // Check every 2 seconds
    }

    public static void navigateTo(String pageName) {
        cardLayout.show(mainContentPanel, pageName);
        if (pageName.equals("Home")) {
            refreshHomeCards();
        }
        if (pageName.equals("Auto") && autoPageRef != null) {
            autoPageRef.refreshDropdowns();
        }

        JButton targetBtn = navButtons.get(pageName);
        if (targetBtn != null) {
            if (currentActiveBtn != null) {
                currentActiveBtn.setBackground(StyleUtils.DARK_BG);
                currentActiveBtn.setForeground(Color.LIGHT_GRAY);
            }
            targetBtn.setBackground(StyleUtils.HIGHLIGHT);
            targetBtn.setForeground(Color.WHITE);
            currentActiveBtn = targetBtn;
        }
    }

    public static void refreshHomeCards() {
        if (homeCardContainer == null) return;
        homeCardContainer.removeAll();
        List<Device> allDevices = DeviceManager.getAllDevices();
        boolean foundPinned = false;

        for (Device d : allDevices) {
            if (d.isPinned()) {
                foundPinned = true;
                String displayVal;
                if (d.getType().contains("Analog") || d.getName().toLowerCase().contains("lidar")) {
                     displayVal = d.getValue() + " cm"; 
                } else {
                     displayVal = (d.getValue() == 1 ? "ON" : "OFF");
                }
                
                Color cardColor = d.getCategory().equalsIgnoreCase("INPUT") ? new Color(100, 150, 255) : new Color(100, 200, 100);
                homeCardContainer.add(createSensorCard(d, displayVal, cardColor));
            }
        }

        if (!foundPinned) {
            JLabel emptyMsg = new JLabel("No pinned devices. Go to 'My Devices' -> 'Control' to pin one!", SwingConstants.CENTER);
            emptyMsg.setForeground(Color.GRAY);
            homeCardContainer.add(emptyMsg);
        }

        homeCardContainer.revalidate();
        homeCardContainer.repaint();
    }

    private static JPanel createHomePanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(StyleUtils.CONTENT_BG);
        homeCardContainer = new JPanel(new GridLayout(0, 2, 20, 20)); 
        homeCardContainer.setBackground(StyleUtils.CONTENT_BG);
        homeCardContainer.setBorder(new EmptyBorder(30, 30, 30, 30)); 
        JScrollPane scroll = new JScrollPane(homeCardContainer);
        scroll.setBorder(null);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    private static JPanel createSensorCard(Device d, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                controlPageRef.loadDevice(d); 
                navigateTo("Control"); 
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 255)); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });

        JPanel colorStrip = new JPanel();
        colorStrip.setBackground(color);
        colorStrip.setPreferredSize(new Dimension(10, 0));
        card.add(colorStrip, BorderLayout.WEST);
        
        JPanel content = new JPanel(new GridLayout(2, 1));
        content.setOpaque(false); 
        content.setBorder(new EmptyBorder(15, 20, 15, 20)); 
        
        JLabel titleLabel = new JLabel(d.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        valueLabel.setForeground(StyleUtils.DARK_BG);
        
        content.add(titleLabel);
        content.add(valueLabel);
        card.add(content, BorderLayout.CENTER);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
        return card;
    }

    private static JPanel createSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(StyleUtils.DARK_BG);
        sidebarPanel.setPreferredSize(new Dimension(240, 700));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 20));
        topPanel.setBackground(StyleUtils.DARK_BG);
        JButton toggleBtn = new JButton();
        toggleBtn.setIcon(StyleUtils.loadIcon("menu.png")); 
        toggleBtn.setContentAreaFilled(false);
        toggleBtn.setBorderPainted(false);
        toggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleBtn.addActionListener(e -> toggleSidebar(topPanel));
        JLabel appTitle = new JLabel("Menu");
        appTitle.setForeground(StyleUtils.ACCENT);
        appTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        topPanel.add(toggleBtn);
        topPanel.add(appTitle);
        
        JPanel menuContainer = new JPanel(new GridLayout(8, 1, 5, 5));
        menuContainer.setBackground(StyleUtils.DARK_BG);
        
        menuContainer.add(createNavButton("Home", "home.png", "Home"));
        menuContainer.add(createNavButton("Add Device", "add.png", "Add"));
        menuContainer.add(createNavButton("My Devices", "list.png", "List"));
        menuContainer.add(createNavButton("Automation", "auto.png", "Auto"));
        menuContainer.add(createNavButton("Analytics", "analytics.png", "Analytics"));
        menuContainer.add(createNavButton("Settings", "settings.png", "Settings"));

        sidebarPanel.add(topPanel, BorderLayout.NORTH);
        sidebarPanel.add(menuContainer, BorderLayout.CENTER);
        return sidebarPanel;
    }

    private static JButton createNavButton(String text, String iconName, String cardName) {
        JButton btn = new JButton(text);
        btn.setIcon(StyleUtils.loadIcon(iconName)); 
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        btn.setBackground(StyleUtils.DARK_BG);
        btn.setForeground(Color.LIGHT_GRAY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(0, 15, 0, 0));
        btn.setIconTextGap(15); 
        btn.putClientProperty("FullText", text);
        navButtons.put(cardName, btn);
        btn.addActionListener(e -> navigateTo(cardName));
        return btn;
    }

    private static JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(800, 65)); 
        header.setBackground(StyleUtils.DARK_BG);      
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, StyleUtils.ACCENT)); 

        JLabel title = new JLabel("Smart Home Dashboard");
        title.setFont(StyleUtils.HEADER_FONT);
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 20, 0, 0)); 
        header.add(title, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 12));
        rightPanel.setBackground(StyleUtils.DARK_BG); 
        
        // This relies on createIconBtn defined below
        rightPanel.add(createIconBtn("bell.png"));      
        
        header.add(rightPanel, BorderLayout.EAST);
        return header;
    }

    private static void startConnectionProcess(LoadingOverlay overlay, MyDevicesPage listPage) {
        new Thread(() -> {
            try {
                overlay.updateProgress(10, "Connecting...");
                boolean success = DeviceManager.syncWithCloud();
                
                if (success) {
                    overlay.updateProgress(90, "Setting up Live Feed...");
                    
                    startPollingLoop(); 

                    overlay.updateProgress(100, "Done!");
                    Thread.sleep(200); 
                    SwingUtilities.invokeLater(() -> {
                        overlay.setVisible(false);
                        listPage.refreshList(); 
                        refreshHomeCards();
                    });
                } else {
                    SwingUtilities.invokeLater(() -> overlay.showError(
                        (e) -> { overlay.showLoading(); startConnectionProcess(overlay, listPage); },
                        (e) -> { overlay.setVisible(false); },
                        (e) -> { System.exit(0); }
                    ));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }).start();
    }

    private static JButton createIconBtn(String iconName) {
        JButton btn = new JButton(StyleUtils.loadIcon(iconName)); 
        btn.setBackground(null); btn.setBorder(null);
        btn.setFocusPainted(false); btn.setContentAreaFilled(false); 
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private static void toggleSidebar(JPanel topPanel) {
        if (sidebarTimer != null && sidebarTimer.isRunning()) return;
        isSidebarExpanded = !isSidebarExpanded; 
        int targetWidth = isSidebarExpanded ? 240 : 70;
        int step = isSidebarExpanded ? 10 : -10; 
        if (!isSidebarExpanded) {
             updateSidebarStyle(false); 
             topPanel.getComponent(1).setVisible(false);
        }
        sidebarTimer = new javax.swing.Timer(10, e -> {
            int currentWidth = sidebarPanel.getWidth();
            int nextWidth = currentWidth + step;
            boolean finished = (step > 0) ? (nextWidth >= targetWidth) : (nextWidth <= targetWidth);
            if (finished) {
                nextWidth = targetWidth;
                sidebarTimer.stop();      
                if (isSidebarExpanded) {
                    updateSidebarStyle(true); 
                    topPanel.getComponent(1).setVisible(true);
                }
            }
            sidebarPanel.setPreferredSize(new Dimension(nextWidth, sidebarPanel.getHeight()));
            sidebarPanel.revalidate(); sidebarPanel.repaint();
        });
        sidebarTimer.start();
    }

    private static void updateSidebarStyle(boolean showFullText) {
        JPanel menuGrid = (JPanel) ((BorderLayout)sidebarPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        for (Component c : menuGrid.getComponents()) {
            if (c instanceof JButton) {
                JButton btn = (JButton) c;
                if (showFullText) {
                    btn.setText((String) btn.getClientProperty("FullText"));
                    btn.setHorizontalAlignment(SwingConstants.LEFT);
                    btn.setBorder(new EmptyBorder(0, 15, 0, 0));
                } else {
                    btn.setText(""); btn.setHorizontalAlignment(SwingConstants.CENTER);
                    btn.setBorder(new EmptyBorder(0, 0, 0, 0));
                }
            }
        }
    }

    private static JPanel createPlaceholder(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(StyleUtils.CONTENT_BG); // Ensure StyleUtils.java is compiled
        JLabel label = new JLabel(title + " Screen (Under Construction)");
        label.setFont(new Font("Arial", Font.ITALIC, 20));
        label.setForeground(Color.GRAY);
        panel.add(label);
        return panel;
    }
}