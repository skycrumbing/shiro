package com.how2java.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * Created by Administrator on 2018/9/17 0017.
 */
@Data
public class User {
    private int id;
    private String name;
    private String password;
    private String salt;
    public User( String name, String password){
        this.password = password;
        this.name = name;
    }
    public User(){}
}
