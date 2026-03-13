package com.gcarda99.ridiculous;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.*;
import org.jetbrains.annotations.NotNull;

public class RidiculousTypingListener implements EditorFactoryListener {

    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();

        editor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent e) {
                int offset = editor.getCaretModel().getOffset();

                if (e.getNewLength() > 0 && e.getOldLength() == 0) {
                    // Typed a character
                    boolean isNewline = e.getNewFragment().toString().contains("\n");
                    if (isNewline) {
                        NewlineEffect.trigger(editor, offset);
                        ScreenShake.trigger(editor, 80, 4);
                    } else {
                        BlipEffect.trigger(editor, offset);
                        ScreenShake.trigger(editor, 50, 3);
                    }
                } else if (e.getOldLength() > 0 && e.getNewLength() == 0) {
                    // Deleted a character (backspace/delete)
                    BoomEffect.trigger(editor, offset);
                    ScreenShake.trigger(editor, 150, 8);
                }
            }
        });
    }
}
