package Controller.Item;

public class User {

    private String userName;
    private String phone;

    public User(){ }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String toString(){
        return  "Покупатель: " + userName + ". Телефон: " + phone;
    }
}
