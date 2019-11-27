package com.example.ohhaseon.test.item;

public class RecyclerItem {

    private String nick;
    private String comment;
    private String date;

    public RecyclerItem(String nick, String comment, String date){
        this.nick = nick;
        this.comment = comment;
        this.date = date;
    }

    public String getNick() {
        return nick;
    }
    public String getComment() {
        return comment;
    }
    public String getDate() {
        return date;
    }

}
