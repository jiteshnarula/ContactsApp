package com.example.jiteshnarula.contactsapp;

/**
 * Created by JiteshNarula on 13-03-2018.
 */

public class ListDataCollection {

    private String name;
    private String phone;
    private byte[] image;
    private int id;

    public ListDataCollection(int id, String name, String phone, byte[] image) {

        this.name = name;
        this.phone = phone;
        this.image = image;
        this.id = id;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}

