package edu.umkc.dl.ndd;

import java.util.List;

public class Grams {

    private Integer[] gramsAsArray;
    private List<Integer> gramsAsList;

    public Integer[] getGramsAsArray() {
        return gramsAsArray;
    }

    public void setGramsAsArray(Integer[] gramsAsArray) {
        this.gramsAsArray = gramsAsArray;
    }

    public List<Integer> getGramsAsList() {
        return gramsAsList;
    }

    public void setGramsAsList(List<Integer> gramsAsList) {
        this.gramsAsList = gramsAsList;
    }

    public int getLength() {
        return this.gramsAsArray.length;
    }
}

