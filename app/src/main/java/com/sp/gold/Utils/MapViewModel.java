package com.sp.gold.Utils;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MapViewModel extends ViewModel {
    private String name = "";
    public boolean isCC = false;
    public boolean isSAC = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCC() {
        return isCC;
    }

    public void setCC(boolean CC) {
        isCC = CC;
    }

    public boolean isSAC() {
        return isSAC;
    }

    public void setSAC(boolean SAC) {
        isSAC = SAC;
    }
}