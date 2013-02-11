package game;

public abstract class Item {

    public String name;
    public int id;

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    protected void init(String name) {
        this.name = name;
    }
}
