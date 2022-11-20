package pacman.view.players;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import pacman.game.IAnimatedPlayer;

/**
 * Обобщённая фигура игрока.
 */
public abstract class AbstractPlayerFigure {
    
    protected final int cellSize;                                           // Размер ячейки лабиринта
    protected final int playerSize;                                         // Размер фигуры игрока, в пикселах
    
    private final Color[] bodyColors = {
        Color.YELLOW, Color.RED, Color.BLUE, Color.MAGENTA, Color.CYAN};    // Цвета игроков
    private final Color confusedGhostColor = Color.WHITE;                   // Цвет контуженного привидения
    
    /**
     * Создаёт новую фигуру игрока.
     * 
     * @param cellSize размер клетки
     */
    public AbstractPlayerFigure(int cellSize) {
        this.cellSize = cellSize;
        playerSize = ((cellSize * 80 / 100) + 1) / 5 * 5;
    }

    /**
     * Изображение фигуры игрока.
     * 
     * @param g2d графический контекст
     * @param myPlayer модель игрока
     */
    public abstract void drawPlayer(Graphics2D g2d, IAnimatedPlayer myPlayer);

    /**
     * Возвращает координаты центра заданной клетки игрового поля.
     *
     * @param cellCoordintes координата клетки игрового поля
     * @return координата центра клетки в пикселах
     */
    protected Point getMiddleCoordinate(Point cellCoordintes) {
        return new Point(cellSize * cellCoordintes.x, cellSize * cellCoordintes.y);
    }

    /**
     * Возвращает цвет игрока.
     * 
     * @param myPlayer модель игрока
     * @return цвет игрока
     */
    protected Color getColorId(IAnimatedPlayer myPlayer) {
        return bodyColors[myPlayer.getColorId()];
    }

    /**
     * Возвращает цвет контуженного привидения.
     * 
     * @return цвет контуженного привидения
     */
    protected Color getConfusedColor() {
        return confusedGhostColor;
    }
    
    /**
     * Возвращает заданное значение, округлённое до большего чётного.
     * 
     * @param numeral значение
     * @return значение, округлённое до большего чётного
     */
    protected int toBiggerEven(int numeral) {
        return ((numeral + 1) / 2 * 2);
    }
}
