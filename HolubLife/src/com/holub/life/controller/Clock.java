package com.holub.life.controller;

import javax.swing.*;
import java.util.*;
import java.util.Timer;		// overrides java.awt.timer

import com.holub.tools.Observable;
import com.holub.tools.Observer;
import com.holub.tools.Visitor;
import com.holub.tools.Publisher;


public class Clock implements Observable {
	private Timer clock = new Timer();
	private TimerTask tick = null;

	private Clock(){}

	private static Clock instance;

	public static Clock getInstance() {
		return Clock.LazyHolder.INSTANCE;
	}

	private static class LazyHolder{
		private static final Clock INSTANCE = new Clock();
	}

	public void startTicking( int millisecondsBetweenTicks ){
		if(tick != null) {
			tick.cancel();
			tick=null;
		}

		if( millisecondsBetweenTicks > 0 ){
			tick =	new TimerTask() {
				public void run(){
					tick();
				}
			};
			clock.scheduleAtFixedRate( tick, 0, millisecondsBetweenTicks);
		}
	}

	/** Stop the clock
	 */

	public void stop(){
		startTicking( 0 );
	}

	private Publisher publisher = new Publisher();

	@Override
	public void registerObserver(Observer observer){
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

	public void tick(){
		publisher.publish( new Visitor(){
			@Override
			public void visit(Observer observer) {
				if(!menuIsActive()) {
					observer.update();
				}
			}
		});
	}

	private boolean menuIsActive(){
		MenuElement[] path = MenuSelectionManager.defaultManager().getSelectedPath();
		return ( path != null && path.length > 0 );
	}
}
