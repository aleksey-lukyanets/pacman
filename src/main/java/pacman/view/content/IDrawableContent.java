package pacman.view.content;

import java.awt.Graphics2D;
import java.awt.Point;
import pacman.game.IAnimatedGameModel;

/**
 * Интерфейс содержимого клетки.
 * <p>
 * Определяет базовый интерфейс изображения содержимого клетки.
 */
public interface IDrawableContent {

    /**
     * Рисует изображение содержимого.
     * 
     * @param g2d графический контекст
     * @param model модель игры
     * @param cell координата клетки
     * @param cellSize размер клетки
     */
    public abstract void drawContent(Graphics2D g2d, IAnimatedGameModel model, Point cell, int cellSize);
}
