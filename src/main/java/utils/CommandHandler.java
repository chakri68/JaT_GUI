package utils;

import java.util.Map;

import syncChat.Server;
import syncChat.Server.ClientHandler;

import static java.util.Map.entry;

import java.util.Arrays;

public class CommandHandler {
    private Map<String, Command> commands;
    private Server server;
    private String cmdStr = "\\";

    public CommandHandler(Server server, Map<String, Command> commands) {
        this.commands = commands;
        this.server = server;
    }

    public CommandHandler(Server server, Map<String, Command> commands, String commandInitStr) {
        this.cmdStr = commandInitStr;
        this.commands = commands;
        this.server = server;
    }

    public Object handle(ClientHandler client, String command) {
        // commandText parsing
        String[] commandParts = command.split(" ");
        command = commandParts[0].substring(cmdStr.length());
        commandParts = Arrays.copyOfRange(commandParts, 1, commandParts.length);
        // Execution with only the input parts sent as input
        return this.commands.get(command).execute(client, commandParts);
    }

    // Example showing how to initialize a CommandHandler
    public static void main(String[] args) {
        Server server = new Server(3000);
        CommandHandler ch = new CommandHandler(server, Map.<String, Command>ofEntries(
                entry("cu", new Command<Void>((s, inpList) -> {
                    System.out.println("new Username: " + inpList[0]);
                    return null;
                })),
                entry("format", new Command<>((s, inpList) -> {
                    System.out
                            .println("This should format this text: " + ColorPrinter.printf(String.join(" ", inpList)));
                    return String.join(" ", inpList);
                }))));
    }
}
