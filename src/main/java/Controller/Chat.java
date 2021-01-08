package Controller;

import Controller.Item.Basket;
import Controller.Item.Item;
import Controller.Item.Order;
import Controller.Item.User;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class Chat {

    private final Bot bot;
    private final Basket basket;
    private final Order order;
    private final Controller controller;
    private Item currentItem;

    private final Statement stml;

    private int oldCategory;
    private int type;

    private HashMap<Integer, String> allTypes;
    private ArrayList<Item> allItems;
    private List<KeyboardRow> keyboard;

    private User user;

    public Chat(Bot bot){
        this.bot = bot;

        this.controller = bot.getController();
        this.stml = bot.getStml();

        this.basket = new Basket();
        this.order = bot.getOrder();

        this.user = new User();
    }

    public void onUpdateReceived(Message message) {
        System.out.println(message.getContact());
        switch (message.getText()){
            case "/start":
                type = 1;
                sendMsg(message, "Выберете категорию");
                break;

            case "Назад":
                type -= 2;
                sendMsg(message, "");
                break;

            case "Корзина":
                 type = 100;
                 sendMsg(message, "");
                 break;

            case "Очистить корзину":
                 type = 101;
                 sendMsg(message, "");
                 break;

            case "Оформить заказ":
                 type = 102;
                 sendMsg(message, "");
                 break;
            default:
                sendMsg(message, "");

        }
    }

    private void sendMsg(Message message, String text){
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText(text);

        try {
            setButtons(sendMessage, message.getText());
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            System.err.println("Error.");
            type --;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public synchronized void setButtons(SendMessage sendMessage, String oldText) throws SQLException {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        keyboard = new ArrayList<KeyboardRow>();
        ResultSet resultSet;
        int i = 0;

        switch (type){
            case 1:
                resultSet = stml.executeQuery("SELECT * FROM DIC_GOODS_GRP");
                getAllType(resultSet);

                Set<Integer> allKeys = allTypes.keySet();
                KeyboardRow keyboardFirstRow = new KeyboardRow();

                for (Integer key : allKeys) {
                    if (i % 2 == 0 && i > 0){
                        keyboard.add(keyboardFirstRow);
                        keyboardFirstRow = new KeyboardRow();
                    }
                    keyboardFirstRow.add(new KeyboardButton(allTypes.get(key)));
                    i++;
                }
                keyboard.add(keyboardFirstRow);

                type = 2;
                sendMessage.setText("Выберете категорию: ");

                keyboardLow(false, true);
                break;
            case 2:
                i = 1;
                StringBuilder sendMessText = new StringBuilder();
                allItems = new ArrayList<>();
                int category = getKeyFromValue(oldText);
                oldCategory = category == -1 ? oldCategory : category;

                resultSet = stml.executeQuery("SELECT * FROM DIC_GOODS WHERE GRP_ID = " + oldCategory);

                while(resultSet.next()){
                    Item item = new Item();
                    item.setId(resultSet.getInt("ID"));
                    item.setName(resultSet.getString("NAME"));
                    item.setParent_id(resultSet.getInt("GRP_ID"));

                    allItems.add(item);
                }

                for (Item item: allItems) {
                    item.setCountAll(getCostAndCount("SELECT * FROM ACC_GOODS WHERE GOODS_ID = " + item.getId(), "GOOD_CNT"));
                    item.setCoast(getCostAndCount("SELECT * FROM JOR_BILL_IN_DT WHERE GOODS_ID = " + item.getId(), "OUT_PRICE"));

                    sendMessText.append(i).append(". ").append(item.toString()).append("\n");
                    i++;
                }

                sendMessage.setText(sendMessText.toString());
                KeyboardRow keyboardSecondRow = new KeyboardRow();

                for (int j = 1; j < i; j++) {
                    if (j % 2 == 0){
                        keyboard.add(keyboardSecondRow);
                        keyboardSecondRow = new KeyboardRow();
                    }
                    keyboardSecondRow.add(new KeyboardButton(Integer.toString(j)));
                }
                keyboard.add(keyboardSecondRow);

                type = 3;
                keyboardLow(true, true);
                break;
            case 3:
                currentItem = allItems.get(Integer.parseInt(oldText) - 1);

                sendMessage.setText("Введите количество: ");
                type = 4;

                keyboardLow(true, true);
                break;

            case 4:
                currentItem.setCountNeed(Integer.parseInt(oldText));
                basket.addNewItem(currentItem);
                sendMessage.setText("Товар добавленно в корзину.");

                currentItem = new Item();

                keyboardLow(true, true);
                break;
            case 100:
                KeyboardRow keyboardThirdRow = new KeyboardRow();
                KeyboardButton offer = new KeyboardButton("Оформить заказ");
                offer.setRequestContact(true);
                keyboardThirdRow.add(offer);

                keyboardThirdRow.add(new KeyboardButton("Очистить корзину"));
                keyboard.add(keyboardThirdRow);

                String basketElem = basket.allItemsString() +
                        "-------------------------------" + "\n" +
                        "Общая цена: " + basket.mainMoney() + " грн.";
                sendMessage.setText(basketElem);
                type = 3;

                keyboardLow(true, false);
                break;
            case 101:
                basket.clearBasket();
                sendMessage.setText("Корзина очищенна.");
                type = 3;

                keyboardLow(true, false);
                break;

            case 102:
                int num = order.getNumOfOrder();
                basket.setOrderNum(num);
                basket.setUser(user.toString());
                order.addNewOrder(basket);

                controller.addNewOrder(basket);

                sendMessage.setText("Ваш заказ: " + num);
                type = 3;
                keyboardLow(true, false);
                break;
            default:
                break;

        }

        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private void keyboardLow(boolean isBack, boolean isBasket){
        KeyboardRow keyboardBack = new KeyboardRow();
        if (isBack) keyboardBack.add(new KeyboardButton("Назад"));
        if (isBasket && basket.haveItem()) keyboardBack.add(new KeyboardButton("Корзина"));
        keyboard.add(keyboardBack);
    }

    private void getAllType(ResultSet resultSet){
        allTypes = new HashMap<Integer, String>();
        try {
            while(resultSet.next()){
                int parent_id = resultSet.getInt("PARENT_ID");
                if (parent_id == 0 || parent_id == 68) continue;

                String name = resultSet.getString("NAME");
                int id = resultSet.getInt("ID");
                allTypes.put(id, name);

            }

        }  catch (SQLException throwables) {
            System.err.println(throwables);
        }
    }

    private int getKeyFromValue(String value){
        Collection<Integer> keys = allTypes.keySet();

        for (int key : keys) {
            String obj = allTypes.get(key);

            if (value.equals(obj)) {
                return key;
            }
        }
        return -1;
    }

    private float getCostAndCount(String sql, String value){
        try {
            ResultSet resultSetCount = stml.executeQuery(sql);
            while(resultSetCount.next()){
                if (resultSetCount.isLast()){
                    return resultSetCount.getFloat(value);
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return 0.f;
    }

    public void onContactReceive(Message message) {
        Contact contact = message.getContact();
        user.setUserName(contact.getFirstName() + " " + contact.getLastName());
        user.setPhone(contact.getPhoneNumber());

        type = 102;
        sendMsg(message, "");
    }
}
