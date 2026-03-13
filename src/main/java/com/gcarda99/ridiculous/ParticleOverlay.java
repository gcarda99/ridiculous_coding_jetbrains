package com.gcarda99.ridiculous;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * A transparent JComponent overlay drawn on top of the editor
 * to render animated particles without altering the editor content.
 */
public class ParticleOverlay extends JComponent {

    private int[] px;
    private int[] py;
    private final List<int[]> velocities;
    private final List<Color> colors;
    private float alpha = 1.0f;

    public ParticleOverlay(JComponent parent, int[] px, int[] py,
                           List<int[]> velocities, List<Color> colors) {
        this.px = px.clone();
        this.py = py.clone();
        this.velocities = velocities;
        this.colors = colors;
        setBounds(0, 0, parent.getWidth(), parent.getHeight());
        setOpaque(false);
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void updatePositions(int[] newPx, int[] newPy) {
        this.px = newPx.clone();
        this.py = newPy.clone();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        for (int i = 0; i < px.length; i++) {
            g2.setColor(colors.get(i));
            g2.fillOval(px[i] - 3, py[i] - 3, 6, 6);
        }
        g2.dispose();
    }
}
