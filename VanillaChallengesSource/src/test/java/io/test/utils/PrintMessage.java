package io.test.utils;

import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.command.ConsoleCommandSenderMock;
import org.mockbukkit.mockbukkit.entity.PlayerMock;

import java.util.ArrayList;
import java.util.List;

public class PrintMessage {

    private ServerMock serverMock;

    public PrintMessage(ServerMock serverMock) {
        this.serverMock = serverMock;
    }

    public List<String> getAllPlayerMessages(PlayerMock playerMock) {
        List<String> messages = new ArrayList<>();
        int maxAttempts = 100;
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            serverMock.getScheduler().performTicks(20L);
            String message = playerMock.nextMessage();
            if (message != null) {
                messages.add(message);
                attempts = 0; // Reset counter when we find a message
            } else {
                attempts++;
            }
        }
        return messages;
    }

    public String getMessage(PlayerMock playerMock) {
        String message = playerMock.nextMessage();
        int count = 0;
        while (message == null) {
            serverMock.getScheduler().performTicks(20L);
            message = playerMock.nextMessage();
            if(message == null) {
                count++;
                if(count == 100) {
                    break;
                }
            }
        }
        return message;
    }

}
