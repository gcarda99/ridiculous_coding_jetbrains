package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Renders animated particle bursts at the caret position when the user types.
 */
public class ParticleEffect {

    private static final int PARTICLE_COUNT = 12;
    private static final int ANIMATION_STEPS = 20;
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
        JComponent component = editor.getContentComponent();
        VisualPosition visualPos = editor.offsetToVisualPosition(offset);
        Point point = editor.visualPositionToXY(visualPos);

        List<int[]> particles = new ArrayList<>();
        List<Color> particleColors = new ArrayList<>();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = 2 * Math.PI * i / PARTICLE_COUNT + RANDOM.nextDouble() * 0.5;
            double speed = 2.0 + RANDOM.nextDouble() * 3.0;
            particles.add(new int[]{(int) (Math.cos(angle) * speed), (int) (Math.sin(angle) * speed)});
            particleColors.add(COLORS[RANDOM.nextInt(COLORS.length)]);
        }

        // Particle positions (x, y) starting from caret
        int[] px = new int[PARTICLE_COUNT];
        int[] py = new int[PARTICLE_COUNT];
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i] = point.x;
            py[i] = point.y;
        }

        int[] step = {0};

        ParticleOverlay overlay = new ParticleOverlay(component, px, py, particles, particleColors);
        component.add(overlay);
        component.setComponentZOrder(overlay, 0);

        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            if (step[0] >= ANIMATION_STEPS) {
                timer.stop();
                component.remove(overlay);
                component.repaint();
                return;
            }
            float alpha = 1.0f - (float) step[0] / ANIMATION_STEPS;
            overlay.setAlpha(alpha);
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                px[i] += particles.get(i)[0];
                py[i] += particles.get(i)[1] + 1; // gravity
            }
            overlay.updatePositions(px, py);
            component.repaint();
            step[0]++;
        });
        timer.start();
    }
}
