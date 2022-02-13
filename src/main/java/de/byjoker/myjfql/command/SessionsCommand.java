package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.database.RelationalTableEntry;
import de.byjoker.myjfql.database.TableEntry;
import de.byjoker.myjfql.network.session.Session;
import de.byjoker.myjfql.network.session.SessionService;
import de.byjoker.myjfql.network.session.StaticSession;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;
import de.byjoker.myjfql.util.IDGenerator;
import de.byjoker.myjfql.util.ResultType;

import java.util.*;


@CommandHandler
public class SessionsCommand extends ConsoleCommand {

    public SessionsCommand() {
        super("sessions", Arrays.asList("COMMAND", "OF", "BIND", "TO", "OPEN", "TOKEN", "CLOSE-ALL", "CLOSE", "DATABASE", "ADDRESS", "EXPIRE"));
    }

    @Override
    public void executeAsConsole(ConsoleCommandSender sender, Map<String, List<String>> args) {
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
                String token = null;
                String database = user.getPreferredDatabaseId();
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

                final Session session = new StaticSession(token, user.getId(), database, Collections.singletonList(address));
                sessionService.openSession(session);

                final TableEntry entry = new RelationalTableEntry();
                entry.insert("token", session.getToken());
                entry.insert("addresses", session.getAddresses());
                entry.insert("database_id", String.valueOf(session.getDatabaseId()));

                sender.sendResult(Collections.singletonList(entry), Arrays.asList("token", "addresses", "database_id"), ResultType.LEGACY);
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

                if (sessionService.getSession(token) == null) {
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

                if (sessionService.getSession(token) == null) {
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
                    session.getAddresses().clear();
                    session.getAddresses().add(address);
                    sessionService.saveSession(session);

                    sender.sendSuccess();
                    return;
                }


                sender.sendSyntax();
                return;
            }

            final List<TableEntry> sessions = new ArrayList<>();

            sessionService.getSessionsByUserId(user.getId()).forEach(session -> {
                final TableEntry entry = new RelationalTableEntry();
                entry.insert("token", session.getToken());
                entry.insert("addresses", session.getAddresses());
                entry.insert("database_id", String.valueOf(session.getDatabaseId()));
                sessions.add(entry);
            });

            sender.sendResult(sessions, Arrays.asList("token", "addresses", "database_id"), ResultType.LEGACY);
            return;
        }

        sender.sendSyntax();
    }

}
