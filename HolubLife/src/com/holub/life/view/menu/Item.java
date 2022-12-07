package com.holub.life.view.menu;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.ListIterator;

public class Item
{
    // private JMenuItem  item;
    private Component item;

    private String		parentSpecification; // of JMenu or of
    // JMenuItem's parent
    private MenuElement parent;		   		 // JMenu or JMenuBar
    private boolean		isHelpMenu;

    private MenuSite menuSite = null;

    public String toString()
    {	StringBuffer b = new StringBuffer(parentSpecification);
        if( item instanceof JMenuItem )
        {	JMenuItem i = (JMenuItem)item;
            b.append(":");
            b.append(i.getName());
            b.append(" (");
            b.append(i.getText());
            b.append(")");
        }
        return b.toString();
    }

    /*------------------------------------------------------------*/

    private boolean valid()
    {	assert item 	!= null : "item is null" ;
        assert parent	!= null : "parent is null" ;
        return true;
    }

    /*** Create a new Item. If the JMenuItem's name is the
     *  string "help" then it's assumed to be the help menu and
     *  is treated specially. Note that several help menus can
     *  be added to a site: They'll be stacked up at the far
     *  right in the reverse order of addition. Similarly
     *  file menus are stacked up at the far left.
     *
     *  @param item		 the item being added
     *  @param parent 	 The menu bar or a menu that
     *  				 contains the current item. Must
     *  				 be a JMenuBar or a JMenu.
     */

    public Item( Component item, MenuElement parent,
                 String parentSpecification )
    {	assert parent != null;
        assert parent instanceof JMenu || parent instanceof JMenuBar
                : "Parent must be JMenu or JMenuBar";

        this.item		   = item;
        this.parent		   = parent;
        this.parentSpecification = parentSpecification;
        this.isHelpMenu  =
                ( item instanceof JMenuItem )
                        && ( item.getName().compareToIgnoreCase("help")==0 );

        assert valid();
    }

    public boolean specifiedBy( String specifier )
    {	return parentSpecification.equals( specifier );
    }

    public Component item()
    {	return item;
    }

    /*** ******************************************************
     * Attach a menu item to it's parent (either a menu
     * bar or a menu). Items are added at the end of the
     * <code>menuBarContents</code> list unless a help
     * menu exists, in which case items are added at
     * the penultimate position.
     */

    public final void attachYourselfToYourParent(MenuSite menuSite)
    {	assert valid();

        this.menuSite = menuSite;

        LinkedList<Item> menuBarContents = menuSite.getMenuBarContents();

        if( parent instanceof JMenu )
        {	((JMenu)parent).add( item );
        }
        else if( menuBarContents.size() <= 0 )
        {	menuBarContents.add( this );
            ((JMenuBar)parent).add( item );
        }
        else
        {	Item last = (Item)(menuBarContents.getLast());
            if( !last.isHelpMenu )
            {
                menuBarContents.addLast(this);
                ((JMenuBar)parent).add( item );
            }
            else	// remove the help menu, add the new
            {		// item, then put the help menu back
                // (following the new item).

                menuBarContents.removeLast();
                menuBarContents.add( this );
                menuBarContents.add( last );

                if( parent == menuSite.getMenuBar())
                    parent = regenerateMenuBar();
            }
        }
    }

    /*** ******************************************************
     * Remove the current menu item from its parent
     * (either a menu bar or a menu). The Item is invalid
     * after it's detached, and should be discarded.
     */
    public void detachYourselfFromYourParent()
    {	assert valid();

        if( parent instanceof JMenu )
        {	((JMenu)parent).remove( item );
        }
        else // the parent's the menu bar.
        {
            menuSite.getMenuBar().remove( item );
            menuSite.getMenuBarContents().remove( this );
            regenerateMenuBar(); // without me on it

            parent = null;
        }
    }

    /*** ******************************************************
     * Set or reset the "disabled" state of a menu item.
     */

    public void setEnableAttribute( boolean on )
    {	if( item instanceof JMenuItem )
    {	JMenuItem item = (JMenuItem) this.item;
        item.setEnabled( on );
    }
    }

    /*** ******************************************************
     * Replace the old menu bar with a new one that reflects
     * the current state of the <code>menuBarContents</code>
     * list.
     */
    private JMenuBar regenerateMenuBar()
    {	assert valid();

        // Create the new menu bar and populate it from
        // the current-contents list.

        JMenuBar menuBar = new JMenuBar();
        menuSite.setMenuBar(menuBar);
        ListIterator i = menuSite.getMenuBarContents().listIterator(0);
        while( i.hasNext() )
            menuBar.add( ((Item)(i.next())).item );

        // Replace the old menu bar with the new one.
        // Calling setVisible causes the menu bar to be
        // redrawn with a minimum amount of flicker. Without
        // it, the redraw doesn't happen at all.

        menuSite.getMenuFrame().setJMenuBar( menuBar );
        menuSite.getMenuFrame().setVisible( true );
        return menuBar;
    }
}

/*** ***************************************************************
 * This class holds methods of interest only when you're
 * debugging. Don't include the associated class file in
 * the released version of the code.
 */