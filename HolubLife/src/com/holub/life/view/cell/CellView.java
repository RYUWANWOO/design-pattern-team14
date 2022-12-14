package com.holub.life.view.cell;

import com.holub.life.controller.Universe;
import com.holub.life.model.Cell;
import com.holub.tools.Observer;

import javax.swing.*;
import java.awt.*;

public interface CellView{
    public void redraw(Graphics g, Rectangle here, boolean drawAll);
    public void userClicked(Point here, Rectangle surface);
}
