package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * Renders the typed character/key near the caret in a pixel-art style,
 * then floats it upward and fades out — inspired by blip.gd label rendering.
 */
public class KeyLabelEffect {

    private static final Random RANDOM = new Random();
    private static final int ANIMATION_STEPS = 30;
    private static final int FLOAT_SPEED = 2; // pixels per frame upward

    private static final Color[] LABEL_COLORS = {
        new Color(255, 100, 100),
        new Color(255, 220, 60),
        new Color(100, 220, 255),
        new Color(180, 100, 255),
        new Color(100, 255, 160),
        new Color(255, 160, 60)
    };

    public static void trigger(Editor editor, int offset, String keyText) {
        if (keyText == null || keyText.isEmpty()) return;
        SwingUtilities.invokeLater(() -> doTrigger(editor, offset, keyText));
    }

    private static void doTrigger(Editor editor, int offset, String keyText) {
        JComponent editorComponent = editor.getContentComponent();
        JRootPane rootPane = SwingUtilities.getRootPane(editorComponent);
        if (rootPane == null) return;

        JLayeredPane layeredPane = rootPane.getLayeredPane();
        VisualPosition visualPos = editor.offsetToVisualPosition(offset);
        Point caretInEditor = editor.visualPositionToXY(visualPos);
        Point caretInLayered = SwingUtilities.convertPoint(editorComponent, caretInEditor, layeredPane);

        Color labelColor = LABEL_COLORS[RANDOM.nextInt(LABEL_COLORS.length)];

        // Start slightly above and to the right of the caret
        int startX = caretInLayered.x + 6;
        int startY = caretInLayered.y - 4;

        KeyLabelOverlay overlay = new KeyLabelOverlay(layeredPane, keyText, labelColor, startX, startY);
        layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
        layeredPane.setComponentZOrder(overlay, 0);

        int[] step = {0};
        int[] currentY = {startY};

        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            if (step[0] >= ANIMATION_STEPS) {
                timer.stop();
                layeredPane.remove(overlay);
                layeredPane.repaint();
                return;
            }
            float alpha = 1.0f - (float) step[0] / ANIMATION_STEPS;
            currentY[0] -= FLOAT_SPEED;
            overlay.update(alpha, startX, currentY[0]);
            layeredPane.repaint(overlay.getBounds());
            step[0]++;
        });
        timer.start();
    }

    // Inner overlay component
    static class KeyLabelOverlay extends JComponent {

        private final String text;
        private final Color color;
        private final Font pixelFont;
        private float alpha = 1.0f;
        private int textX;
        private int textY;

        KeyLabelOverlay(JComponent parent, String text, Color color, int x, int y) {
            this.text = text;
            this.color = color;
            this.textX = x;
            this.textY = y;
            // Use a bold monospaced font to simulate pixel art style
            this.pixelFont = new Font("Monospaced", Font.BOLD, 11);
            setBounds(0, 0, parent.getWidth(), parent.getHeight());
            setOpaque(false);
        }

        void update(float alpha, int x, int y) {
            this.alpha = alpha;
            this.textX = x;
            this.textY = y;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            // Pixel art style: no antialiasing on text
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, Math.max(0f, alpha)));
            g2.setFont(pixelFont);

            // Dark shadow for readability
            g2.setColor(Color.BLACK);
            g2.drawString(text, textX + 1, textY + 1);

            // Main colored text
            g2.setColor(color);
            g2.drawString(text, textX, textY);
            g2.dispose();
        }
    }
}
