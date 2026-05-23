package de.peters.relativepathinserter;

import org.openide.nodes.Node;
import org.openide.windows.TopComponent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.openide.filesystems.FileObject;

public class SelectedFileTracker implements PropertyChangeListener {

    private static Node lastSelectedNode = null;

    public SelectedFileTracker() {
        // Nur Registry hören — keine Warnung mehr, kein "this escape"
        TopComponent.getRegistry().addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if (!TopComponent.Registry.PROP_ACTIVATED_NODES.equals(evt.getPropertyName())) {
            return; // uninteressant
        }

        // Aktives TopComponent holen
        TopComponent tc = TopComponent.getRegistry().getActivated();

        if (tc == null) return;

        String tcName = tc.getClass().getName();

        // 👉 GANZ WICHTIG:
        // Nur WENN Files-View (Projekt-/Datei-Fenster)
        // NICHT reagieren, wenn Editor aktiv wird!
        if (tcName.contains("Favorites") ||
            tcName.contains("Files") ||
            tcName.contains("Project") ||
            tcName.contains("Projects")) {

            Node[] nodes = TopComponent.getRegistry().getActivatedNodes();

            if (nodes != null && nodes.length > 0) {
                lastSelectedNode = nodes[0];

                // Datei bestimmen (falls vorhanden)
                FileObject fo = lastSelectedNode.getLookup().lookup(FileObject.class);
            }
        }
    }

    // Getter, kompatibel mit deiner Action
    public static Node getSelectedNode() {
        return lastSelectedNode;
    }
}
