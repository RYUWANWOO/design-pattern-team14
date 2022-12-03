package com.holub.life.universe_settings;

import com.holub.life.controller.Universe;
import com.holub.life.view.UniverseView;
import com.holub.ui.MenuSite;

import java.awt.*;
import java.awt.event.*;

public class SettingA extends SampleSetting{

    private Universe universe;
    private UniverseView universeview;

    public SettingA(){
        universe = Universe.getInstance();
        universeview = UniverseView.getInstance();
    }

    @Override
    public void view_setting(){
        universeview.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Rectangle bounds = universeview.getBounds();
                bounds.height /= universe.getWidthInCells();
                bounds.height *= universe.getWidthInCells();
                bounds.width = bounds.height;
                universeview.setBounds(bounds);
            }
        });

        universeview.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Rectangle bounds = universeview.getBounds();
                bounds.x = 0;
                bounds.y = 0;
                universe.userClicked(e.getPoint(), bounds);
                universeview.repaint();
            }
        });
    }

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
}
