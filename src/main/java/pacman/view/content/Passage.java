package pacman.view.content;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import pacman.game.IAction;
import pacman.game.IAnimatedGameModel;

/**
 * Изображение проходимой клетки.
 */
public class Passage implements IDrawableContent {
    
    private final int cornerRoundingSize;
    
    /**
     * Создаёт новое изображение проходимой клетки.
     * 
     * @param cornerRoundingSize радиус скругления угла стен
     */
    public Passage(int cornerRoundingSize) {
        this.cornerRoundingSize = cornerRoundingSize;
    }

    /*
     * Если клетка без стены: залить квадрат со скруглёнными углами
     * и скруглённые углы превращать в заполненные по необходимости,
     * при условии, что в смежной клетке отсутствует стена.
     */
    @Override
    public void drawContent(Graphics2D g2d, IAnimatedGameModel model, Point cell, int cellSize) {
        g2d.setPaint(Color.BLACK);
        g2d.fillRoundRect(cell.x * cellSize, cell.y * cellSize,
                cellSize, cellSize,
                2 * cornerRoundingSize, 2 * cornerRoundingSize);

        for (IAction action : model.getPossibleActions()) {
            if (model.getMaze().isCellMovable(action.getLocationAfterAction(cell))) {
                g2d.fill(squarePassageSide(cell, action, cellSize));
            }
        }
    }
    
    private Rectangle2D squarePassageSide(Point location, IAction action, int cellSize) {
        int dx = 0, dy = 0;
        int width = 0, height = 0;
        if (action.getAxisSign() == 1) {
            if (action.getBasis().x == 1) {
                dx = cellSize - cornerRoundingSize;
            } else if (action.getBasis().y == 1) {
                dy = cellSize - cornerRoundingSize;
            }
        }
        if (action.getBasis().x == 0) {
            width = cellSize;
            height = cornerRoundingSize;
        }
        if (action.getBasis().y == 0) {
            width = cornerRoundingSize;
            height = cellSize;
        }
        return new Rectangle2D.Float(
                location.x * cellSize + dx,
                location.y * cellSize + dy,
                width, height);
    }
}
