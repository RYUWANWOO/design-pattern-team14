package com.holub.life.view.menu;

import com.holub.life.controller.Universe;
import com.holub.life.view.UniverseView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GridMenu implements MenuItem{

    private MenuSite menuSite;
    private UniverseView universeView;
    private Universe universe;

    public GridMenu(MenuSite menuSite, UniverseView universeView, Universe universe) {
        this.menuSite = menuSite;
        this.universeView = universeView;
        this.universe = universe;
    }

    @Override
    public void setup() {
        menuSite.addLine(universeView, "Grid", "Store", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                universe.doStore();
            }
        });
        menuSite.addLine(universeView, "Grid", "Load",new ActionListener(){
            public void actionPerformed(ActionEvent e){
                universe.doLoad();
            }
        });
        menuSite.addLine(universeView, "Grid", "Exit", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menuSite.addLine(universeView, "Grid", "Clear", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                universe.clear();
                universeView.repaint();
            }
        });

    }
}
