import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

public class ModernToggleSwitch extends JPanel {

    private boolean isOn = false;
    private Color switchColor = new Color(200, 200, 200); 
    private Color bgColor = new Color(50, 50, 50);        
    
    
    private Timer timer;
    private int knobX = 5; 
    private final int PADDING = 5;
    
    private Consumer<Boolean> onToggleAction; 

    public ModernToggleSwitch() {
        setPreferredSize(new Dimension(100, 40)); 
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle();
            }
        });
    }

    public void setOn(boolean on) {
        this.isOn = on;
        int width = getWidth() > 0 ? getWidth() : 100;
        int height = getHeight() > 0 ? getHeight() : 40;
        int knobSize = height - (PADDING * 2);
        
        if (isOn) {
            knobX = width - knobSize - PADDING;
            bgColor = Color.WHITE;
            switchColor = StyleUtils.ACCENT; 
        } else {
            knobX = PADDING;
            bgColor = new Color(50, 50, 50); 
            switchColor = new Color(200, 200, 200); 
        }
        repaint();
    }

    public void setOnToggleAction(Consumer<Boolean> action) {
        this.onToggleAction = action;
    }

    private void toggle() {
        isOn = !isOn;
        animateSwitch();
        if (onToggleAction != null) {
            onToggleAction.accept(isOn);
        }
    }

    private void animateSwitch() {
        if (timer != null && timer.isRunning()) timer.stop();

        int width = getWidth();
        int height = getHeight();
        int knobSize = height - (PADDING * 2);
        
        // Target positions
        int targetX = isOn ? (width - knobSize - PADDING) : PADDING;
        
        timer = new Timer(5, e -> {
            int speed = 4; 
            
            
            if (knobX < targetX) {
                knobX += speed;
                if (knobX >= targetX) { knobX = targetX; ((Timer)e.getSource()).stop(); }
            } else if (knobX > targetX) {
                knobX -= speed;
                if (knobX <= targetX) { knobX = targetX; ((Timer)e.getSource()).stop(); }
            }
            
            
            if (isOn) {
                bgColor = Color.WHITE;
                switchColor = StyleUtils.ACCENT;
            } else {
                bgColor = new Color(50, 50, 50);
                switchColor = new Color(200, 200, 200);
            }
            
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int knobSize = h - (PADDING * 2);

        
        g2.setColor(bgColor);
        g2.fillRoundRect(0, 0, w, h, h, h); 

        
        g2.setColor(new Color(100, 100, 100));
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, w-1, h-1, h, h);

        
        g2.setColor(switchColor);
        g2.fillOval(knobX, PADDING, knobSize, knobSize);
        
        
        g2.setColor(new Color(0,0,0,50));
        g2.drawOval(knobX, PADDING, knobSize, knobSize);
    }
}