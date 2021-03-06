package com.jmp.redissonEvaluation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SampleObject implements Serializable {
    private String name;
    private String family;
    private double wage;
    private int age;
    List<SampleObject2> sampleObject2s=new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public List<SampleObject2> getSampleObject2s() {
        return sampleObject2s;
    }

    public void setSampleObject2s(List<SampleObject2> sampleObject2s) {
        this.sampleObject2s = sampleObject2s;
    }

    public double getWage() {
        return wage;
    }

    public void setWage(double wage) {
        this.wage = wage;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
