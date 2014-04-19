package pacman.game;

import algorithms.core.ISearchAlgorithm;
import java.awt.Point;

/**
 * Интерфейс управления моделью игры.
 * <p>
 * Определяет базовый интерфейс управления моделью игры со стороны контроллера.
 */
public interface IControlableGameModel {

    //------------------------------------------------- Выполнение хода Пакмана

    /**
     * Запрашивает инициализацию новой игры с установленными параметрами.
     */
    public void reinitializeGame();
    
    /**
     * Возвращает <code>true</code>, если игра завершилась победой или
     * поражением Пакмана.
     * 
     * @return <code>true</code>, если игра завершена
     */
    public boolean isGameComplete();
    
    /**
     * Устанавливает количество привидений в игре.
     * 
     * @param ghostsNumber количество привидений в игре
     */
    public void setGhostsNumber(int ghostsNumber);
    
    /**
     * Запрашивает выполнение очередного действия Пакманом.
     * 
     * @param algorithm алгоритм для расчёта действия
     * @param goal координата целевого расположения на игровом поле
     */
    public void performPacmanAction(ISearchAlgorithm algorithm, Point goal);
    
    /**
     * Возвращает координату клетки игрового поля по координате на холсте
     * игрового поле.
     * 
     * @param canvasCoordinate координата на холсте игрового поля, в пикселах
     * @return координата клетки игрового поля
     */
    public Point getCellAddress(Point canvasCoordinate);
}
