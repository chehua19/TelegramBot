package Controller.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Order {

    private ArrayList<Basket> orderValue;
    private int timecode;

    public Order() {
        this.timecode = Integer.parseInt(new SimpleDateFormat("MMdd").format(new Date())) * 1000;
        orderValue = new ArrayList<>();
    }

    public void addNewOrder(Basket basket){
        orderValue.add(basket);
        timecode++;
    }

    public int getNumOfOrder(){
        return timecode;
    }

    public Basket getOrder(int num){
        return orderValue.get(num);
    }

    public ArrayList<Basket> getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(ArrayList<Basket> orderValue) {
        this.orderValue = orderValue;
    }
}
