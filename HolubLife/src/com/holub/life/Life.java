package com.holub.life;

import java.awt.*;
import java.util.LinkedList;
import javax.swing.*;

import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.model.Cell;
import com.holub.life.model.Neighborhood;
import com.holub.life.model.Resident;
import com.holub.life.view.UniverseView;
import com.holub.ui.ClockMenu;
import com.holub.ui.GridMenu;
import com.holub.ui.MenuSite;

/*******************************************************************
 * An implemenation of Conway's Game of Life.
 *
 * @include /etc/license.txt
 */
public final class Life extends JFrame {
    private static final int DEFAULT_GRID_SIZE = 8;

    public static void main(String[] arguments) {
        new Life();
    }

    private Life() {
        //제목을 정해요 - 건들 필요 없고
        super("The Game of Life. " + "(c)2003 Allen I. Holub <http://www.holub.com>");

        MenuSite menuSite = new MenuSite();
        menuSite.establish(this);//{=life.java.establish}

        Clock clock = Clock.getInstance();
        Cell outermostCell = new Neighborhood(DEFAULT_GRID_SIZE, new Neighborhood(DEFAULT_GRID_SIZE, new Resident()));
        Universe universe = new Universe(clock,outermostCell);
        UniverseView universeview = new UniverseView(menuSite,universe,outermostCell,clock);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(universeview, BorderLayout.CENTER); //{=life.java.install}

        pack();
        //visible도 건들 필요가 없다.
        setVisible(true);
    }
}