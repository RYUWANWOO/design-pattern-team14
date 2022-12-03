package com.holub.life.view;

import com.holub.life.model.Cell;
import com.holub.tools.Observer;
import com.holub.life.controller.Universe;
import com.holub.life.universe_settings.DummySettings;
import com.holub.ui.MenuSite;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UniverseView extends JPanel implements Observer {
    private final Universe universe;
    private static final int DEFAULT_CELL_SIZE = 8;
    private CellView cellView;
    private Cell cell;

    private UniverseView() {
        this.universe = Universe.getInstance();

        final Dimension PREFERRED_SIZE = new Dimension(universe.getWidthInCells() * DEFAULT_CELL_SIZE, universe.getWidthInCells() * DEFAULT_CELL_SIZE);
        this.cell = universe.getOutermostCell();
        this.cellView = new NeighborhoodView(cell,this);

        setBackground(Color.white);
        setPreferredSize(PREFERRED_SIZE);
        setMaximumSize(PREFERRED_SIZE);
        setMinimumSize(PREFERRED_SIZE);
        setOpaque(true);

        //{=Universe.mouse}
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                Rectangle bounds = getBounds();
                bounds.x = 0;
                bounds.y = 0;
                universe.userClicked(e.getPoint(), bounds);
                repaint();
            }
        });

        MenuSite.addLine(this, "Grid", "Clear", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                universe.clear();
                repaint();
            }
        });

        // {=Universe.load.setup}
//        MenuSite.addLine(this, "Grid", "Load",new ActionListener(){
//            public void actionPerformed(ActionEvent e){
//                universe.doLoad();
//            }
//        });

        MenuSite.addLine(this, "Grid", "Store", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                universe.doStore();
            }
        });

        MenuSite.addLine(this, "Grid", "Exit", new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public static UniverseView getInstance() {
        return UniverseView.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final UniverseView INSTANCE = new UniverseView();
    }

    public void paint(Graphics g) {
        Rectangle panelBounds = getBounds();
        Rectangle clipBounds = g.getClipBounds();

        // The panel bounds is relative to the upper-left
        // corner of the screen. Pretend that it's at (0,0)
        panelBounds.x = 0;
        panelBounds.y = 0;
//        outermostCell.redraw(g, panelBounds, true);		//{=Universe.redraw1}

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
                    //              outermostCell.redraw(g, panelBounds, false); //{=Universe.redraw2}
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
