package com.holub.life.universe_settings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.holub.life.controller.Universe;
import com.holub.life.view.UniverseView;

public class DummySettings{
    private Universe universe;
    private UniverseView universeview;

    public DummySettings(){
        universe = Universe.getInstance();
        universeview = UniverseView.getInstance();
    }

    public void establish(){
        universeview.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Rectangle bounds = universeview.getBounds();
                bounds.height /= universe.getWidthInCells();
                bounds.height *= universe.getWidthInCells();
                bounds.width = bounds.height;
                universeview.setBounds(bounds);
            }
        });
    }

}
