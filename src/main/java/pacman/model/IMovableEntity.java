package pacman.model;

import java.awt.Point;
import pacman.game.IAction;

/**
 * 
 */
public interface IMovableEntity {

    /**
     * Устанавливает текущее действие объекта.
     *
     * @param action действие объекта
     */
    void setCurrentAction(IAction action);

    /**
     * Устанавливает сдвиг фигуры объекта относительно центра клетки.
     *
     * @param shift вектор сдвига фигуры объекта относительно центра клетки
     */
    void setAnimationShift(Point shift);
}
