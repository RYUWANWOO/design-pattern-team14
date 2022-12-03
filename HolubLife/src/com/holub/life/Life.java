package com.holub.life;

import java.awt.*;
import javax.swing.*;

import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.model.DummyCell;
import com.holub.life.view.UniverseView;
import com.holub.ui.MenuSite;

/*******************************************************************
 * An implemenation of Conway's Game of Life.
 *
 * @include /etc/license.txt
 */

public final class Life extends JFrame {
    public static void main(String[] arguments) {
        new Life();
    }

    private Life() {
        super("The Game of Life. " + "(c)2003 Allen I. Holub <http://www.holub.com>");

        MenuSite.establish(this);        //{=life.java.establish}

        Clock clock = Clock.getInstance();
        Universe universe = Universe.getInstance();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(UniverseView.getInstance(), BorderLayout.CENTER); //{=life.java.install}

        pack();
        setVisible(true);
    }
}
