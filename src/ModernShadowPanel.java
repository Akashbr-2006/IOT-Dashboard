import javax.swing.*;
import java.awt.*;

public class ModernShadowPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private int shadowGap = 5;
    private int shadowOffset = 4;
    private int shadowAlpha = 50; 
    private int cornerRadius = 20;
    
    
    private Color backgroundColor = Color.WHITE;

    public ModernShadowPanel() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(shadowGap, shadowGap, shadowGap + shadowOffset, shadowGap + shadowOffset));
    }

   
    @Override
    public void setBackground(Color bg) {
        super.setBackground(bg);
        this.backgroundColor = bg;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth() - (shadowGap * 2);
        int height = getHeight() - (shadowGap * 2);

    
        g2.setColor(new Color(0, 0, 0, shadowAlpha));
        g2.fillRoundRect(shadowGap + shadowOffset, shadowGap + shadowOffset, width, height, cornerRadius, cornerRadius);

        
        g2.setColor(backgroundColor);
        g2.fillRoundRect(shadowGap, shadowGap, width, height, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}