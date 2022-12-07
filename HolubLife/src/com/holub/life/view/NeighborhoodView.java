package com.holub.life.view;

import com.holub.asynch.ConditionVariable;
import com.holub.life.model.Cell;
import com.holub.life.model.Neighborhood;
import com.holub.tools.Observer;
import com.holub.ui.Colors;

import javax.swing.*;
import java.awt.*;

public class NeighborhoodView extends JPanel implements Observer, CellView {
    private Neighborhood neighborhood;
    private UniverseView universeView;
    private CellView[][] gridView;

    public NeighborhoodView(Cell cell, UniverseView universeView) {
        this.neighborhood = (Neighborhood) cell;
        int gridSize = this.neighborhood.getGridSize();
        this.gridView = new CellView[gridSize][gridSize];
        this.universeView = universeView;
        neighborhood.registerObserver(this);

        for (int row = 0; row < gridSize; ++row) {
            for (int column = 0; column < gridSize; ++column) {
                if (neighborhood.getGrid()[row][column] instanceof Neighborhood) {
                    gridView[row][column] = new NeighborhoodView(neighborhood.getGrid()[row][column], universeView);
                } else {
                    gridView[row][column] = new ResidentView(neighborhood.getGrid()[row][column], universeView);
                }
            }
        }
    }

    @Override
    public void redraw(Graphics g, Rectangle here, boolean drawAll) {
        boolean amActive = neighborhood.isAmActive();
        boolean oneLastRefreshRequired = neighborhood.isOneLastRefreshRequired();
        ConditionVariable readingPermitted = neighborhood.getReadingPermitted();
        int gridSize = neighborhood.getGridSize();

        if (!amActive && !oneLastRefreshRequired && !drawAll) {
            return;
        }
        try {
            neighborhood.setOneLastRefreshRequired(false);
            int compoundWidth = here.width;
            Rectangle subcell = new Rectangle(here.x, here.y,
                    here.width / gridSize,
                    here.height / gridSize);

            // Check to see if we can paint. If not, just return. If
            // so, actually wait for permission (in case there's
            // a race condition, then paint.

            //{=Neighborhood.reading.not.permitted}
            if (!readingPermitted.isTrue()) {
                return;
            }

            readingPermitted.waitForTrue();

            for (int row = 0; row < neighborhood.getGridSize(); ++row) {
                for (int column = 0; column < neighborhood.getGridSize(); ++column) {
                    gridView[row][column].redraw(g, subcell, drawAll);    // {=Neighborhood.redraw3}
                    subcell.translate(subcell.width, 0);
                }
                subcell.translate(-compoundWidth, subcell.height);
            }

            g = g.create();
            g.setColor(Colors.LIGHT_ORANGE);
            g.drawRect(here.x, here.y, here.width, here.height);

            if (amActive) {
                g.setColor(Color.BLUE);
                g.drawRect(here.x + 1, here.y + 1,
                        here.width - 2, here.height - 2);
            }

            g.dispose();
        } catch (InterruptedException e) {    // thrown from waitForTrue. Just
            // ignore it, since not printing is a
            // reasonable reaction to an interrupt.
        }
    }

    @Override
    public void update() {
        universeView.repaint();
    }

    public void userClicked(Point here, Rectangle surface) {
        int pixelsPerCell = surface.width / neighborhood.getGridSize();
        int row = here.y / pixelsPerCell;
        int column = here.x / pixelsPerCell;
        int rowOffset = here.y % pixelsPerCell;
        int columnOffset = here.x % pixelsPerCell;

        Point position = new Point(columnOffset, rowOffset);
        Rectangle subcell = new Rectangle(0, 0, pixelsPerCell, pixelsPerCell);

        gridView[row][column].userClicked(position, subcell); //{=Neighborhood.userClicked.call}
        neighborhood.setAmActive(true);
        neighborhood.rememberThatCellAtEdgeChangedState(row, column);
    }
}
