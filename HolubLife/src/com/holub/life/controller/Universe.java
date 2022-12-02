package com.holub.life.controller;

import java.io.*;

import java.awt.*;
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

/**
 * The Universe is a mediator that sits between the Swing
 * event model and the Life classes. It is also a singleton,
 * accessed via Universe.instance(). It handles all
 * Swing events and translates them into requests to the
 * outermost Neighborhood. It also creates the Composite
 * Neighborhood.
 *
 * @include /etc/license.txt
 */

public class Universe implements Observable {
	private final Cell outermostCell;
	private Publisher publisher;

	/** The default height and width of a Neighborhood in cells.
	 *  If it's too big, you'll run too slowly because
	 *  you have to update the entire block as a unit, so there's more
	 *  to do. If it's too small, you have too many blocks to check.
	 *  I've found that 8 is a good compromise.
	 */
	private static final int DEFAULT_GRID_SIZE = 8;

	/** The size of the smallest "atomic" cell---a Resident object.
	 *  This size is extrinsic to a Resident (It's passed into the
	 *  Resident's "draw yourself" method.
	 */
	private static final int DEFAULT_CELL_SIZE = 8;

	// The constructor is private so that the universe can be created
	// only by an outer-class method [Neighborhood.createUniverse()].

	private Universe() {
		// Create the nested Cells that comprise the "universe." A bug
		// in the current implementation causes the program to fail
		// miserably if the overall size of the grid is too big to fit
		// on the screen.

		outermostCell = new Neighborhood(DEFAULT_GRID_SIZE,
				new Neighborhood(DEFAULT_GRID_SIZE, new Resident()));
		this.publisher = new Publisher();

		//{=Universe.clock.subscribe}
		Clock.getInstance().registerObserver(new Observer() {
			@Override
			public void update() {
				if (outermostCell.figureNextState(DummyCell.getInstance(), DummyCell.getInstance(), DummyCell.getInstance(), DummyCell.getInstance(),
						DummyCell.getInstance(), DummyCell.getInstance(), DummyCell.getInstance(), DummyCell.getInstance())) {
					if (outermostCell.transition()) {
						notifyObservers();
					}
				}
			}
		});
	}

	/** Singleton Accessor. The Universe object itself is manufactured
	 *  in Neighborhood.createUniverse()
	 */

	@Override
	public void registerObserver(Observer observer) {
		publisher.subscribe(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		publisher.cancelSubscription(observer);
	}

	@Override
	public void notifyObservers(){
		publisher.publish(new Visitor() {
			@Override
			public void visit(Object object) {
				((Observer)object).update();
			}
		});
	}

	public static Universe getInstance() {
		return LazyHolder.INSTANCE;
	}

	private static class LazyHolder{
		private static final Universe INSTANCE = new Universe();
	}

//	public void doLoad()
//	{	try
//		{
//			FileInputStream in = new FileInputStream(
//			   Files.userSelected(".",".life","Life File","Load"));
//
//			Clock.instance().stop();		// stop the game and
//			outermostCell.clear();			// clear the board.
//
//			Storable memento = outermostCell.createMemento();
//			memento.load( in );
//			outermostCell.transfer( memento, new Point(0,0), Cell.LOAD );
//
//			in.close();
//		}
//		catch( IOException theException )
//		{	JOptionPane.showMessageDialog( null, "Read Failed!",
//					"The Game of Life", JOptionPane.ERROR_MESSAGE);
//		}
//		repaint();
//	}

	public void doStore()
	{	try
		{
			FileOutputStream out = new FileOutputStream(
				  Files.userSelected(".",".life","Life File","Write"));

			Clock.getInstance().stop();		// stop the game

			Storable memento = outermostCell.createMemento();
			outermostCell.transfer( memento, new Point(0,0), Cell.STORE );
			memento.flush(out);

			out.close();
		}
		catch( IOException theException )
		{	JOptionPane.showMessageDialog( null, "Write Failed!",
					"The Game of Life", JOptionPane.ERROR_MESSAGE);
		}
	}

	public int getWidthInCells(){
		return outermostCell.widthInCells();
	}

	public void userClicked(Point here, Rectangle surface){
		outermostCell.userClicked(here,surface);
	}
	public void clear(){
		outermostCell.clear();
	}
	public void redraw(Graphics g, Rectangle here, boolean drawAll){
		outermostCell.redraw(g,here,drawAll);
	}
}
