package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.database.SimpleColumn;
import de.byjoker.myjfql.server.session.Session;
import de.byjoker.myjfql.server.session.SessionService;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;
import de.byjoker.myjfql.util.IDGenerator;

import java.text.SimpleDateFormat;
import java.util.*;

@CommandHandler
public class SessionsCommand extends ConsoleCommand {

    public SessionsCommand() {
        super("sessions", Arrays.asList("COMMAND", "OF", "BIND", "TO", "OPEN", "TOKEN", "CLOSE-ALL", "CLOSE", "DATABASE", "ADDRESS", "EXPIRE"));
    }

    @Override
    public void handleConsoleCommand(ConsoleCommandSender sender, Map<String, List<String>> args) {
        final UserService userService = MyJFQL.getInstance().getUserService();
        final SessionService sessionService = MyJFQL.getInstance().getSessionService();
        final DatabaseService databaseService = MyJFQL.getInstance().getDatabaseService();

        if (args.containsKey("OF")) {
            final String userIdentifier = formatString(args.get("OF"));

            if (userIdentifier == null) {
                sender.sendError("Undefined user!");
                return;
            }

            if (!userService.existsUserByIdentifier(userIdentifier)) {
                sender.sendError("User doesn't exists!");
                return;
            }

            final User user = userService.getUserByIdentifier(userIdentifier);

            if (args.containsKey("OPEN")) {
                final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                long expire = -1;

                if (args.containsKey("EXPIRE") && args.get("EXPIRE").size() != 0
                        && !Objects.requireNonNull(formatString(args.get("EXPIRE"))).equalsIgnoreCase("never")) {

                    try {
                        expire = dateFormat.parse(formatString(args.get("EXPIRE"))).getTime();
                    } catch (Exception ex) {
                        sender.sendError("Unknown date format!");
                        return;
                    }

                    if (expire <= System.currentTimeMillis()) {
                        sender.sendError("Session already expired!");
                        return;
                    }
                }

                String token = null;
                String database = user.getPreferredDatabase();
                String address = "*";

                if (args.containsKey("TOKEN") && args.get("TOKEN").size() != 0) {
                    token = formatString(args.get("TOKEN"));
                }

                if (token != null && token.length() < 16) {
                    sender.sendError("Token is to short! Minimum 16 characters!");
                    return;
                }

                if (args.containsKey("DATABASE") && args.get("DATABASE").size() != 0) {
                    database = formatString(args.get("DATABASE"));

                    if (!databaseService.existsDatabaseByIdentifier(database)) {
                        sender.sendError("Database doesn't exists!");
                        return;
                    }

                    database = databaseService.getDatabaseByIdentifier(database).getId();
                }

                if (args.containsKey("ADDRESS") && args.get("ADDRESS").size() != 0) {
                    address = formatString(args.get("ADDRESS"));
                }

                if (token == null) {
                    token = IDGenerator.generateMixed(25);
                }

                final Session session = new Session(token, user.getId(), database, address, expire);
                sessionService.openSession(session);

                final Column column = new SimpleColumn();
                column.setItem("token", session.getToken());
                column.setItem("address", session.getAddress());
                column.setItem("database_id", String.valueOf(session.getDatabaseId()));
                column.setItem("start", dateFormat.format(new Date(session.getOpen())));
                column.setItem("expire", session.getExpire() == -1 ? "never" : dateFormat.format(new Date(session.getExpire())));

                sender.sendResult(Collections.singletonList(column), new String[]{"token", "address", "database_id", "start", "expire"});
                return;
            }

            if (args.containsKey("CLOSE-ALL")) {
                sessionService.closeSessions(user.getId());
                sender.sendSuccess();
                return;
            }

            if (args.containsKey("CLOSE")) {
                final String token = formatString(args.get("CLOSE"));

                if (token == null) {
                    sender.sendError("Undefined session!");
                    return;
                }

                if (!sessionService.existsSession(token)) {
                    sender.sendError("Session doesn't exists!");
                    return;
                }

                sessionService.closeSession(token);
                sender.sendSuccess();
                return;
            }

            if (args.containsKey("BIND") && args.containsKey("TO")) {
                final String token = formatString(args.get("BIND"));

                if (token == null) {
                    sender.sendError("Undefined session!");
                    return;
                }

                if (!sessionService.existsSession(token)) {
                    sender.sendError("Session doesn't exists!");
                    return;
                }

                if (args.containsKey("DATABASE")) {
                    final String databaseIdentifier = formatString(args.get("DATABASE"));

                    if (databaseIdentifier == null) {
                        sender.sendError("Undefined database!");
                        return;
                    }

                    if (!databaseService.existsDatabaseByIdentifier(databaseIdentifier)) {
                        sender.sendError("Database doesn't exists!");
                        return;
                    }

                    final Session session = sessionService.getSession(token);
                    session.setDatabaseId(databaseService.getDatabaseByIdentifier(databaseIdentifier).getId());
                    sessionService.saveSession(session);

                    sender.sendSuccess();
                    return;
                }

                if (args.containsKey("ADDRESS")) {
                    final String address = formatString(args.get("ADDRESS"));

                    if (address == null) {
                        sender.sendError("Undefined address!");
                        return;
                    }

                    final Session session = sessionService.getSession(token);
                    session.setAddress(address);
                    sessionService.saveSession(session);

                    sender.sendSuccess();
                    return;
                }

                if (args.containsKey("EXPIRE")) {
                    final String expire = formatString(args.get("EXPIRE"));

                    if (expire == null) {
                        sender.sendError("Undefined date!");
                        return;
                    }

                    if (expire.equalsIgnoreCase("never")) {
                        final Session session = sessionService.getSession(token);
                        session.setExpire(-1);
                        sessionService.saveSession(session);
                        sender.sendSuccess();
                        return;
                    }

                    long date;

                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(formatString(args.get("EXPIRE"))).getTime();
                    } catch (Exception ex) {
                        sender.sendError("Unknown date format!");
                        return;
                    }

                    if (date <= System.currentTimeMillis()) {
                        sender.sendError("Session already expired!");
                        return;
                    }

                    final Session session = sessionService.getSession(token);
                    session.setExpire(date);
                    sessionService.saveSession(session);
                    sender.sendSuccess();
                    return;
                }

                sender.sendSyntax();
                return;
            }

            final List<Column> sessions = new ArrayList<>();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            sessionService.getSessionsByUserId(user.getId()).forEach(session -> {
                final Column column = new SimpleColumn();
                column.setItem("token", session.getToken());
                column.setItem("address", session.getAddress());
                column.setItem("database_id", String.valueOf(session.getDatabaseId()));
                column.setItem("start", dateFormat.format(new Date(session.getOpen())));
                column.setItem("expire", session.getExpire() == -1 ? "never" : dateFormat.format(new Date(session.getExpire())));

                sessions.add(column);
            });

            sender.sendResult(sessions, new String[]{"token", "address", "database_id", "start", "expire"});
            return;
        }

        sender.sendSyntax();
    }

}
