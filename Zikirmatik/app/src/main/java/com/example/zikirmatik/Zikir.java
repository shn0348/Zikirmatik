package com.example.zikirmatik;

import java.io.Serializable;

public class Zikir  implements Serializable {
    Integer id;
    String name;
    int adet;
    Integer durakNo;

    public Zikir(Integer id, String name, int adet,int durakNo) {
        this.id = id;
        this.name = name;
        this.adet = adet;
        this.durakNo=durakNo;
    }
}
