package com.jmp.redissonEvaluation;

import java.io.Serializable;

public class SampleObject2 implements Serializable {
    private String Address;

    public SampleObject2(String address) {
        Address = address;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
