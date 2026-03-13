package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Newline: horizontal sweep of particles triggered when pressing Enter.
 */
public class NewlineEffect {

    private static final int PARTICLE_COUNT = 16;
    private static final int ANIMATION_STEPS = 25;
    private static final Random RANDOM = new Random();

    private static final Color[] COLORS = {
        new Color(100, 220, 255),
        new Color(150, 255, 200),
        new Color(200, 200, 255),
        new Color(255, 255, 150),
        new Color(180, 255, 180)
    };

    public static void trigger(Editor editor, int offset) {
        SwingUtilities.invokeLater(() -> doTrigger(editor, offset));
    }

    private static void doTrigger(Editor editor, int offset) {
        JComponent editorComponent = editor.getContentComponent();
        JRootPane rootPane = SwingUtilities.getRootPane(editorComponent);
        if (rootPane == null) return;

        JLayeredPane layeredPane = rootPane.getLayeredPane();
        VisualPosition visualPos = editor.offsetToVisualPosition(offset);
        Point caretInEditor = editor.visualPositionToXY(visualPos);
        Point caretInLayered = SwingUtilities.convertPoint(editorComponent, caretInEditor, layeredPane);

        List<int[]> velocities = new ArrayList<>();
        List<Color> colors = new ArrayList<>();
        int[] px = new int[PARTICLE_COUNT];
        int[] py = new int[PARTICLE_COUNT];

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            // Mostly horizontal spread (simulates sweeping to the right)
            double angle = -0.3 + RANDOM.nextDouble() * 0.6; // narrow horizontal cone
            double speed = 3.0 + RANDOM.nextDouble() * 5.0;
            velocities.add(new int[]{(int)(Math.cos(angle) * speed), (int)(Math.sin(angle) * speed) - 2});
            colors.add(COLORS[RANDOM.nextInt(COLORS.length)]);
            px[i] = caretInLayered.x;
            py[i] = caretInLayered.y;
        }

        ParticleOverlay overlay = new ParticleOverlay(layeredPane, px, py, velocities, colors, 5);
        layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
        layeredPane.setComponentZOrder(overlay, 0);

        int[] step = {0};
        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            if (step[0] >= ANIMATION_STEPS) {
                timer.stop();
                layeredPane.remove(overlay);
                layeredPane.repaint();
                return;
            }
            overlay.setAlpha(1.0f - (float) step[0] / ANIMATION_STEPS);
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                px[i] += velocities.get(i)[0];
                py[i] += velocities.get(i)[1] + 1;
            }
            overlay.updatePositions(px, py);
            layeredPane.repaint(overlay.getBounds());
            step[0]++;
        });
        timer.start();
    }
}
