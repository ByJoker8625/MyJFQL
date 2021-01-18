package de.jokergames.jfql.core.boot;

import java.util.List;

public class BootSection {

    private final String name;
    private final List<BootArgument> arguments;
    private final BootHandler bootHandler;
    private Type type;

    public BootSection(String name, List<BootArgument> arguments, BootHandler bootHandler) {
        this.name = name;
        this.arguments = arguments;
        this.bootHandler = bootHandler;
        this.type = Type.EQUALS;
    }

    public BootSection(String name, List<BootArgument> arguments, BootHandler bootHandler, Type type) {
        this.name = name;
        this.arguments = arguments;
        this.bootHandler = bootHandler;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void boot(List<BootArgument> arguments) {
        bootHandler.boot(arguments);
    }

    public BootHandler getBootHandler() {
        return bootHandler;
    }

    public String getName() {
        return name;
    }

    public List<BootArgument> getArguments() {
        return arguments;
    }

    public enum Type {
        EQUALS,
        DEFAULT
    }
}
