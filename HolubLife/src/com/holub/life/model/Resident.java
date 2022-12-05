package com.holub.life.model;

import java.awt.*;

import com.holub.tools.*;
import com.holub.ui.Colors;	// Contains constants specifying various
							// colors not defined in java.awt.Color.


/*** ****************************************************************
 * The Resident class implements a single cell---a "resident" of a
 * block.
 *
 * @include /etc/license.txt
 */

public final class Resident implements Cell, Observable {
	private Publisher publisher;

	private boolean amAlive 	= false;
	private boolean willBeAlive	= false;

	public Resident(){
		this.publisher = new Publisher();
	}

	private boolean isStable() {
		return amAlive == willBeAlive;
	}

	/** figure the next state.
	 *  @return true if the cell is not stable (will change state on the
	 *  next transition().
	 */
	public boolean figureNextState(
							Cell north, 	Cell south,
							Cell east, 		Cell west,
							Cell northeast, Cell northwest,
							Cell southeast, Cell southwest ) {
		verify( north, 		"north"		);
		verify( south, 		"south"		);
		verify( east, 		"east"		);
		verify( west, 		"west"		);
		verify( northeast,	"northeast"	);
		verify( northwest,	"northwest" );
		verify( southeast,	"southeast" );
		verify( southwest,	"southwest" );

		int neighbors = 0;

		if( north.	  isAlive()) ++neighbors;
		if( south.	  isAlive()) ++neighbors;
		if( east. 	  isAlive()) ++neighbors;
		if( west. 	  isAlive()) ++neighbors;
		if( northeast.isAlive()) ++neighbors;
		if( northwest.isAlive()) ++neighbors;
		if( southeast.isAlive()) ++neighbors;
		if( southwest.isAlive()) ++neighbors;

		willBeAlive = (neighbors==3 || (amAlive && neighbors==2));
		return !isStable();
	}

	private void verify( Cell c, String direction ) {
		assert (c instanceof Resident) || (c == DummyCell.getInstance())
				: "incorrect type for " + direction +  ": "
				+ c.getClass().getName();
	}

	/** This cell is monetary, so it's at every edge of itself. It's
	 *  an internal error for any position except for (0,0) to be
	 *  requsted since the width is 1.
	 */
	public Cell	edge(int row, int column) {
		assert row==0 && column==0;
		return this;
	}

	public boolean transition() {
		boolean changed = isStable();
		amAlive = willBeAlive;
		return changed;
	}

	public void tick(){
		if (figureNextState(DummyCell.getInstance(), DummyCell.getInstance(), DummyCell.getInstance(), DummyCell.getInstance(),
				DummyCell.getInstance(), DummyCell.getInstance(), DummyCell.getInstance(), DummyCell.getInstance())) {
			if (transition()) {
				notifyObservers();
			}
		}
	}

	public boolean isAmAlive(){
		return this.amAlive;
	}

	public void userClicked(Point here, Rectangle surface){
		amAlive = !amAlive;
	}

	public void clear() {
		amAlive = willBeAlive = false;
		notifyObservers();
	}

	public boolean isAlive() {
		return amAlive;
	}

	public Cell create() {
		return new Resident();
	}

	public int widthInCells() {
		return 1;
	}

	public Direction isDisruptiveTo() {
		return isStable() ? Direction.NONE : Direction.ALL;
	}

	public boolean transfer(Storable blob,Point upperLeft,boolean doLoad){
		Memento memento = (Memento)blob;
		if( doLoad ){
			if( amAlive = willBeAlive = memento.isAlive(upperLeft) ) {
				return true;
			}
		}
		// store only live cells
		else if( amAlive ) {
			memento.markAsAlive(upperLeft);
		}

		return false;
	}

	/** Mementos must be created by Neighborhood objects. Throw an
	 *  exception if anybody tries to do it here.
	 */
	public Storable createMemento() {
		throw new UnsupportedOperationException("May not create memento of a unitary cell");
	}

	@Override
	public void registerObserver(Observer observer) {
		publisher.subscribe(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		publisher.cancelSubscription(observer);
	}

	@Override
	public void notifyObservers() {
		publisher.publish(new Visitor() {
			@Override
			public void visit(Observer observer) {
				observer.update();
			}
		});
	}

	public boolean getAmALive(){
		return this.amAlive;
	}
	public boolean getWillAmAlive(){
		return this.willBeAlive;
	}
	public void setAmALive(boolean b){
		this.amAlive = b;
	}
	public void setWillBeAlive(boolean b){
		this.willBeAlive = b;
	}
}
