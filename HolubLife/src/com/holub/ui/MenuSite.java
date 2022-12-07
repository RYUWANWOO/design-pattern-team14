package com.holub.ui;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import java.util.regex.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/*** **************************************************************** 
 *  A MenuSite is a frame that holds a menu bar.
 *  Other objects in
 *	the system (which do not have to be visual objects) can negotiate
 *	with the MenuSite to have menu's placed on the site's menu
 *  bar (or within submenus already found on the menu bar). These
 *  objects can easily remove the menu modifications they've made.
 *  <p>
 *  The MenuSite is a static Singleton. You cannot create one
 *  with <code>new</code>---It's made up entirely of <code>static</code>
 *  methods which you can access globally.
 *	<p>
 *  The first thing you must do is tell the system
 *  which JFrame will host the menu bar by calling
 *  {@link #establish MenuSite.establish(mainFrame)}. For example:
 *  <PRE>
 *  public class mainFrame extends JFrame
 *  {	public mainFrame()
 *  	{	MenuSite.establish( this );
 *			//...
 *  	}
 *  }
 *  </PRE>
 *  Once a menu site is established,
 *  objects that create user interfaces can add items
 *  to the menu site by calling {@link #addLine addLine(...)}
 *  (or more rarely, {@link #addMenu addMenu(...)}). In the
 *  following example, an <code>EmployeeManager</code> object
 *  adds an "Employee" menu that has two submenus: "Hire" and
 *  "Fire"
 *  <PRE>
 *  public class EmployeeManager
 *  {
 *  	public EmployeeManager()
 *  	{	MenuSite.addLine( this, "Employee",		 null )
 *  		MenuSite.addLine( this, "Employee:Hire," HireListener );
 *  		MenuSite.addLine( this, "Employee:Fire," FireListener );
 *  	}
 *
 *  }
 *  </PRE>
 *	The <code>HireListener</code> and <code>FireListener</code>
 *	objects implement {@link java.awt.event.ActionListener}, and
 *	are notified when the menu item is selected.
 *	<p>
 *  There's (deliberatly) no way to remove individual menu items.
 *  When the object that added items shuts down it's user interface,
 *  it issues a single call to {@link #removeMyMenus removeMyMenus}
 *  to remove <i>all</i> the menus and line items it added.
 *  <p>
 *  The creating object can also issue a
 *  {@link #setEnable} call to disable (or enable) all
 *  menu and menus it created.
 *  <p><b>Important:</b> A referece to the object that asked for
 *  the menu item to be inserted is passed into most of the methods
 *  of this class as the <code>requester</code> argument. The
 *  requester is used as a "key" in a hash table, and the hash-table
 *  lookup method uses both the object's <code>equals()</code> and
 *  <code>hashcode()</code> methods to do the lookup. For things
 *  to work properly, the object used as a requester should
 *  not implement <code>equals()</code>. Java visual objects
 *  like JComponent work fine as a requester, but be careful if
 *  you use an object of a class of your own devising. Solve the
 *  problem as follows:
 *  <PRE>
 *  class MyClass
 *  {	public boolean equals() { ... }
 *
 *		private final Object requesterId = new Object();
 *		public f()
 *		{	// Use requesterId instead of "this."
 *			MenuSite.addLine( requesterId, ... );
 *		}
 *	}
 *  </PRE>
 *	<p>If you haven't worked with menus before, bear in mind that
 *	menu items and menus have both a "name" and also a "label."
 *	The label is visible to the program's user, the name is
 *	an arbitrary internal string. In an internationalized application,
 *	the label will change with the local, but the name will
 *	be fixed. The current classes use only the menu "name." If you don't
 *	do anything special, the name is used as the label. You can provide
 *	a file that maps names to arbitrary strings (and also defines
 *	menu shortcuts) by calling {@link #mapNames mapNames(...)}.
 *
 *  @include /etc/license.txt
 */

public final class MenuSite
{
	private  JFrame		menuFrame	    = null;
	private  JMenuBar		menuBar  	= null;

	/*** The "requesters" table keeps track of who requested which
	 * menu items. It is indexed by requester and contains a
	 * Set of MenuSite.Item objects that identify all
	 * items added by that requester.
	 */
	private Map requesters = new HashMap();

	/*** Maps "names" to the visible labels that actually
	 *  appear on the screen.
	 */
	private static Properties nameMap;

	/*** Regular expression for extracting shortcuts from the
	 *  name-to-label map. The input syntax is the string to
	 *  the right of the = in the input line:
	 *  <code>
	 *  name = label; optional shortcut
	 *  </code>
	 *  This regular expression strips out leading and trailing
	 *  whitespace and all whitespace that surrounds the semicolon.
	 *  After matching, group(1) holds the label and group(2) either
	 *  holds the shortcut or is null if no shortcut is specified.
	 */
	private static Pattern shortcutExtractor =
				Pattern.compile(
						"\\s*([^;]+?)\\s*"				// value
						+"(;\\s*([^\\s].*?))?\\s*$" );	// ; shortcut

	/*** Isolate the menu names. Given an input string of the
	 *  form "one:two:three:four", after matching
	 *  group(1) holds "one", group(2) holds "two", etc.
	 *  up to 7 colons are recognized.
	 */
	private static Pattern submenuExtractor =
				Pattern.compile( "(.*?)(?::(.*?))?"
									+ "(?::(.*?))?"
									+ "(?::(.*?))?"
									+ "(?::(.*?))?"
									+ "(?::(.*?))?"
									+ "(?::(.*?))?" );

	/*** The menuBarContents list contains references to the
	 *	various menus that comprise the menu bar. This kludge is
	 *	necessary because Swing does not really support the notion
	 *	of a Help menu (though it claims to do so). In
	 *	particular, Swing won't let you insert menus anywhere
	 *	other than the far right of the menu bar, where the Help
	 *	menu should be. Removing the help menu and then adding it
	 *	back doesn't work reliably in all (or any?) Swing versions,
	 *	either. Consequently, when a new menu is added to
	 *	a menu bar, you need to clear out the existing menu bar
	 *	and rebuild it from scratch. This list holds the menu
	 *	items that comprise the menu bar in the order that they
	 *	will appear on the screen (left to right).
	 */

	private static final LinkedList<Item> menuBarContents =
												new LinkedList<>();

	public LinkedList<Item> getMenuBarContents(){
		return menuBarContents;
	}

	public void setMenuFrame(JFrame menuFrame) {
		this.menuFrame = menuFrame;
	}

	public void setMenuBar(JMenuBar menuBar) {
		this.menuBar = menuBar;
	}

	public JFrame getMenuFrame() {
		return menuFrame;
	}

	public JMenuBar getMenuBar() {
		return menuBar;
	}

	/*** ***************************************************************
	 * MenuSite is a singleton. A private constructor prevents
	 * you from manufacturing one using <code>new</code>.
	 */
	public MenuSite(){}

	/*** Check the current object for validity. If you use this
	 *  method in an assertion ("assert valid()") then it will
	 *  be removed when assetions are enabled.
	 *
	 *  //@throws AssertionException if the menu hasn't been established.
	 */

	private boolean valid()
	{	assert menuFrame != null : "MenuSite not established";
		assert menuBar   != null : "MenuSite not established";
		return true;
	}

	/*** ***************************************************************
	 * Establish a JFrame as the program's menu site. This method must
	 * be called before any of the other menu-site methods may be called.
	 * (Most of these will throw a {@link NullPointerException}
	 * if you try to use them when no menu site has been established.)
	 */
	public synchronized void establish(JFrame container)
	{
		assert container != null;
		assert menuFrame == null:
							"Tried to establish more than one MenuSite";
		//private static JFrame		menuFrame	= null;
		menuFrame = container;
		menuFrame.setJMenuBar( menuBar = new JMenuBar() );

		assert valid();
	}

	/*** **************************************************************
	 *  Create and add an empty menu to the menu bar.
	 *  Menus are generally created by {@link #addLine addLine(...)}.
	 *  This method
	 *  is provided for situations where one "requester" creates
	 *  a menu structure and other requesters will add line items
	 *  to this structure. By using one requester for the main
	 *  menu, and other requesters for the line items on the menu,
	 *  the requesters that added the line items can remove those
	 *  items without removing the menu that contained the items.
	 *	<p>
	 *  Menus are inserted on the menu bar just to the left of the
	 *  "Help" menu.
	 *	(The "help" menu [a menu whose <i>name</i> is the
	 *	string "help"---case is ignored] is special
	 *	in that it always appears on the far right of the menu bar.)
	 *	<p>
	 *  Use {@link #addLine addLine(...)}
	 *  to add line items to the menu created by the current call.
	 *  The name-to-label substitution described in
	 *  {@link #addLine addLine(...)} is
	 *  done here as well. As in that method, the <code>name</code> string
	 *  also defines the (visible) label if no mapping is found.
	 *  <p>
	 *  If the requested menu already exists, this method silently
	 *  does nothing.
	 *  <p>
	 *  @param requester The object that requested that this menu
	 *  	be added. All menus (and line items) added by a specific
	 *  	requster are removed by a single 
	 *  	{@link #removeMyMenus removeMyMenus(...)}	call.
	 *		The requester need not be the actual object that adds
	 *		the menu---there may not be a single one. It is simply
	 *		used to identify a group of menu items that will be
	 *		removed in bulk. All items that have the same requester
	 *		object are removed at once.
	 *
	 *  @param menuSpecifier The menu to create. A simple specifier (with
	 *  	no colons in it) creates an item on the menu bar itself.
	 *  	Submenus are specified using the syntax <code>"main:sub"</code>.
	 *  	For example,
	 *  	<code>addMenu( this, "File:New" )</code>
	 *  	creates a "New" submenu under the "File" menu. If the supermenu
	 *  	(in this example, "File") doesn't exist, it's created. You can
	 *  	have more than one colon if you want to go down more than one
	 *  	level (e.g. "Edit:Text:Size"). Up to six levels below the
	 *  	menu bar (six colons) are supported.
	 *  	(If you have more than that, you should seriously
	 *  	reconsider your menu structure.)
	 *  	Intermediate menus are
	 *  	added as necessary.
	 *
	 *  @throws IllegalArgumentException if the menuSpecifier is malformed
	 *  		(e.g. has spaces in it) or if the specifier identifies
	 *  		an existing line item (as compared to a menu).
	 */
	public void addMenu( Object requester, String menuSpecifier )
	{	createSubmenuByName( requester, menuSpecifier );
	}

	/*** **************************************************************
	 *  Adds a line item to a menu.
	 *  The menu is created if it does not already exist.
	 *  <p>
	 *  This method is the preferred way to both create menus and
	 *  add line items to existing menus.
	 *  See {@link #addMenu addMenu(...)} for the
	 *  rules of menu creation.
	 *  <p>
	 *  By default, the "name" is used for the "label." However,
	 *  when there is a name map (see {@link #mapNames}), then the name
	 *  parameter is used for the name, and the associated labels and
	 *  shortcuts specified in the map are used.
	 *  If there is a map, but the map has no
	 *  entry for the item named by the <code>name</code> parameter,
	 *  then the name is used for the label and a warning is logged to the
	 *  com.holub.ui stream using the standard java Logging APIs.
	 *
	 *  @param requester	The object that requested that this
	 *  					line item be added.
	 *
	 *  @param name		The (hidden) name text for this item.
	 *  				When there's no {@linkplain #mapNames name map},
	 *  				the same string is used for	both the name and the
	 *  				label (and there is no shortcut), otherwise
	 *  				the <code>name</code> argument specifies the
	 *  				name only, and the associated label
	 *  				(and shortcut) is taken	from the map.
	 *  				<p>
	 *  				Use the name <code>"-"</code> to place a separator
	 *  				into a menu. The <code>listener</code> argument
	 *  				is not used in this case, and can be null.
	 *
	 *  @param toThisMenu The specifier of the menu to which you're adding
	 *  				  the line item.
	 *  				  (See {@link #addMenu addMenu(...)}
	 *  				  for a discussion of specifiers.) The
	 *  				  specified menu is created if it doesn't
	 *  				  already exist.
	 *
	 *  @param listener	The ActionListener to notify when the menu
	 *  				item is selected.
	 *
	 *	@see #addMenu
	 *  @see #mapNames
	 */
	public void addLine(	Object requester,
								String toThisMenu,
								String name,
								ActionListener listener)
	{
		assert requester  != null: "null requester"	;
		assert name		  != null: "null item" 		;
		assert toThisMenu != null: "null toThisMenu";
		assert valid();

		// The "element" field is here only so that we don't create
		// a menu if the assertion in the else clause fires.
		// Otherwise, we could just create the items in the
		// if and else clauses.

		Component element;

		if( name.equals("-") )
			element = new JSeparator();
		else
		{	assert listener	!= null: "null listener";

			JMenuItem lineItem = new JMenuItem(name);
			lineItem.setName( name );
			lineItem.addActionListener( listener );
			setLabelAndShortcut( lineItem );

			element = lineItem;
		}

		JMenu found = createSubmenuByName( requester, toThisMenu );
		if( found==null )
			throw new IllegalArgumentException(
						"addLine() can't find menu ("+ toThisMenu +")" );

		Item item = new Item(element, found, toThisMenu );
		menusAddedBy(requester).add( item );
		item.attachYourselfToYourParent(this);
	}

	/*** **************************************************************
	 *  Remove all items that were added by this requester.
	 *  <p>
	 *  For the time being, the case of "foreign" items being
	 *  placed on a menu created by another requester is not
	 *  handled.
	 *  Consider a program in which two object both add an item
	 *  to the "File" menu. The first object to add an item will
	 *  be the official "owner" of the menu, since it created the
	 *  menu. When you call <code>removeMyMenus()</code> for this
	 *  first object, you  want to remove the line item it added
	 *  to the "File" menu, but you don't want to remove the "File"
	 *  menu itself because it's not empty. Right now, the only
	 *  solution to this problem is for a third requester to create
	 *  the menu itself using {@link #addMenu addMenu(...)}.
	 */

	public void removeMyMenus( Object requester )
	{
		assert requester != null;
		assert valid();

		Collection allItems=(Collection)( requesters.remove(requester) );

		if( allItems != null )
		{	Iterator i = allItems.iterator();
			while( i.hasNext() )
			{	Item current = (Item) i.next();
				current.detachYourselfFromYourParent();
			}
		}
	}

	/*** **************************************************************
	 * Disable or enable all menus and menu items added by a specific
	 * requester. You can disable a single menu item by using
	 * <code>
	 * MenuSite.getMyMenuItem(requester,"parent:spec", "name")
	 * 										.setEnabled(FALSE);
	 * </code>
	 *
	 * @param enable true to enable all the requester's menu items.
	 *
	 */
	public void setEnable(Object requester, boolean enable)
	{
		assert requester != null;
		assert valid();

		Collection allItems = (Collection)( requesters.get(requester) );

		if( allItems != null )
		{
			Iterator i = allItems.iterator();
			while( i.hasNext() )
			{	Item current = (Item) i.next();
				current.setEnableAttribute(enable);
			}
		}
	}

/// TODO:
///	/*** **************************************************************
///	 * Disable or enable the named menu items added by a specific
///	 * requester.
///	 * The <code>name</code> argument specifies the affected menu item.
///	 * Consider the call
///	 * Assuming that the same requester has created the Edit and
///	 * Edit:Options
///	 * options menus and put a Colors line on Edit:Options, all of the
///	 * following will work:
///	 * <code>
///	 * MenuSite.setEnable(this, "Edit" ,				 false);
///	 * MenuSite.setEnable(this, "Edit:Options" ,		 false);
///	 * MenuSite.setEnable(this, "Edit:Options:Colors", false);
///	 * MenuSite.setEnable(this, "Colors",				 false);
///	 * </code>
///	 *
///	 * @param name The name associated with a line item.
///	 * @param how  true to enable all the requester's menu items.
///	 */
///
///	public static void setEnable(Object requester,
///										String name, boolean how)
///	{
///	}
///
	/*** **************************************************************
	 * Get a menu item for external modification. You can use this
	 * method to get the {@link JMenuItem} that was created by
	 * {@link #addLine addLine(...)} under the covers. Use the returned
	 * reference to do things like disable the line item. Do not
	 * manipulate the menu structure (by adding and removing items),
	 * however.
	 *
	 * @param requester 	the object that inserted the menu or item
	 * @param menuSpecifier	the menuSpecifier passed to the original
	 * 						{@link #addMenu addMenu(...)} or
	 * 						{@link #addLine addLine(...)} call.
	 * @param name			the name passed to
	 * 						{@link #addLine addLine(...)}. null
	 * 						if you want a menu rather than a line item
	 * 						within the menu.
	 * @return				the underlying {@link JMenu} or
	 * 						{@link JMenuItem}.
	 * 						Returns <code>null</code> if the item
	 * 						doesn't exist.
	 */

	public JMenuItem getMyMenuItem(Object requester,
									String menuSpecifier, String name)
	{
		assert requester 		!= null;
		assert menuSpecifier	!= null;
		assert valid();

		Collection allItems = (Collection)( requesters.get(requester) );

		if( allItems != null )
		{	Iterator i = allItems.iterator();
			while( i.hasNext() )
			{	Item current = (Item) i.next();
				if( current.specifiedBy( menuSpecifier ) )
				{	if( current.item() instanceof JSeparator )
						continue;

					if( name==null && current.item() instanceof JMenu )
						return (JMenu)( current.item() );

					if(((JMenuItem)current.item()).getName().equals(name))
						return (JMenuItem) current.item();
				}
			}
		}
		return null;
	}


	//===========================================================
	// 			Private support methods and classes				|
	//===========================================================

	/*** **************************************************************
	 * Find the submenu specified by <code>specifier</code>. If it
	 * doesn't exist, create it.
	 * @see #addMenu
	 */
	private JMenu createSubmenuByName( Object requester,
												String menuSpecifier )
	{
		assert requester != null;
		assert menuSpecifier != null;
		assert valid();

		Matcher m = submenuExtractor.matcher(menuSpecifier);
		if( !m.matches() )
			throw new IllegalArgumentException(
										"Malformed menu specifier.");

		// If it's null, then start the search at the menu bar,
		// otherwise start the search at the menu addressed by "parent"

		JMenuItem   child  = null;
		MenuElement parent = menuBar;
		String		childName;

		for(int i=1; (childName = m.group(i++)) != null; parent=child )
		{
			child = getSubmenuByName(childName,parent.getSubElements());

			if( child != null )
			{	if( !(child instanceof JMenu) )	// it's a line item!
					throw new IllegalArgumentException(
							"Specifier identifes line item, not menu.");
			}
			else // it doesn't exist, create it
			{
				child = new JMenu		(childName);
				child.setName			(childName );
				setLabelAndShortcut	(child );

				Item item = new Item(child, parent, menuSpecifier );
				menusAddedBy(requester).add(item);
				item.attachYourselfToYourParent(this);
			}
		}

		return (JMenu)child; // the earlier instanceof guarantees safety
	}

	/*** Return the JMenu with the given name from the menu bar
	 *  or null if there is no such item.
	 *  This method doesn't understand colon-delimited specifiers. It
	 *  searches the level "contained" in the parent for an item with
	 *  the given "name."
	 */

	private JMenuItem getSubmenuByName( String name,
											MenuElement[] contents )
	{
		JMenuItem found = null;
		for( int i = 0; found==null && i < contents.length ; ++i )
		{
			// This is not documented, but the system creates internal
			// popup menus for empty submenus. If we come across one of
			// these, then look for "name" in the popup's contents. This
			// would be a lot easier if PopupMenu and JMenuItem
			// implemented a common interface, but they don't.
			// I can't use a class adapter to make them appear to
			// implement a comon interface becuase the JPopupWindows
			// are manufactured by Swing, not by me.

			if( contents[i] instanceof JPopupMenu )
				found = getSubmenuByName( name,
							((JPopupMenu)contents[i]).getSubElements());

			else if( ((JMenuItem) contents[i]).getName().equals(name) )
				found = (JMenuItem) contents[i];
		}
		return found;
	}

	/*** *************************************************************
	 *  Establishes a "map" of (hidden) names to (visible) labels and
	 *  shortcuts.
	 *  Establishing a map changes the behavior of
	 *  {@link #addLine addLine(...)} and
	 *  {@link #addMenu addMenu(...)} and
	 *  in that the specified label
	 *  and shortcut are installed automatically for all names specified
	 *  in the table.
	 *  A map must be specified before the item named in the map
	 *  are added to the menu site.
	 *  You may call this method multiple times to load multiple maps,
	 *  but the "name" component of each entry must be unique across
	 *  all maps.
	 *
	 *  @param table a Properties-style file that maps named
	 *  keys to labels, along with an optional
	 *  shortcuts. The general form is:
	 *  <PRE>
	 *  name.1 = label One; C
	 *  name.2 = label Two; Alt X
	 *  </PRE>
	 *  The shortcut can be specified in one of two ways. If it's
	 *  a single character, as in the first example, above,
	 *  the platform-default modifier is used. For example, in the
	 *  first example, the shortcut will be a Ctrl-C in Windows,
	 *  a Command-C on the Mac, etc.
	 *  Otherwise, the shortcut specifier must take the form described
	 *  in {@link javax.swing.KeyStroke#getKeyStroke(String)}.
	 *  For example:
	 *  <PRE>
	 *  F1
	 *  control DELETE
	 *  alt shift X
	 *  alt shift released X
	 *  typed a
	 *  </PRE>
	 *  Names like DELETE and F1 are shorthand for VK_DELETE and VK_F1.
	 *  (The complete set of VK_<i>xxx</i> constants are found in the
	 *  {@link java.awt.event.KeyEvent} class.) You can use any of
	 *  these "virtual" keys simply by removing the VK_.
	 *	<p>
	 *  For reasons that are mysterious to me, F10 is hard mapped
	 *  to display the main menu (so that you can navigate the
	 *  menus with the arrow keys). You could probably defeat this
	 *  behavior with a key binding, but it's easier to just
	 *  accept it as a fait accompli, and not try to define F10 as
	 *  a keyboard "shortcut."
	 *  <p>
	 *  The input file is a standard "Properties" file, which is
	 *  assumed to be ISO 8859-1 (not Unicode) encoded.
	 *  See {@link java.util.Properties#load} for a full
	 *  description of the file format.
	 *
	 *	@see java.awt.event.KeyEvent
	 *	@see java.util.Properties
	 *	@throws IOException if it can't load the table
	 */

	public void mapNames(URL table) throws IOException
	{	if( nameMap == null )
			nameMap = new Properties();
		nameMap.load( table.openStream() );
	}

	/***
	 * Add a name-to-label mapping manually. A mapping must be specified
	 * before the item is added to the menu site.
	 *
	 * @param name	The menu-item name passed to
	 * 					{@link #addMenu addMenu(...)}
	 * 				or	{@link #addMenu addLine(...)}.
	 * @param label    The visible label for that item.
	 * @param shortcut The shortcut, if any. Should be an empty string
	 * 				   (<code>""</code>) if no shortcut is required.
	 * 				   See {@link #mapNames mapNames(...)}
	 * 				   for information on how to
	 * 				   form this string.
	 *
	 * @see #mapNames
	 */

	public void addMapping( String name, String label,
														String shortcut)
	{	if( nameMap == null )
			nameMap = new Properties();
		nameMap.put( name, label + ";" + shortcut );
	}

	/*** ****************************************************************
	 *  This method modifies the incoming item to hold the label
	 *  and shortcut specified in name map
	 *  (see {@link #mapNames mapNames(...)}).
	 *  Normally, you would not call this method. Instead, you'd
	 *  insert items using
	 *  {@link #addLine addLine(...)},
	 *  which calls this method internally.
	 *  You may also call this method directly if you
	 *  intend to use
	 *  {@link #addLine addLine(...)} to insert
	 *  menu items.
	 *	<p>
	 *  The current
	 *  method does nothing if (1) there's no map or (2) the
	 *  name parameter isn't a key in the map. The incoming item
	 *  must have a name or label. If it has only a label, then
	 *  the name is set to the label and the label is replaced
	 *  by whatever label is found in the index file.
	 *  The method also silently does nothing if the incoming
	 *  <code>JMenuItem</code> does not have a name.
	 *
	 *  @see #mapNames
	 *  @see #addLine
	 */
	private void setLabelAndShortcut( JMenuItem item )
	{	String name = item.getName();
		if( name == null )
			return;

		String label;
		if( nameMap != null 
				&& (label= (String)(nameMap.get(name))) != null )
		{
			Matcher m = shortcutExtractor.matcher(label);
			if( !m.matches() )	// Malformed input line
			{
				item.setText( name );
				Logger.getLogger("com.holub.ui").warning
				(
					"Bad "
					+"name-to-label map entry:"
					+ "\n\tinput=[" + name + "=" + label + "]"
					+ "\n\tSetting label to " + name
				);
			}
			else
			{	item.setText( m.group(1) );

				String shortcut = m.group(3);

				if( shortcut != null )
				{	if( shortcut.length() == 1 )
					{	item.setAccelerator
						(  KeyStroke.getKeyStroke
						  ( shortcut.toUpperCase().charAt(0),
						    Toolkit.getDefaultToolkit().
						   					getMenuShortcutKeyMask(),
						    false
						  )
						);
					}
					else
					{	KeyStroke key=KeyStroke.getKeyStroke(shortcut);
						if( key != null )
							item.setAccelerator( key );
						else
						{	Logger.getLogger("com.holub.ui").warning
							( "Malformed shortcut parent specification "
								+ "in MenuSite map file: "
								+ shortcut
							);
						}
					}
				}
			}
		}
	}

	/*** *********************************************************
	 * Return a Collection of menu items associated with a given
	 * requester. A new (empty) Collection is created and returned
	 * if there are no menus associated with the requester at
	 * present.
	 */
	private Collection menusAddedBy( Object requester )
	{
		assert requester  != null: "Bad argument"  ;
		assert requesters != null: "No requesters" ;
		assert valid();

		Collection menus = (Collection)( requesters.get(requester) );
		if( menus == null )
		{	menus = new LinkedList();
			requesters.put( requester, menus );
		}
		return menus;
	}

	/*** ***********************************************************
	 * An Item makes the association between a line item or
	 * submenu and the MenuBar or Menu that contains it. You can
	 * ask an Item to add or remove itself from its container.
	 * All the weirdness associated with help menus is handled
	 * here.
	 */

}