package pacman.view.content;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import pacman.game.IAction;
import pacman.game.IAnimatedGameModel;
import pacman.game.IGameField;

/**
 * Изображение стены.
 */
public class Wall implements IDrawableContent {
    
    private final int cornerRoundingSize;
    
    /**
     * Создаёт новое изображение стены.
     * 
     * @param cornerRoundingSize радиус скругления угла стены
     */
    public Wall(int cornerRoundingSize) {
        this.cornerRoundingSize = cornerRoundingSize;
    }

    /*
     * Если клетка со стеной: залить квадрат и закруглять его углы
     * при условии, что в двух смежных клетка со стороны угла
     * отсутствуют стены.
     */
    @Override
    public void drawContent(Graphics2D g2d, IAnimatedGameModel model, Point cell, int cellSize) {
        g2d.setPaint(Color.BLACK);
        for (IAction[] corner : model.getCorners()) {
            IAction axisX = corner[0];
            IAction axisY = corner[1];
            IGameField maze = model.getMaze();
            if ((maze.isCellMovable(axisX.getLocationAfterAction(cell)))
                    && (maze.isCellMovable(axisY.getLocationAfterAction(cell)))) {
                g2d.fill(roundWallCorner(cell, axisX, axisY, cellSize));
            }
        }
    }
    
    private Area roundWallCorner(Point location, IAction actionX, IAction actionY, int cellSize) {
        int dxRect = 0, dyRect = 0;
        int dxEll = 0, dyEll = 0;
        if (actionX.getAxisSign() == 1) {
            dxRect = cellSize - cornerRoundingSize;
            dxEll = cellSize - 2 * cornerRoundingSize;
        }
        if (actionY.getAxisSign() == 1) {
            dyRect = cellSize - cornerRoundingSize;
            dyEll = cellSize - 2 * cornerRoundingSize;
        }
        Rectangle2D rect =  new Rectangle2D.Float(
                location.x * cellSize + dxRect,
                location.y * cellSize + dyRect,
                cornerRoundingSize, cornerRoundingSize);
        Area ar = new Area(rect);
        Area ell = new Area(new Arc2D.Float(
                location.x * cellSize + dxEll,
                location.y * cellSize + dyEll,
                2 * cornerRoundingSize, 2 * cornerRoundingSize,
                Math.min(actionX.getDirectionDegrees(), actionY.getDirectionDegrees()),
                Math.max(actionX.getDirectionDegrees(), actionY.getDirectionDegrees()),
                Arc2D.PIE));
        ar.subtract(ell);
        return ar;
    }
}
