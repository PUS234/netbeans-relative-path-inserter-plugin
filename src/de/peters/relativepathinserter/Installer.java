package de.peters.relativepathinserter;

import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        new SelectedFileTracker();
        new RightClickTracker();
    }
}
