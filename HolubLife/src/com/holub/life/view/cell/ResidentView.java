package com.holub.life.view.cell;

import com.holub.life.model.Cell;
import com.holub.life.model.Resident;
import com.holub.life.view.UniverseView;
import com.holub.tools.Observer;
import com.holub.life.view.Colors;

import javax.swing.*;
import java.awt.*;

public class ResidentView extends JPanel implements CellView, Observer {
    private static final Color BORDER_COLOR = Colors.DARK_YELLOW;
    private static final Color LIVE_COLOR = Color.RED;
    private static final Color DEAD_COLOR = Colors.LIGHT_YELLOW;

    private Resident resident;
    private UniverseView universeView;

    public ResidentView(Cell cell, UniverseView universeView) {
        this.resident = (Resident) cell;
        this.universeView = universeView;
        resident.registerObserver(this);
    }

    public void redraw(Graphics g, Rectangle here, boolean drawAll) {
        g = g.create();
        g.setColor(resident.isAmAlive() ? LIVE_COLOR : DEAD_COLOR);
        g.fillRect(here.x + 1, here.y + 1, here.width - 1, here.height - 1);

        g.setColor(BORDER_COLOR);
        g.drawLine(here.x, here.y, here.x, here.y + here.height);
        g.drawLine(here.x, here.y, here.x + here.width, here.y);
        g.dispose();
    }

    @Override
    public void update() {
        universeView.repaint();
    }

    public void userClicked(Point here, Rectangle surface) {
        resident.setAmALive(!resident.getAmALive());
    }
}
