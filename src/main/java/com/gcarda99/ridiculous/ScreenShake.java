package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;

import javax.swing.*;
import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * ScreenShake: briefly shakes the editor window on keystrokes.
 * Duration in ms, intensity in pixels.
 */
public class ScreenShake {

    private static final Random RANDOM = new Random();
    private static final AtomicBoolean shaking = new AtomicBoolean(false);

    public static void trigger(Editor editor, int durationMs, int intensity) {
        if (shaking.getAndSet(true)) return; // don't stack shakes

        SwingUtilities.invokeLater(() -> doShake(editor, durationMs, intensity));
    }

    private static void doShake(Editor editor, int durationMs, int intensity) {
        JComponent editorComponent = editor.getContentComponent();
        JRootPane rootPane = SwingUtilities.getRootPane(editorComponent);
        if (rootPane == null) { shaking.set(false); return; }

        Point originalLocation = rootPane.getLocation();
        int steps = durationMs / 16;
        int[] step = {0};

        Timer timer = new Timer(16, null);
        timer.addActionListener(e -> {
            if (step[0] >= steps) {
                timer.stop();
                rootPane.setLocation(originalLocation);
                shaking.set(false);
                return;
            }
            // Dampen shake over time
            float factor = 1.0f - (float) step[0] / steps;
            int dx = (int)(RANDOM.nextInt(intensity * 2 + 1) - intensity) * factor > 0.5f ? 1 : 0;
            int offsetX = (int)((RANDOM.nextFloat() * 2 - 1) * intensity * factor);
            int offsetY = (int)((RANDOM.nextFloat() * 2 - 1) * intensity * factor);
            rootPane.setLocation(originalLocation.x + offsetX, originalLocation.y + offsetY);
            step[0]++;
        });
        timer.start();
    }
}
