package com.gcarda99.ridiculous;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

/**
 * Listens to editor factory events and attaches a DocumentListener
 * to each newly created editor to trigger visual effects on typing.
 */
public class RidiculousTypingListener implements EditorFactoryListener {

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();

        DocumentListener listener = new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent e) {
                if (e.getNewLength() > 0) {
                    // A character was inserted — trigger the effect
                    ParticleEffect.trigger(editor, editor.getCaretModel().getOffset());
                }
            }
        };

        editor.getDocument().addDocumentListener(listener);

        // Cleanup the listener when the editor is disposed
        Disposer.register(editor.getProject() != null ? editor.getProject() : getPluginDisposable(), new Disposable() {
            @Override
            public void dispose() {
                editor.getDocument().removeDocumentListener(listener);
            }
        });
    }

    private static Disposable getPluginDisposable() {
        return Disposer.newDisposable("RidiculousTypingListener");
    }
}
