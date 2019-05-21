package com.softwareengineering.aasfalis.models;

import java.io.Serializable;

public class Test  implements Serializable {

    private static final long serialVersionUID = 1L;
    private String test;

    public Test(String test) {
        this.test = test;
    }

    public String getTest() {
        return test;
    }
}
