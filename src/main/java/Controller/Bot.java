package Controller;

import Controller.Item.Order;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.sql.Statement;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    private final HashMap<Long, Chat> allChats = new HashMap<Long, Chat>();
    private final Statement stml;
    private final Controller controller;
    private Order order;

    public Bot(Statement stml, Controller controller){
        this.stml = stml;
        this.controller = controller;
        this.order = new Order();
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null) {
            long chatId = message.getChatId();
            Chat chat = allChats.get(chatId);

            if (chat == null) {
                chat = new Chat(this);
                allChats.put(chatId, chat);
            }
            if (message.hasText()) chat.onUpdateReceived(message);
            if (message.hasContact()) chat.onContactReceive(message);

        }
    }

    public Controller getController() {
        return controller;
    }

    public Statement getStml() {
        return stml;
    }

    public String getBotUsername() {
        return "ZabavaBot";
    }

    public String getBotToken() {
        return "1412141744:AAFYI_lkP2WZ-aXzCXF1XM-NrkcztbWubLg";
    }

    public Order getOrder() {
        return this.order;
    }
}
