package de.jokergames.jfql.module;

import de.jokergames.jfql.core.JFQL;
import de.jokergames.jfql.exception.ModuleException;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Janick
 */

public class ModuleHandler {

    private final List<Module> modules;
    private final ModuleLoader moduleLoader;

    public ModuleHandler() {
        this.modules = new ArrayList<>();
        this.moduleLoader = new ModuleLoader();
    }

    public void enanbleModule(Module module) {
        if (module == null)
            return;

        module.setEnabled(true);
        modules.add(module);
    }

    public void disableModule(Module module) {
        if (module == null)
            return;

        module.setEnabled(false);
        modules.remove(module);
    }

    public void enableModules() throws Exception {
        ModuleInfo[] moduleInfos = moduleLoader.loadDirectory(new File("module"));

        for (ModuleInfo moduleInfo : moduleInfos) {
            if (moduleInfo != null) {
                try {
                    enanbleModule(transform(moduleInfo));
                    JFQL.getInstance().getConsole().logInfo("Loading module " + moduleInfo.getName() + "...");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void disableModules() {
        try {

            for (Module module : modules) {
                try {
                    disableModule(module);
                } catch (Exception ex) {
                    new ModuleException(ex).printStackTrace();
                }
            }

            modules.clear();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Module transform(ModuleInfo moduleInfo) throws Exception {
        Class<?> clazz = Class.forName(moduleInfo.getMain(), true, new URLClassLoader(new URL[]{moduleInfo.getFile().toURI().toURL()}));
        Module module = (Module) clazz.newInstance();
        module.setModuleInfo(moduleInfo);
        return module;
    }

    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public List<Module> getModules() {
        return modules;
    }
}
