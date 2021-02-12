package org.jokergames.myjfql.module;

/**
 * @author Janick
 */

public abstract class Module {

    private boolean enabled;
    private ModuleInfo moduleInfo;

    public abstract void onEnable();

    public abstract void onDisable();

    public ModuleInfo getModuleInfo() {
        return moduleInfo;
    }

    public void setModuleInfo(ModuleInfo pluginInfo) {
        this.moduleInfo = pluginInfo;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (enabled) {
            onEnable();
        } else {
            onDisable();
        }

    }

}
