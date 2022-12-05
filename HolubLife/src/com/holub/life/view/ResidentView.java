package com.holub.life.view;

import com.holub.life.controller.Universe;
import com.holub.life.model.Cell;
import com.holub.life.model.Resident;
import com.holub.tools.Observer;
import com.holub.ui.Colors;

import javax.swing.*;
import java.awt.*;

public class ResidentView extends JPanel implements CellView, Observer {
    private static final Color BORDER_COLOR = Colors.DARK_YELLOW;
    private static final Color LIVE_COLOR = Color.RED;
    private static final Color DEAD_COLOR = Colors.LIGHT_YELLOW;

    private Universe universe;
    private Resident resident;
    private Component component;

    public ResidentView(Cell cell, Universe universe, Component component) {
        this.universe = universe;
        this.resident = (Resident) cell;
        this.component = component;
        resident.registerObserver(this);
    }

    public void redraw(Graphics g, Rectangle here, boolean drawAll) {
        g = g.create();
        g.setColor(resident.isAmAlive() ? LIVE_COLOR : DEAD_COLOR);
        g.fillRect(here.x + 1, here.y + 1, here.width - 1, here.height - 1);

        // Doesn't draw a line on the far right and bottom of the
        // grid, but that's life, so to speak. It's not worth the
        // code for the special case.

        g.setColor(BORDER_COLOR);
        g.drawLine(here.x, here.y, here.x, here.y + here.height);
        g.drawLine(here.x, here.y, here.x + here.width, here.y);
        g.dispose();
    }

    @Override
    public void update() {
        component.repaint();
    }


    public void userClicked(Point here, Rectangle surface){
        resident.setAmALive(!resident.getAmALive());
    }
}
