package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.VisualPosition;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Blip: small colorful particles bursting from caret on each keystroke.
 * Pitch increases with consecutive keystrokes (simulated via particle size).
 */
public class BlipEffect {

    private static final int PARTICLE_COUNT = 8;
    private static final int ANIMATION_STEPS = 20;
    private static final Random RANDOM = new Random();
    private static final AtomicInteger combo = new AtomicInteger(0);

    private static final Color[] COLORS = {
        new Color(255, 80, 80),
        new Color(255, 200, 50),
        new Color(80, 200, 120),
        new Color(80, 150, 255),
        new Color(200, 80, 255),
        new Color(255, 140, 0)
    };

    public static void trigger(Editor editor, int offset) {
        int currentCombo = combo.incrementAndGet();
        // Reset combo after 500ms of inactivity
        Timer resetTimer = new Timer(500, e -> combo.set(0));
        resetTimer.setRepeats(false);
        resetTimer.start();

        SwingUtilities.invokeLater(() -> doTrigger(editor, offset, currentCombo));
    }

    private static void doTrigger(Editor editor, int offset, int comboLevel) {
        JComponent editorComponent = editor.getContentComponent();
        JRootPane rootPane = SwingUtilities.getRootPane(editorComponent);
        if (rootPane == null) return;

        JLayeredPane layeredPane = rootPane.getLayeredPane();
        VisualPosition visualPos = editor.offsetToVisualPosition(offset);
        Point caretInEditor = editor.visualPositionToXY(visualPos);
        Point caretInLayered = SwingUtilities.convertPoint(editorComponent, caretInEditor, layeredPane);

        // Particle size grows with combo
        int particleSize = Math.min(4 + comboLevel / 3, 10);
        int count = Math.min(PARTICLE_COUNT + comboLevel / 5, 16);

        List<int[]> velocities = new ArrayList<>();
        List<Color> colors = new ArrayList<>();
        int[] px = new int[count];
        int[] py = new int[count];

        for (int i = 0; i < count; i++) {
            double angle = 2 * Math.PI * i / count + RANDOM.nextDouble() * 0.6;
            double speed = 2.0 + RANDOM.nextDouble() * 3.5;
            velocities.add(new int[]{(int)(Math.cos(angle) * speed), (int)(Math.sin(angle) * speed)});
            colors.add(COLORS[RANDOM.nextInt(COLORS.length)]);
            px[i] = caretInLayered.x;
            py[i] = caretInLayered.y;
        }

        ParticleOverlay overlay = new ParticleOverlay(layeredPane, px, py, velocities, colors, particleSize);
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
            for (int i = 0; i < count; i++) {
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
