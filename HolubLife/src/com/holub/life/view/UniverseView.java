package com.holub.life.view;

import com.holub.tools.Observer;
import com.holub.life.controller.Universe;
import com.holub.ui.MenuSite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UniverseView extends JPanel implements Observer {
    private final Universe universe;
    private static final int DEFAULT_CELL_SIZE = 8;

    private UniverseView(){
        this.universe = Universe.getInstance();
        this.universe.registerObserver(this);

        final Dimension PREFERRED_SIZE = new Dimension(universe.getWidthInCells() * DEFAULT_CELL_SIZE, universe.getWidthInCells() * DEFAULT_CELL_SIZE);

        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e) {
                // Make sure that the cells fit evenly into the
                // total grid size so that each cell will be the
                // same size. For example, in a 64x64 grid, the
                // total size must be an even multiple of 63.

                 Rectangle bounds = getBounds();
                 bounds.height /= universe.getWidthInCells();
                 bounds.height *= universe.getWidthInCells();
                 bounds.width  =  bounds.height;
                 setBounds( bounds );
            }
        });

        setBackground	( Color.white	 );
        setPreferredSize( PREFERRED_SIZE );
        setMaximumSize	( PREFERRED_SIZE );
        setMinimumSize	( PREFERRED_SIZE );
        setOpaque		( true			 );

        //{=Universe.mouse}
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e){
                Rectangle bounds = getBounds();
                bounds.x = 0;
                bounds.y = 0;
                universe.userClicked(e.getPoint(),bounds);
                repaint();
            }
        });

        MenuSite.addLine( this, "Grid", "Clear", new ActionListener() {
            public void actionPerformed(ActionEvent e){
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

        MenuSite.addLine(this, "Grid", "Store",new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                universe.doStore();
            }
        });

        MenuSite.addLine(this, "Grid", "Exit",new ActionListener(){
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
    }

    public static UniverseView getInstance() {
        return UniverseView.LazyHolder.INSTANCE;
    }

    private static class LazyHolder{
        private static final UniverseView INSTANCE = new UniverseView();
    }

    public void paint(Graphics g) {
        Rectangle panelBounds = getBounds();
        Rectangle clipBounds  = g.getClipBounds();

        // The panel bounds is relative to the upper-left
        // corner of the screen. Pretend that it's at (0,0)
        panelBounds.x = 0;
        panelBounds.y = 0;
//        outermostCell.redraw(g, panelBounds, true);		//{=Universe.redraw1}
        universe.redraw(g,panelBounds,true);
    }

    private void refreshNow()
    {	SwingUtilities.invokeLater
            (	new Runnable()
                 {	public void run()
                 {	Graphics g = getGraphics();
                     if( g == null )		// Universe not displayable
                         return;
                     try
                     {
                         Rectangle panelBounds = getBounds();
                         panelBounds.x = 0;
                         panelBounds.y = 0;
//                         outermostCell.redraw(g, panelBounds, false); //{=Universe.redraw2}
                         universe.redraw(g,panelBounds,false);
                     }
                     finally
                     {	g.dispose();
                     }
                 }
                 }
            );
    }

    @Override
    public void update() {
        refreshNow();
    }
}
