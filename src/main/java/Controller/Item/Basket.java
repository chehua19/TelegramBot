package Controller.Item;

import java.util.ArrayList;

public class Basket {

    private ArrayList<Item> itemsList;
    private int orderNum;
    private String orderText;
    private String user;

    public Basket(){
        itemsList = new ArrayList<>();
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public void addNewItem(Item item){
        itemsList.add(item);
    }

    public void removeItem(Item item){
        itemsList.remove(item);
    }

    public void clearBasket(){
        itemsList.clear();
    }

    public boolean haveItem(){
        return itemsList.size() > 0;
    }

    public ArrayList<Item> getItemsList() {
        return itemsList;
    }

    public void setItemsList(ArrayList<Item> itemsList) {
        this.itemsList = itemsList;
    }

    public double mainMoney(){
        return itemsList.stream().mapToDouble(Item::mainCoast).sum();
    }

    public String allItemsString(){
        int i = 0;
        StringBuilder basketElem = new StringBuilder();

        for (Item item: itemsList) {
            basketElem.append(i+1).append(". ").append(item.toStringShop()).append("\n");
            i++;
        }
        orderText = basketElem.toString();
        return orderText;
    }

    public String getOrderText() {
        return orderText;
    }

    public void setOrderText(String orderText) {
        this.orderText = orderText;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
