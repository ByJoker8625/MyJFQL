package org.jokergames.myjfql.module;

import org.jokergames.myjfql.core.MyJFQL;
import org.jokergames.myjfql.exception.ModuleException;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Janick
 */

public class ModuleService {

    private final List<Module> modules;
    private final ModuleLoader moduleLoader;

    public ModuleService() {
        this.modules = new ArrayList<>();
        this.moduleLoader = new ModuleLoader();
    }

    public void enableModule(Module module) {
        if (module == null)
            return;

        module.setEnabled(true);
        modules.add(module);
    }

    public void disableModule(Module module) {
        if (module == null)
            return;

        module.setEnabled(false);
        modules.removeIf(module1 -> module1.getModuleInfo().getName().equals(module.getModuleInfo().getName()));
    }

    public void enableModules() throws Exception {
        ModuleInfo[] moduleInfos = moduleLoader.loadDirectory(new File("module"));

        Arrays.stream(moduleInfos).filter(Objects::nonNull).forEach(moduleInfo -> {
            try {
                enableModule(transform(moduleInfo));
                MyJFQL.getInstance().getConsole().logInfo("Loading module " + moduleInfo.getName() + "...");
            } catch (Exception ex) {
                new ModuleException(ex).printStackTrace();
            }
        });
    }

    public void disableModules() {
        try {
            modules.forEach(module -> {
                try {
                    disableModule(module);
                } catch (Exception ex) {
                    new ModuleException(ex).printStackTrace();
                }
            });

            modules.clear();
        } catch (Exception ex) {
            new ModuleException(ex).printStackTrace();
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
