package com.example.worker.fratriaru;

public class Item {
    /**
     * Заголовок
     */
    public String header;
    public String subHeader;
    public String comments;

    Item(String header, String subHeader, String comments){
        this.header=header;
        this.subHeader=subHeader;
        this.comments=comments;
    }

    //Всякие гетеры и сеттеры
    public String getHeader() {
        return header;
    }
    public void setHeader(String header) {
        this.header = header;
    }
}