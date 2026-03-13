package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;

/**
 * @deprecated Use BlipEffect, BoomEffect, NewlineEffect directly.
 * Kept for compatibility during refactor.
 */
@Deprecated
public class ParticleEffect {
    public static void trigger(Editor editor, int offset) {
        BlipEffect.trigger(editor, offset);
    }
}
