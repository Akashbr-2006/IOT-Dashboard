import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class StyleUtils {
    
    public static final Color DARK_BG = new Color(40, 44, 52);
    public static final Color HIGHLIGHT = new Color(65, 70, 80);
    public static final Color ACCENT = Color.CYAN;
    public static final Color CONTENT_BG = new Color(245, 245, 250);

    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static ImageIcon loadIcon(String iconName) {
        try {
            URL imgUrl = StyleUtils.class.getResource("/icons/" + iconName);
            if (imgUrl == null) {
                return null;
            }
            ImageIcon icon = new ImageIcon(imgUrl);
            Image img = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Icon createAvatarIcon() {
        int size = 32;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(100, 100, 100)); 
        g2.fillOval(0, 0, size, size);
        g2.setColor(Color.WHITE);
        g2.fillOval((size - 10)/2, 6, 10, 10); 
        g2.fillArc((size - 18)/2, 18, 18, 20, 0, 180); 
        g2.dispose();
        return new ImageIcon(img);
    }
}