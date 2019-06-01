package za.co.a101apps.cognitologinapp;

public class Woda extends DBObject {


    String number;
    String date;

    public Woda(String id, String num, String date) {
        super();
        this.ID = id;
        this.number=num;
        this.date=date;
//        required constructor
    }


    public String getNumber() {
        return number;
    }

    /*ste the telephone number*/
    public void setNumber(String telephone) {
        this.number = number;
    }

    /*get the name*/
    public String getDate() {
        return date;
    }

    /*set the name*/
    public void setDate(String date) {
        this.date = date;
    }
}
