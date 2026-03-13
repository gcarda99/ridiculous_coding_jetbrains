package com.gcarda99.ridiculous;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Renders animated particle bursts at the caret position when the user types.
 * Must be called from any thread — internally dispatches to the EDT.
 */
public class ParticleEffect {

    private static final int PARTICLE_COUNT = 12;
    private static final int ANIMATION_STEPS = 25;
    private static final Random RANDOM = new Random();

    private static final Color[] COLORS = {
        new Color(255, 80, 80),
        new Color(255, 200, 50),
        new Color(80, 200, 120),
        new Color(80, 150, 255),
        new Color(200, 80, 255),
        new Color(255, 140, 0)
    };

    public static void trigger(Editor editor, int offset) {
        SwingUtilities.invokeLater(() -> doTrigger(editor, offset));
    }

    private static void doTrigger(Editor editor, int offset) {
        JComponent editorComponent = editor.getContentComponent();

        // Use the root pane's layered pane for proper overlay (above all editor layers)
        JRootPane rootPane = SwingUtilities.getRootPane(editorComponent);
        if (rootPane == null) return;

        JLayeredPane layeredPane = rootPane.getLayeredPane();

        // Convert caret position to layered pane coordinates
        VisualPosition visualPos = editor.offsetToVisualPosition(offset);
        Point caretInEditor = editor.visualPositionToXY(visualPos);
        Point caretInLayered = SwingUtilities.convertPoint(editorComponent, caretInEditor, layeredPane);

        List<int[]> velocities = new ArrayList<>();
        List<Color> particleColors = new ArrayList<>();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = 2 * Math.PI * i / PARTICLE_COUNT + RANDOM.nextDouble() * 0.6;
            double speed = 3.0 + RANDOM.nextDouble() * 4.0;
            velocities.add(new int[]{(int) (Math.cos(angle) * speed), (int) (Math.sin(angle) * speed)});
            particleColors.add(COLORS[RANDOM.nextInt(COLORS.length)]);
        }

        int[] px = new int[PARTICLE_COUNT];
        int[] py = new int[PARTICLE_COUNT];
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i] = caretInLayered.x;
            py[i] = caretInLayered.y;
        }

        ParticleOverlay overlay = new ParticleOverlay(layeredPane, px, py, velocities, particleColors);
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
            float alpha = 1.0f - (float) step[0] / ANIMATION_STEPS;
            overlay.setAlpha(alpha);
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                px[i] += velocities.get(i)[0];
                py[i] += velocities.get(i)[1] + 1; // gravity
            }
            overlay.updatePositions(px, py);
            layeredPane.repaint(overlay.getBounds());
            step[0]++;
        });
        timer.start();
    }
}
