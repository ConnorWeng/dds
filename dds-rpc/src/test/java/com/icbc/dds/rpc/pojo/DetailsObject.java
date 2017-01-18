package com.icbc.dds.rpc.pojo;

/**
 * Created by kfzx-wengxj on 17/01/2017.
 */
public class DetailsObject {
    private String name;
    private int[] intValues;

    public DetailsObject() {

    }

    public DetailsObject(String name, int[] intValues) {
        this.name = name;
        this.intValues = intValues;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getIntValues() {
        return intValues;
    }

    public void setIntValues(int[] intValues) {
        this.intValues = intValues;
    }

    @Override
    public String toString() {
        String ints = "";
        for (int i : intValues) {
            ints += i + ",";
        }
        return String.format("name:%s ints:%s", name, ints);
    }

    @Override
    public boolean equals(Object object) {
        DetailsObject object1 = (DetailsObject) object;
        boolean isSame = object1.getIntValues().length == this.intValues.length;
        if (isSame) {
            for (int i = 0; i < object1.getIntValues().length; i++) {
                if (object1.getIntValues()[i] != this.intValues[i]) {
                    isSame = false;
                }
            }
            return object1.getName().equals(this.name) && isSame;
        } else {
            return isSame;
        }
    }

    @Override
    public int hashCode() {
        return intValues.hashCode() + name.hashCode();
    }
}
