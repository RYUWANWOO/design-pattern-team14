package com.holub.life.universe_settings;

import com.holub.life.controller.Universe;
import com.holub.life.model.Cell;

public abstract class SampleSetting {
    public void establish(){
        view_setting();
        menu_setting();
        clock_setting();
    }

    abstract void view_setting();
    abstract void menu_setting();
    abstract void clock_setting();
}
