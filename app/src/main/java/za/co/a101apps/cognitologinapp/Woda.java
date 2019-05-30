package za.co.a101apps.cognitologinapp;

public class Woda {

    String ID;
    String number;
    String date;

    public Woda(String ID, String num, String date) {
        this.ID=ID;
        this.number=num;
        this.date=date;
//        required constructor
    }

    public String getID() {
        return ID;
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
