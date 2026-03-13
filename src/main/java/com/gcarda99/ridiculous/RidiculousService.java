package com.gcarda99.ridiculous;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.EditorFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Application-level service that registers the EditorFactory listener
 * as soon as the plugin is initialized.
 */
public class RidiculousService implements Disposable {

    public RidiculousService() {
        System.out.println("[Ridiculous] RidiculousService initialized!");
        EditorFactory.getInstance().addEditorFactoryListener(
            new RidiculousTypingListener(), this
        );
        System.out.println("[Ridiculous] EditorFactoryListener registered!");
    }

    @Override
    public void dispose() {
        System.out.println("[Ridiculous] RidiculousService disposed.");
    }
}
