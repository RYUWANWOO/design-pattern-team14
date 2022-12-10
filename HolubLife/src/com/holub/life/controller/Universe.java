package com.holub.life.controller;

import java.io.*;

import java.awt.*;
import java.util.Stack;
import javax.swing.*;

import com.holub.io.Files;
import com.holub.life.model.DummyCell;
import com.holub.tools.Observable;
import com.holub.tools.Observer;
import com.holub.tools.Storable;
import com.holub.tools.Visitor;
import com.holub.life.model.Cell;
import com.holub.life.model.Neighborhood;
import com.holub.life.model.Resident;
import com.holub.tools.Publisher;


public class Universe implements Observer {
    private Cell outermostCell;
    private Clock clock;
    private static final int DEFAULT_CELL_SIZE = 8;

    public Universe(Clock clock, Cell outermostCell) {
        this.clock = clock;
        this.outermostCell = outermostCell;
        this.clock.registerObserver(this);
    }

    private Stack<Storable> clickedCached = new Stack<>();

    @Override
    public void update() {
        clickedCached.push(outermostCell.createMemento());
        outermostCell.tick();
    }

    public void doUndo() throws IOException{
        if(!clickedCached.isEmpty()){
            Clock.getInstance().stop();
            outermostCell.clear();
            outermostCell.transfer(clickedCached.pop(),new Point(0,0),Cell.LOAD);
        }
    }

    public void doLoad() {
        try {
            FileInputStream in = new FileInputStream(
                    Files.userSelected(".", ".life", "Life File", "Load"));

            Clock.getInstance().stop();        // stop the game and
            outermostCell.clear();            // clear the board.

            Storable memento = outermostCell.createMemento();
            memento.load(in);
            outermostCell.transfer(memento, new Point(0, 0), Cell.LOAD);

            in.close();
        } catch (IOException theException) {
            JOptionPane.showMessageDialog(null, "Read Failed!",
                    "The Game of Life", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void doStore() {
        try {
            FileOutputStream out = new FileOutputStream(
                    Files.userSelected(".", ".life", "Life File", "Write"));

            Clock.getInstance().stop();        // stop the game

            Storable memento = outermostCell.createMemento();
            outermostCell.transfer(memento, new Point(0, 0), Cell.STORE);
            memento.flush(out);

            out.close();
        } catch (IOException theException) {
            JOptionPane.showMessageDialog(null, "Write Failed!",
                    "The Game of Life", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getWidthInCells() {
        return outermostCell.widthInCells();
    }

    public void clear() {
        clickedCached.clear();
        outermostCell.clear();
    }

    public void setActive(Cell cell, boolean b){
        cell.setAlive(b);
    }
    public void rememberThatCellAtEdgeChangedState(int row, int column){
        outermostCell.rememberThatCellAtEdgeChangedState(row,column);
    }
}
