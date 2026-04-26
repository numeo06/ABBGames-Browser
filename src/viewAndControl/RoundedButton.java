package viewAndControl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Custom JButton class with rounded corners and hover effects.
 *  This button overrides the default Swing painting behavior to render
 *  a rounded rectangle background and changes color when the mouse hovers over it.
 */
public class RoundedButton extends JButton
{
    /** Track if mouse is over the button */
    private boolean hover = false;

    /**
     * Constructs a RoundedButton with the specified label and size.
     *
     * @param text  the text displayed on the button
     * @param width the preferred width of the button
     * @param height the preferred height of the button
     */
    public RoundedButton(String text, int width, int height)
    {
        super(text);
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setFont(new Font("Arial Black", Font.BOLD, 14));
        setForeground(GUIColors.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(width, height));

        //Mouse listener to trigger hover visual changes
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e)
            {
                hover = true;
                repaint();
            }
            @Override
            public void mouseExited(MouseEvent e)
            {
                hover = false;
                repaint();
            }
        });
    }
    /**
     * Paints the custom rounded button background and then delegates
     * to the superclass for text rendering.
     * The button background changes color depending on whether the
     * mouse is hovering over it.
     *
     * @param g the Graphics context used for painting
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hover ? GUIColors.MID : GUIColors.DARK);
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
        g2.dispose();
        super.paintComponent(g);
    }
}