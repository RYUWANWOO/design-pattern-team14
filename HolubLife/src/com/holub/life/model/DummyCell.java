package com.holub.life.model;

import com.holub.life.controller.Universe;
import com.holub.tools.Direction;
import com.holub.tools.Storable;

import java.awt.*;

public class DummyCell implements Cell{
    public static DummyCell getInstance() {
        return DummyCell.LazyHolder.INSTANCE;
    }

    private static class LazyHolder{
        private static final DummyCell INSTANCE = new DummyCell();
    }

    @Override
    public boolean figureNextState(Cell north, Cell south, Cell east, Cell west, Cell northeast, Cell northwest, Cell southeast, Cell southwest) {
        return true;
    }

    @Override
    public Cell edge(int row, int column) {
        return this;
    }

    @Override
    public boolean transition() {
        return false;
    }

    @Override
    public void redraw(Graphics g, Rectangle here, boolean drawAll) {
    }

    @Override
    public void userClicked(Point here, Rectangle surface) {
    }

    @Override
    public boolean isAlive() {
        return false;
    }

    @Override
    public int widthInCells() {
        return 0;
    }

    @Override
    public Cell create() {
        return this;
    }

    @Override
    public Direction isDisruptiveTo() {
        return Direction.NONE;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean transfer(Storable memento, Point upperLeftCorner, boolean doLoad) {
        return false;
    }

    @Override
    public Storable createMemento() {
        throw new UnsupportedOperationException("Cannot create memento of dummy block");
    }
}
