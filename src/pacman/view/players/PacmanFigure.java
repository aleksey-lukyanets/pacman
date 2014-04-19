package pacman.view.players;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import pacman.game.IAction;
import pacman.game.IAnimatedPlayer;

/**
 * Класс фигуры Пакмана.
 */
public class PacmanFigure extends AbstractPlayerFigure {
    private final double pacmanBasicMouth = 50;         // Обычный угол раскрытия рта, в градусах
    private final double pacmanMinimalMouth = 10;       // Минимальный угол раскрытия рта, в градусах

    /**
     * Создаёт новую фигуру Пакмана.
     * 
     * @param cellSize размер клетки игрового поля
     */
    public PacmanFigure(int cellSize) {
        super(cellSize);
    }

    @Override
    public void drawPlayer(Graphics2D g2d, IAnimatedPlayer myPlayer) {
        Paint tempColor = g2d.getPaint();
        Point animationLocation = getMiddleCoordinate(myPlayer.getLocation());
        Point animationShift = myPlayer.getShiftVector();
        Point figureCenter = new Point(animationLocation);
        figureCenter.translate(animationShift.x + (cellSize-playerSize)/2, animationShift.y + (cellSize-playerSize)/2);

        g2d.setPaint(getColorId(myPlayer));
        IAction action = myPlayer.getCurrentAction();
        g2d.fillArc(figureCenter.x, figureCenter.y,
                playerSize, playerSize,
                action.getDirectionDegrees() + getPacmanMouthSize(myPlayer),
                360 - getPacmanMouthSize(myPlayer) * 2);

        g2d.setPaint(tempColor);
    }

    /**
     * Рассчитывает размер рта Пакмана для создания красивой анимации.
     *
     * @param action направление движения
     */
    private int getPacmanMouthSize(IAnimatedPlayer myPlayer) {
        double normalized = 0.5 * Math.abs(myPlayer.getShiftScalar()) / (cellSize / Math.PI * 0.5);
        return (int) (pacmanBasicMouth * Math.sin(normalized) + pacmanMinimalMouth);
    }
}