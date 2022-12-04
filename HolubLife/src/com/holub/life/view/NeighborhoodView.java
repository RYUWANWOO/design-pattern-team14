package com.holub.life.view;

import com.holub.asynch.ConditionVariable;
import com.holub.life.controller.Universe;
import com.holub.life.model.Cell;
import com.holub.life.model.Neighborhood;
import com.holub.tools.Observer;
import com.holub.ui.Colors;

import javax.swing.*;
import java.awt.*;

public class NeighborhoodView extends JPanel implements Observer, CellView {
    private Universe universe;
    private Neighborhood neighborhood;
    private CellView[][] gridView;
    private Component component;

    public NeighborhoodView(Cell cell,Component component){
        this.universe = Universe.getInstance();
        this.neighborhood = (Neighborhood)cell;
        int gridSize = this.neighborhood.getGridSize();
        this.gridView = new CellView[gridSize][gridSize];
        this.component = component;
        ((Neighborhood) cell).registerObserver(this);

        for( int row = 0; row < gridSize; ++row ) {
            for (int column = 0; column < gridSize; ++column) {
                if(neighborhood.getGrid()[row][column] instanceof Neighborhood){
                    gridView[row][column] = new NeighborhoodView(neighborhood.getGrid()[row][column],component);
                }else{
                    gridView[row][column] = new ResidentView(neighborhood.getGrid()[row][column],component);
                }
            }
        }
    }

    @Override
    public void redraw(Graphics g, Rectangle here, boolean drawAll) {
        // If the current neighborhood is stable (nothing changed
        // in the last transition stage), then there's nothing
        // to do. Just return. Otherwise, update the current block
        // and all sub-blocks. Since this algorithm is applied
        // recursively to sublocks, only those blocks that actually
        // need to update will actually do so.

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
        component.repaint();
    }
}
