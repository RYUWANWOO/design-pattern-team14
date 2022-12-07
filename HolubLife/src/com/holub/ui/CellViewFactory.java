package com.holub.ui;

import com.holub.life.controller.Clock;
import com.holub.life.model.Cell;
import com.holub.life.model.Neighborhood;
import com.holub.life.model.Resident;
import com.holub.life.view.CellView;
import com.holub.life.view.NeighborhoodView;
import com.holub.life.view.ResidentView;
import com.holub.life.view.UniverseView;

public class CellViewFactory {
    public static CellViewFactory getInstance() {
        return CellViewFactory.LazyHolder.INSTANCE;
    }

    private static class LazyHolder{
        private static final CellViewFactory INSTANCE = new CellViewFactory();
    }

    public CellView createCellView(Cell cell, UniverseView universeView) {
        if (cell instanceof Neighborhood) {
            return new NeighborhoodView(cell, universeView);
        } else if (cell instanceof Resident) {
            return new ResidentView(cell, universeView);
        }
        return null;
    }
}
