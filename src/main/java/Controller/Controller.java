package Controller;

import Controller.Item.Basket;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import jdk.nashorn.internal.objects.annotations.Property;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.sql.*;

public class Controller{

    @FXML
    public TableView orderTable;

    private static final String dataBaseURL = "jdbc:firebirdsql://localhost:3064/C:\\Program Files (x86)\\Ultra Demo Shop RU\\DB\\dbDemoShopRU.fdb";

    static final String USER = "sysdba";
    static final String PASS = "masterkey";
    public TableColumn num;
    public TableColumn value;
    public TableColumn user;

    public void initialize(){
        num.setCellValueFactory(new PropertyValueFactory<>("orderNum"));
        value.setCellValueFactory(new PropertyValueFactory<>("orderText"));
        user.setCellValueFactory(new PropertyValueFactory<>("user"));
    }

    public void loadBot(final ActionEvent actionEvent) {

        Connection conn = null;
        Statement stmt = null;
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            System.out.println("Connecting to database.");
            conn = DriverManager.getConnection(dataBaseURL, USER, PASS);
            stmt = conn.createStatement();

            telegramBotsApi.registerBot(new Bot(stmt, this));

            System.out.println("Bot activate)");
        }catch (TelegramApiRequestException | SQLException e){
            e.printStackTrace();
        }
    }

    public void addNewOrder(Basket order) {
        orderTable.getItems().add(order);
    }
}
