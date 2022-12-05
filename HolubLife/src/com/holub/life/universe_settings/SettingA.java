package com.holub.life.universe_settings;

import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.model.Cell;
import com.holub.life.view.UniverseView;
import com.holub.ui.MenuSite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingA extends SampleSetting{
    Universe universe;
    UniverseView universeview;
    Clock clock;
    ActionListener modifier;
    Cell cell;

    public SettingA(Universe universe, Cell cell, UniverseView universeView){
        this.universe = universe;
        this.cell = cell;
        this.universeview = universeView;
        clock = Clock.getInstance();
    }
//
//    @Override
//    public void view_setting(){
//        universeview.addComponentListener(new ComponentAdapter() {
//            public void componentResized(ComponentEvent e) {
//                Rectangle bounds = universeview.getBounds();
//                bounds.height /= universe.getWidthInCells();
//                bounds.height *= universe.getWidthInCells();
//                bounds.width = bounds.height;
//                universeview.setBounds(bounds);
//            }
//        });
//
//        universeview.addMouseListener(new MouseAdapter() {
//            public void mousePressed(MouseEvent e) {
//                Rectangle bounds = universeview.getBounds();
//                bounds.x = 0;
//                bounds.y = 0;
//                universe.userClicked(e.getPoint(), bounds);
//                universeview.repaint();
//            }
//        });
//    }

    @Override
    public void menu_setting(){
        MenuSite.addLine(universeview, "Grid", "Clear", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                universe.clear();
                universeview.repaint();
            }
        });

        MenuSite.addLine(universeview, "Grid", "Exit", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    @Override
    public void clock_setting(){
        modifier = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String name = ((JMenuItem)e.getSource()).getName();
                char toDo = name.charAt(0);

                if( toDo=='D'){
                    clock.tick();
                    clock.tick();
                }else if( toDo=='T' )
                    clock.tick();				      // single tick
                else{
                    clock.startTicking(   toDo=='A' ? 500:	  // agonizing
                            toDo=='S' ? 150:	  // slow
                                    toDo=='M' ? 70 :	  // medium
                                            toDo=='F' ? 30 : 0 ); // fast
                }
            }
        };

        MenuSite.addLine(this,"Go","Halt",  			modifier);
        MenuSite.addLine(this,"Go","Tick (Single Step)",modifier);
        MenuSite.addLine(this,"Go","Double Tick (Double Step)",modifier);
        MenuSite.addLine(this,"Go","Agonizing",	 	  	modifier);
    }
}
