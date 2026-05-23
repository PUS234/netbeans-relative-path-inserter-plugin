package de.peters.relativepathinserter;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;

public class RightClickTracker {

    private static volatile int lastRightClickOffset = -1;

    public RightClickTracker() {
        EditorRegistry.addPropertyChangeListener(evt -> {

            if (EditorRegistry.FOCUS_GAINED_PROPERTY.equals(evt.getPropertyName())) {
                JTextComponent editor = EditorRegistry.lastFocusedComponent();

                if (editor != null) {
                    installMouseListener(editor);
                }
            }
        });
    }

    private void installMouseListener(JTextComponent editor) {

        // doppeltes Hinzufügen verhindern
        for (MouseAdapter a : editor.getListeners(MouseAdapter.class)) {
            if (a instanceof EditorRightClickListener) return;
        }

        editor.addMouseListener(new EditorRightClickListener(editor));
    }

    public static int getLastRightClickOffset() {
        return lastRightClickOffset;
    }

    private static class EditorRightClickListener extends MouseAdapter {

        private final JTextComponent editor;

        public EditorRightClickListener(JTextComponent editor) {
            this.editor = editor;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            handle(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            handle(e);
        }

        private void handle(MouseEvent e) {
            if (!e.isPopupTrigger()) return;

            Component src = e.getComponent();
            if (!(src instanceof JTextComponent)) return;

            if (EditorRegistry.lastFocusedComponent() != editor) return;

            Point p = new Point(e.getX(), e.getY());
            int offset = editor.viewToModel2D(p);
            if (offset >= 0) {
                lastRightClickOffset = offset;
            }
        }
    }
}
