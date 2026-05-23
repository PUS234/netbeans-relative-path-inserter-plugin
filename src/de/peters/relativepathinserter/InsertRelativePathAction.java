package de.peters.relativepathinserter;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import org.netbeans.api.editor.EditorRegistry;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Node;

@ActionID(
        category = "Edit",
        id = "de.peters.relativepathinserter.InsertRelativePathEditorAction"
)
@ActionRegistration(
        displayName = "Insert relative path"
)

@ActionReferences({
    // Editors-Pfade (explizit)
    @ActionReference(path = "Editors/text/x-java/Popup", position = 22222),
    @ActionReference(path = "Editors/text/x-php/Popup", position = 22222),
    @ActionReference(path = "Editors/text/x-php5/Popup", position = 22222),
    @ActionReference(path = "Editors/text/html/Popup", position = 22222),
    @ActionReference(path = "Editors/text/css/Popup", position = 22222),

    // Loaders-Pfade (viel robuster, deckt viele Editoren ab)
    @ActionReference(path = "Loaders/text/x-java/Popup", position = 22222),
    @ActionReference(path = "Loaders/text/x-php/Popup", position = 22222),
    @ActionReference(path = "Loaders/text/x-php5/Popup", position = 22222),
    @ActionReference(path = "Loaders/text/html/Popup", position = 22222),
    @ActionReference(path = "Loaders/text/css/Popup", position = 22222),

    // generische Text-Loader als zusätzliche Absicherung
    @ActionReference(path = "Loaders/text/*/Popup", position = 22222)
})

public final class InsertRelativePathAction extends AbstractAction {

    public InsertRelativePathAction() {
        super("Insert relative path");
    }

    @Override
    public boolean isEnabled() {
        return SelectedFileTracker.getSelectedNode() != null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Node selectedNode = SelectedFileTracker.getSelectedNode();
        if (selectedNode == null) {
            return;
        }

        FileObject fo = selectedNode.getLookup().lookup(FileObject.class);
        if (fo == null) {
            return;
        }

        JTextComponent editor = EditorRegistry.lastFocusedComponent();
        if (editor == null) {
            return;
        }

        int pos = RightClickTracker.getLastRightClickOffset();
        if (pos < 0) {
            pos = editor.getCaretPosition();
        }

        // FileObject des geöffneten Dokuments
        FileObject currentFile
                = org.netbeans.modules.editor.NbEditorUtilities.getFileObject(editor.getDocument());

        String path;

        if (currentFile != null) {
            path = getRelativePath(currentFile, fo);
        } else {
            // Fallback: absolut
            path = fo.getPath();
        }

        try {
//            Document doc = editor.getDocument();
//            doc.insertString(pos, path, null);
            if (editor.getSelectedText() != null) {
                // markierten Text ersetzen
                editor.replaceSelection(path);

            } else {
                // normal einfügen
                Document doc = editor.getDocument();
                doc.insertString(pos, path, null);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ======================================
    // ========== RELATIVE PATH LOGIK =======
    // ======================================
    private static String getRelativePath(FileObject from, FileObject to) {

        String[] fromParts = from.getPath().split("/");
        String[] toParts = to.getPath().split("/");

        int i = 0;

        // gemeinsamen Prefix finden
        while (i < fromParts.length && i < toParts.length
                && fromParts[i].equals(toParts[i])) {
            i++;
        }

        StringBuilder rel = new StringBuilder();

        // für jede Ebene ab from → ".."
        for (int j = i; j < fromParts.length - 1; j++) {
            rel.append("../");
        }

        // Rest des Zielpfads
        for (int j = i; j < toParts.length; j++) {
            rel.append(toParts[j]);
            if (j < toParts.length - 1) {
                rel.append("/");
            }
        }

        return rel.toString();
    }
}
