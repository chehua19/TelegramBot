package Controller.Item;

public class Item {

    private int id;
    private String name;

    private int parent_id;
    private String parent_name;

    private float countAll;
    private int countNeed;
    private float coast;

    public Item(){}

    public int getCountNeed() {
        return countNeed;
    }

    public void setCountNeed(int countNeed) {
        this.countNeed = countNeed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }

    public float getCountAll() {
        return countAll;
    }

    public void setCountAll(float count) {
        this.countAll = count;
    }

    public float getCoast() {
        return coast;
    }

    public void setCoast(float coast) {
        this.coast = coast;
    }

    @Override
    public String toString(){
        return name + " || Кількість: " + countAll + " || Ціна: " + coast;
    }

    public String toStringShop(){
        return name + " || Кількість: " + countNeed + " || Загальна ціна: " + mainCoast();
    }

    public float mainCoast(){
        return coast * countNeed;
    }
}
