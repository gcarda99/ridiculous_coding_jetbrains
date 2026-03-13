package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Boom: large explosion effect triggered on character deletion.
 */
public class BoomEffect {

    private static final int PARTICLE_COUNT = 20;
    private static final int ANIMATION_STEPS = 30;
    private static final Random RANDOM = new Random();

    private static final Color[] COLORS = {
        new Color(255, 60, 60),
        new Color(255, 120, 0),
        new Color(255, 220, 0),
        new Color(200, 50, 50),
        new Color(255, 180, 50)
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
            double angle = 2 * Math.PI * i / PARTICLE_COUNT + RANDOM.nextDouble() * 0.4;
            double speed = 4.0 + RANDOM.nextDouble() * 6.0;
            velocities.add(new int[]{(int)(Math.cos(angle) * speed), (int)(Math.sin(angle) * speed)});
            colors.add(COLORS[RANDOM.nextInt(COLORS.length)]);
            px[i] = caretInLayered.x;
            py[i] = caretInLayered.y;
        }

        // Bigger particles for boom
        ParticleOverlay overlay = new ParticleOverlay(layeredPane, px, py, velocities, colors, 8);
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
