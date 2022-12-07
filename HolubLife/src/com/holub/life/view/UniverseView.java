package com.holub.life.view;

import com.holub.life.controller.Clock;
import com.holub.life.model.Cell;
import com.holub.tools.Observer;
import com.holub.life.controller.Universe;
import com.holub.ui.ClockMenu;
import com.holub.ui.GridMenu;
import com.holub.ui.MenuSite;
import com.holub.ui.UndoMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UniverseView extends JPanel{
    private final Universe universe;
    private CellView cellView;
    private Cell cell;
    private static final int DEFAULT_CELL_SIZE = 8;

    public UniverseView(MenuSite menuSite, Universe universe, Cell cell, Clock clock) {
        this.universe = universe;
        this.cell = cell;
        this.cellView = new NeighborhoodView(this.cell, this);

        final Dimension PREFERRED_SIZE = new Dimension(this.universe.getWidthInCells() * DEFAULT_CELL_SIZE,
                this.universe.getWidthInCells() * DEFAULT_CELL_SIZE);

        GridMenu gridMenu = new GridMenu(menuSite,this,this.universe);
        ClockMenu clockMenu = new ClockMenu(clock,menuSite);
        UndoMenu undoMenu = new UndoMenu(menuSite,universe);

        gridMenu.setup();
        clockMenu.setup();
        undoMenu.setup();


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
}
