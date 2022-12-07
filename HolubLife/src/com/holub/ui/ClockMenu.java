package com.holub.ui;

import com.holub.life.controller.Clock;
import com.holub.life.controller.Universe;
import com.holub.life.view.UniverseView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClockMenu implements MenuItem{

    private Clock clock;
    private MenuSite menuSite;

    ActionListener modifier;

    public ClockMenu(Clock clock, MenuSite menuSite) {
        this.clock = clock;
        this.menuSite = menuSite;
    }

    @Override
    public void setup() {
        modifier = new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String name = ((JMenuItem)e.getSource()).getName();
                char toDo = name.charAt(0);

                if( toDo=='D'){
                    clock.tick();
                    clock.tick();
                }else if( toDo=='T' )
                    clock.tick();				      // single tick
                else{
                    clock.startTicking(   toDo=='A' ? 500:	  // agonizing
                            toDo=='S' ? 150:	  // slow
                                    toDo=='M' ? 70 :	  // medium
                                            toDo=='F' ? 30 : 0 ); // fast
                }
            }
        };

        menuSite.addLine(this,"Go","Halt",  			modifier);
        menuSite.addLine(this,"Go","Tick (Single Step)",modifier);
        menuSite.addLine(this,"Go","Double Tick (Double Step)",modifier);
        menuSite.addLine(this,"Go","Agonizing",	 	  	modifier);
        menuSite.addLine(this,"Go","Slow",		 		modifier);
        menuSite.addLine(this,"Go","Medium",	 	 	modifier);
        menuSite.addLine(this,"Go","Fast",				modifier); // {=endSetup}
    }
}
