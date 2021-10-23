package de.byjoker.myjfql.command;

public abstract class CommandSender {

    private final String name;
    private final String address;

    public CommandSender(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public abstract boolean hasPermission(String permission);

    public abstract boolean isStaticDatabase();

    public abstract void sendError(Object obj);

    public abstract void sendInfo(Object obj);

    public abstract void sendForbidden();

    public abstract void sendSyntax();

    public abstract void sendSuccess();

    public abstract void sendAnswer(Object obj, Object structure);

    public abstract void send(Object obj);

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
