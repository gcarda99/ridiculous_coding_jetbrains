package com.gcarda99.ridiculous;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.*;
import org.jetbrains.annotations.NotNull;

/**
 * Application-level service that registers the EditorFactory listener
 * as soon as the plugin is initialized.
 */
public class RidiculousService implements Disposable {

    public RidiculousService() {
        EditorFactory.getInstance().addEditorFactoryListener(
            new RidiculousTypingListener(), this
        );
    }

    @Override
    public void dispose() {
        // Cleanup is handled automatically by EditorFactory
        // when this Disposable is disposed
    }
}
