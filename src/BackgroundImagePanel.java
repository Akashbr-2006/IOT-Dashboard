import javax.swing.*;
import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;

public class BackgroundImagePanel extends JPanel {
    
    private Image backgroundImage;

   
    public BackgroundImagePanel(LayoutManager layout, String imageName) {
        super(layout);
        setOpaque(false);
        
        try {
            
            String path = "icons/" + imageName;
            File imgFile = new File(path);
            if (imgFile.exists()) {
                backgroundImage = ImageIO.read(imgFile);
            } else {
                System.err.println("Background image not found at: " + imgFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            Graphics2D g2 = (Graphics2D) g;
            
            
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            

            g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            


            g2.setColor(new Color(255, 255, 255, 150));

            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}