package com.holub.life.universe_settings;

import com.holub.life.controller.Universe;
import com.holub.life.view.UniverseView;
import com.holub.ui.MenuSite;

import java.awt.*;
import java.awt.event.*;

public class SettingB extends SettingA{
    @Override
    public void view_setting(){
        super.view_setting();
    }

    @Override
    public void menu_setting(){
        super.menu_setting();

        MenuSite.addLine(universeview, "Grid", "Store", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                universe.doStore();
            }
        });
        MenuSite.addLine(this, "Grid", "Load",new ActionListener(){
            public void actionPerformed(ActionEvent e){
                universe.doLoad();
            }
        });
    }

    @Override
    public void clock_setting(){
        super.clock_setting();

        MenuSite.addLine(this,"Go","Slow",		 		modifier);
        MenuSite.addLine(this,"Go","Medium",	 	 	modifier);
        MenuSite.addLine(this,"Go","Fast",				modifier); // {=endSetup}
    }
}
