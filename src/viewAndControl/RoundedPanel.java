package viewAndControl;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class RoundedPanel extends JPanel {
    private final int radius;
    private Color background;
    /**
     * Constructs a rounded panel.
     *
     * @param radius     the corner radius
     * @param background the background color
     */
    public RoundedPanel(int radius, Color background) {
        this.radius = radius;
        this.background = background;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(background);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 4, getHeight() - 4, radius, radius));
        g2.dispose();
        super.paintComponent(g);
    }

    /**
     * Sets background color of panel.
     *
     * @param background the desired background <code>Color</code>
     */
    @Override
    public void setBackground(Color background)
    {
        this.background = background;
    }
}
