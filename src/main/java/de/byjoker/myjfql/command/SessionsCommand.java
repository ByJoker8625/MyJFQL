package de.byjoker.myjfql.command;

import de.byjoker.myjfql.core.MyJFQL;
import de.byjoker.myjfql.database.Column;
import de.byjoker.myjfql.database.DatabaseService;
import de.byjoker.myjfql.user.User;
import de.byjoker.myjfql.user.UserService;
import de.byjoker.myjfql.user.session.Session;
import de.byjoker.myjfql.user.session.SessionService;

import java.text.SimpleDateFormat;
import java.util.*;

@CommandHandler
public class SessionsCommand extends ConsoleCommand {

    public SessionsCommand() {
        super("sessions", Arrays.asList("COMMAND", "OF", "BIND", "TO", "CLOSE-ALL", "CLOSE"));
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
                final String databaseIdentifier = formatString(args.get("TO"));

                if (token == null) {
                    sender.sendError("Undefined session!");
                    return;
                }

                if (databaseIdentifier == null) {
                    sender.sendError("Undefined database!");
                    return;
                }

                if (!sessionService.existsSession(token)) {
                    sender.sendError("Session doesn't exists!");
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

            final List<Column> sessions = new ArrayList<>();
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            sessionService.getSessionsByUserId(user.getId()).forEach(session -> {
                Column column = new Column();
                column.putContent("Token", session.getToken());
                column.putContent("Address", session.getAddress());
                column.putContent("Start", dateFormat.format(new Date(session.getOpen())));
                column.putContent("Expire", dateFormat.format(new Date(session.getExpire())));
            });

            sender.sendResult(sessions, new String[]{"Token", "Address", "Start", "Expire"});
            return;
        }

        sender.sendSyntax();
    }

}
