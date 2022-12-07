package com.holub.life.view;

import com.holub.life.controller.Clock;
import com.holub.life.model.Cell;
import com.holub.tools.Observer;
import com.holub.life.controller.Universe;
import com.holub.ui.ClockMenu;
import com.holub.ui.GridMenu;
import com.holub.ui.MenuSite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UniverseView extends JPanel implements Observer {
    private final Universe universe;
    private CellView cellView;
    private Cell cell;
    private static final int DEFAULT_CELL_SIZE = 8;

    public UniverseView(MenuSite menuSite, Universe universe, Cell cell, Clock clock) {
        this.universe = universe;
        this.cell = cell;
        this.cellView = new NeighborhoodView(cell, this);

        final Dimension PREFERRED_SIZE = new Dimension(universe.getWidthInCells() * DEFAULT_CELL_SIZE,
                universe.getWidthInCells() * DEFAULT_CELL_SIZE);

        GridMenu gridMenu = new GridMenu(menuSite,this,universe);
        ClockMenu clockMenu = new ClockMenu(clock,menuSite);

        gridMenu.setup();
        clockMenu.setup();

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                Rectangle bounds = getBounds();
                bounds.height /= universe.getWidthInCells();
                bounds.height *= universe.getWidthInCells();
                bounds.width = bounds.height;
                setBounds(bounds);
            }
        });

        setBackground(Color.white);
        setPreferredSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setMinimumSize(PREFERRED_SIZE);
        setOpaque(true);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Rectangle bounds = getBounds();
                bounds.x = 0;
                bounds.y = 0;
                cellView.userClicked(e.getPoint(), bounds);
                repaint();
            }
        });
    }

    public void paint(Graphics g) {
        Rectangle panelBounds = getBounds();
        Rectangle clipBounds = g.getClipBounds();

        // The panel bounds is relative to the upper-left
        // corner of the screen. Pretend that it's at (0,0)
        panelBounds.x = 0;
        panelBounds.y = 0;

        cellView.redraw(g, panelBounds, true);
    }

    private void refreshNow() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Graphics g = getGraphics();
                // Universe not displayable
                if (g == null) {
                    return;
                }
                try {
                    Rectangle panelBounds = getBounds();
                    panelBounds.x = 0;
                    panelBounds.y = 0;
                    cellView.redraw(g, panelBounds, false);
                } finally {
                    g.dispose();
                }
            }
        });
    }

    @Override
    public void update() {
        refreshNow();
    }
}
