package com.holub.ui;

import com.holub.life.controller.Universe;

import javax.swing.*;
import java.io.IOException;

public class UndoMenu implements MenuItem{


    private Universe universe;
    private MenuSite menuSite;


    public UndoMenu(MenuSite menuSite,Universe universe){
        this.menuSite = menuSite;
        this.universe = universe;
    }

    @Override
    public void setup() {
        menuSite.addLine(this, "Transaction", "Undo",
                e -> {
                    try {
                        universe.doUndo();
                    } catch (IOException theException) {
                        JOptionPane.showMessageDialog(null, "Undo Failed!",
                                "The Game of Life", JOptionPane.ERROR_MESSAGE);
                    }
                });


    }
}