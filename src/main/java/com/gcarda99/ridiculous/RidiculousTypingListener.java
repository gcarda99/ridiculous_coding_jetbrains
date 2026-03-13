package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.*;
import org.jetbrains.annotations.NotNull;

/**
 * Listens to editor factory events and attaches a DocumentListener
 * to each newly created editor to trigger visual effects on typing.
 */
public class RidiculousTypingListener implements EditorFactoryListener {

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        System.out.println("[Ridiculous] editorCreated: " + editor);

        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent e) {
                System.out.println("[Ridiculous] documentChanged! newLength=" + e.getNewLength());
                if (e.getNewLength() > 0) {
                    int offset = editor.getCaretModel().getOffset();
                    System.out.println("[Ridiculous] Triggering particle at offset=" + offset);
                    ParticleEffect.trigger(editor, offset);
                }
            }
        });
    }
}
