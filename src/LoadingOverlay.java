import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class LoadingOverlay extends JPanel {

    private JLabel statusLabel;
    private JPanel buttonPanel;
    private JButton btnRetry, btnOffline, btnExit;
    private JProgressBar progressBar; 

    public LoadingOverlay() {
        setLayout(new GridBagLayout()); 
        setOpaque(false); 

        
        JPanel centerBox = new ModernShadowPanel(); 
        centerBox.setLayout(new BoxLayout(centerBox, BoxLayout.Y_AXIS));
        centerBox.setBackground(Color.WHITE);
        centerBox.setPreferredSize(new Dimension(400, 280)); 
        centerBox.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        
        statusLabel = new JLabel("Starting...");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true); 
        progressBar.setPreferredSize(new Dimension(300, 25)); 
        progressBar.setForeground(new Color(0, 200, 100)); 
        progressBar.setBackground(new Color(230, 230, 230)); 
        progressBar.setBorderPainted(false);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        
        buttonPanel = new JPanel(new GridLayout(3, 1, 5, 10)); 
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.setVisible(false); 

        
        btnRetry = createButton(" Try Again", Color.BLACK);
        btnOffline = createButton(" Use Offline", Color.DARK_GRAY);
        btnExit = createButton(" Exit App", new Color(50, 50, 50)); 

        buttonPanel.add(btnRetry);
        buttonPanel.add(btnOffline);
        buttonPanel.add(btnExit);

        centerBox.add(Box.createVerticalGlue());
        centerBox.add(statusLabel);
        centerBox.add(Box.createVerticalStrut(20));
        centerBox.add(progressBar);
        centerBox.add(buttonPanel);
        centerBox.add(Box.createVerticalGlue());

        add(centerBox);
        addMouseListener(new java.awt.event.MouseAdapter() {});
    }

    public void updateProgress(int percent, String message) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setValue(percent);
            statusLabel.setText(message);
        });
    }

    public void showLoading() {
        setVisible(true);
        progressBar.setVisible(true);
        buttonPanel.setVisible(false);
        updateProgress(0, "Initializing...");
        repaint();
    }

    public void showError(ActionListener onRetry, ActionListener onOffline, ActionListener onExit) {
        statusLabel.setText("Connection Failed");
        statusLabel.setForeground(Color.RED);
        progressBar.setVisible(false); 
        buttonPanel.setVisible(true); 
        
        for(ActionListener al : btnRetry.getActionListeners()) btnRetry.removeActionListener(al);
        for(ActionListener al : btnOffline.getActionListeners()) btnOffline.removeActionListener(al);
        for(ActionListener al : btnExit.getActionListeners()) btnExit.removeActionListener(al);

        btnRetry.addActionListener(onRetry);
        btnOffline.addActionListener(onOffline);
        btnExit.addActionListener(onExit);
        
        revalidate();
        repaint();
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        
        btn.setFocusPainted(false);
        btn.setBorderPainted(false); 
        btn.setOpaque(true); 
        btn.setContentAreaFilled(true); 
        
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 35));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(255, 255, 255, 200)); 
        g2.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }
}