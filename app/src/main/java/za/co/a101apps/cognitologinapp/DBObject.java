package za.co.a101apps.cognitologinapp;

public class DBObject {

    protected String ID;
    public DBObject() {

    }
    public DBObject(String id) {
        this.ID=id;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
