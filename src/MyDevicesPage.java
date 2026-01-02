import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MyDevicesPage {

    private JPanel gridPanel; 
    private ControlPanelPage controlPageRef;
    private CardLayout cardLayout;
    private JPanel mainContainer;


    public MyDevicesPage(CardLayout layout, JPanel container, ControlPanelPage controlPage) {
        this.cardLayout = layout;
        this.mainContainer = container;
        this.controlPageRef = controlPage; 
    }
    
 
    public void setControlPage(ControlPanelPage cp) {
        this.controlPageRef = cp;
    }
    public JPanel getPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(StyleUtils.CONTENT_BG);
        wrapper.setBorder(new EmptyBorder(30, 30, 30, 30));

        
        JLabel title = new JLabel("My Devices");
        title.setFont(StyleUtils.HEADER_FONT);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));
        wrapper.add(title, BorderLayout.NORTH);

        
        gridPanel = new JPanel(new GridLayout(0, 1, 15, 15)); 
        gridPanel.setBackground(StyleUtils.CONTENT_BG);

        
        JPanel northContainer = new JPanel(new BorderLayout());
        northContainer.setBackground(StyleUtils.CONTENT_BG);
        northContainer.add(gridPanel, BorderLayout.NORTH);

        
        JScrollPane scroll = new JScrollPane(northContainer); 
        scroll.setBorder(null);
        scroll.setBackground(StyleUtils.CONTENT_BG);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        wrapper.add(scroll, BorderLayout.CENTER);

        
        scroll.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int availableWidth = scroll.getViewport().getWidth();
                int cardWidth = 230; // Card width (220) + Gap (10)
                
                
                int columns = Math.max(1, availableWidth / cardWidth);
                
                
                GridLayout g = (GridLayout) gridPanel.getLayout();
                if (g.getColumns() != columns) {
                    g.setColumns(columns);
                    gridPanel.revalidate();
                }
            }
        });

        refreshList(); 
        return wrapper;
    }

    public void refreshList() {
        gridPanel.removeAll(); 
        List<Device> devices = DeviceManager.getAllDevices();

        if (devices.isEmpty()) {
            
            JLabel emptyLabel = new JLabel("No Devices Connected");
            emptyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setBorder(new EmptyBorder(50, 0, 0, 0));
            gridPanel.add(emptyLabel);
        } else {
            for (Device d : devices) {
                gridPanel.add(createDeviceCard(d));
            }
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createDeviceCard(Device d) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        
        
        card.setPreferredSize(new Dimension(220, 110)); 
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        
        JLabel iconLabel = new JLabel(StyleUtils.loadIcon("settings.png")); 
        iconLabel.setBorder(new EmptyBorder(0, 15, 0, 10));
        card.add(iconLabel, BorderLayout.WEST);

        
        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(new EmptyBorder(15, 0, 15, 10));

        JLabel nameLabel = new JLabel(d.getName());
        nameLabel.setFont(StyleUtils.LABEL_FONT);
        
        JLabel statusLabel = new JLabel(d.getType());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusLabel.setForeground(Color.GRAY);
        
        textPanel.add(nameLabel);
        textPanel.add(statusLabel);
        card.add(textPanel, BorderLayout.CENTER);

        
        JPanel statusStrip = new JPanel();
        statusStrip.setPreferredSize(new Dimension(5, 0));
        statusStrip.setBackground(d.getValue() > 0 ? new Color(0, 200, 100) : new Color(200, 200, 200));
        card.add(statusStrip, BorderLayout.EAST);

        
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                controlPageRef.loadDevice(d); 
                cardLayout.show(mainContainer, "Control"); 
            }
            public void mouseEntered(MouseEvent e) { 
                card.setBackground(new Color(245, 250, 255));
                textPanel.setBackground(new Color(245, 250, 255));
            }
            public void mouseExited(MouseEvent e) { 
                card.setBackground(Color.WHITE); 
                textPanel.setBackground(Color.WHITE);
            }
        });

        return card;
    }
    
}