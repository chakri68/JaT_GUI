package utils;

import java.util.function.BiFunction;

import syncChat.Server.ClientHandler;

public class Command<R> {
    BiFunction<ClientHandler, String[], R> commandHandler;

    public Command(BiFunction<ClientHandler, String[], R> commandHandler) {
        this.commandHandler = commandHandler;
    }

    public R execute(ClientHandler client, String[] inpList) {
        // Executing the function
        return commandHandler.apply(client, inpList);
    }
}
