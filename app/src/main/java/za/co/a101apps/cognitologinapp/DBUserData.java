package za.co.a101apps.cognitologinapp;

import java.io.Serializable;

public class DBUserData extends DBObject implements Serializable {

    String username;
    String waga;
    String wzrost;
    String wiek;
    String plec;
    String aktywnosc;
    String kroki;
    String woda;
    String sen;

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

    public DBUserData(String id, String waga, String kroki, String woda, String sen) {
        super();
        this.ID=id;
        this.waga = waga;
        this.kroki = kroki;
        this.woda = woda;
        this.sen = sen;

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
