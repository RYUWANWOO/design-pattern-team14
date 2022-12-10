package com.holub.tools;

import com.holub.life.controller.Universe;
import com.holub.life.model.Cell;
import com.holub.life.model.Neighborhood;
import com.holub.life.model.Resident;
import com.holub.life.view.cell.CellView;
import com.holub.life.view.cell.NeighborhoodView;
import com.holub.life.view.cell.ResidentView;
import com.holub.life.view.UniverseView;

public class CellViewFactory {
    public static CellViewFactory getInstance() {
        return CellViewFactory.LazyHolder.INSTANCE;
    }

    private static class LazyHolder {
        private static final CellViewFactory INSTANCE = new CellViewFactory();
    }

    public CellView createCellView(Cell cell, UniverseView universeView, Universe universe) {
        if (cell instanceof Neighborhood) {
            return new NeighborhoodView(cell, universeView, universe);
        } else if (cell instanceof Resident) {
            return new ResidentView(cell, universeView, universe);
        }
        return null;
    }
}
