package com.holub.life.model;
import java.awt.*;

import com.holub.tools.Direction;
import com.holub.tools.Storable;

/***
 * This interface is the basic unit that comprises a life board.
 * It's implemented both by {@link Resident} (which represents
 * an individual cell on the board) and {@link Neighborhood},
 * which represents a group of cells.
 *
 * @include /etc/license.txt
 */

public interface Cell {
	/** Figure out the next state of the cell, given the specified
	 *  neighbors.
	 *  @return true if the cell is unstable (changed state).
	 */
	boolean figureNextState(	Cell north, 	Cell south,
								Cell east,		Cell west,
								Cell northeast, Cell northwest,
								Cell southeast, Cell southwest );

	/** Access a specific contained cell located at the edge of the
	 *  composite cell.
	 *  @param row 		The requested row. Must be on the edge of
	 *  				the block.
	 *  @param column	The requested column. Must be on the edge
	 *  				of the block.
	 *  @return	true	if the the state changed.
	 */
	Cell edge( int row, int column );

	/** Transition to the state computed by the most recent call to
	 *  {@link #figureNextState}
	 *  @return true if a changed of state happened during the transition.
	 */
	boolean transition();

	/** A user has clicked somewhere within you.
	 *  @param here The position of the click relative to the bounding
	 *  			rectangle of the current Cell.
	 */

	void userClicked(Point here, Rectangle surface);

	/** Return true if this cell or any subcells are alive.
	 */
	boolean isAlive();

	/** Return the specified width plus the current cell's width
	 */
	int widthInCells();

	/** Return a fresh (newly created) object identical to yourself
	 *  in content.
	 */
	Cell create();

	/** Returns a Direction indicated the directions of the cells
	 *  that have changed state.
	 *  @return A Direction object that indicates the edge or edges
	 *  		on which a change has occured.
	 */

	Direction isDisruptiveTo();

	/** Set the cell and all subcells into a "dead" state.
	 */

	void clear();

	/**
	 *	The Cell.Memento interface stores the state
	 *	of a Cell and all its subcells for future restoration.
	 *
	 *	@see Cell
	 */

	interface Memento extends Storable {
		/** On creation of the memento, indicate that a cell is
		 *  alive.
		 */
		void markAsAlive	(Point location);

		/** On restoration of a cell from a memento, indicate that
		 *  a cell is alive.
		 */
		boolean isAlive	(Point location);
	}

	/**  This method is used internally to save or restore the state
	 *   of a cell from a memento.
	 *   @return true if this cell was modified by the transfer.
	 */
	boolean transfer( Storable memento, Point upperLeftCorner, boolean doLoad );

	/** Possible value for the "load" argument to transfer() */
	public static boolean STORE = false;

	/** Possible value for the "load" argument to transfer() */
	public static boolean LOAD = true;

	/** This method is used by container of the outermost cell.
	 *  It is not used internally. It need be implemented only by
	 *  whatever class defines the outermost cell in the universe.
	 *  Other cell implementions should throw an
	 *  UnsupportedOperationException when this method is called.
	 */
	Storable createMemento();

	/** The DUMMY Singleton represents a permanently dead (thus stable)
	 * 	cell. It's used for the edges of the grid. It's a singleton.
	 * 	The Dummy class is private, but it is accessed through
	 * 	the public DUMMY field, declared below. I'd like this
	 * 	class to be private, but the JLS doesn't allow private
	 * 	members in an interface.
	 */

	public void	tick();
}
