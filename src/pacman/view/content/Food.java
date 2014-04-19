package pacman.view.content;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import pacman.game.IAnimatedGameModel;

/**
 * Изображение единицы еды.
 */
public class Food implements IDrawableContent {

    private final int foodUnitSize;
    
    /**
     * Создаёт новое изображение единицы еды.
     * 
     * @param foodUnitSize размер единицы еды
     */
    public Food(int foodUnitSize) {
        this.foodUnitSize = foodUnitSize;
    }

    @Override
    public void drawContent(Graphics2D g2d, IAnimatedGameModel model, Point cell, int cellSize) {
        g2d.setPaint(Color.WHITE);
        g2d.fillOval((cellSize * cell.x) + cellSize/2 - foodUnitSize / 2,
                (cellSize * cell.y) + cellSize/2 - foodUnitSize / 2,
                foodUnitSize, foodUnitSize);
    }
}
