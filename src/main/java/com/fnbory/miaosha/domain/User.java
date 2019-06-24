package com.fnbory.miaosha.domain;

import lombok.Data;
import lombok.Getter;

/**
 * @Author: fnbory
 * @Date: 2019/6/14 20:19
 */

public class User {
    private  int id;
    private  String name;

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
}
