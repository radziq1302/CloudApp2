package za.co.a101apps.cognitologinapp;

import java.io.Serializable;

public class DBUserData extends DBObject implements Serializable {

    String username;
    String waga;
    String wzrost;
    String wiek;
    String plec;
    String aktywnosc;

    public DBUserData(String id, String username, String waga, String wzrost, String wiek, String plec, String aktywnosc) {
        super();
        this.ID=id;
        this.username = username;
        this.waga = waga;
        this.wzrost = wzrost;
        this.wiek = wiek;
        this.plec = plec;
        this.aktywnosc = aktywnosc;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getWaga() {
        return waga;
    }

    public void setWaga(String waga) {
        this.waga = waga;
    }

    public String getWzrost() {
        return wzrost;
    }

    public void setWzrost(String wzrost) {
        this.wzrost = wzrost;
    }

    public String getWiek() {
        return wiek;
    }

    public void setWiek(String wiek) {
        this.wiek = wiek;
    }

    public String getPlec() {
        return plec;
    }

    public void setPlec(String plec) {
        this.plec = plec;
    }

    public String getAktywnosc() {
        return aktywnosc;
    }

    public void setAktywnosc(String aktywnosc) {
        this.aktywnosc = aktywnosc;
    }
}
