package pacman.game;

import java.awt.Dimension;
import java.util.List;

/**
 * Интерфейс анимирования модели.
 * <p>
 * Определяет базовый интерфейс получения от модели игры данных для её
 * графического изображения и приведения в движение.
 */
public interface IAnimatedGameModel {

    /**
     * Возвращает размер игрового поля.
     *
     * @return размер игрового поля в пикселах
     */
    public Dimension getGameCanvasDimension();

    /**
     * Возвращает размер клетки игрового поля.
     *
     * @return размер клетки игрового поля в пикселах
     */
    public int getMazeCellSize();

    /**
     * Возвращает <code>true</code>, если игра завершена поражением Пакмана.
     *
     * @return <code>true</code>, если игра завершена поражением Пакмана.
     */
    public boolean isGameLost();

    /**
     * Возвращает <code>true</code>, если игра завершена победой Пакмана.
     *
     * @return <code>true</code>, если игра завершена победой Пакмана.
     */
    public boolean isGameWon();

    /**
     * Возвращает игровое поле.
     *
     * @return игровое поле
     */
    public IGameField getMaze();

    /**
     * Возвращает перечень игроков.
     *
     * @return перечень игроков
     */
    public IAnimatedPlayer[] getPlayers();

    /**
     * Возвращает итератор по допустимым в игре действиям.
     *
     * @return итератор по действия
     */
    public List<IAction> getPossibleActions();

    /**
     * Возвращает итератор по углам клетки игрового поля (угол определяется как
     * пара действий передвижения).
     *
     * @return итератор по углам клетки игрового поля
     */
    public List<IAction[]> getCorners();

    /**
     * Возвращает <code>true</code>, если Пакманом выполнил все переданные ему
     * действия.
     *
     * @return <code>true</code>, если Пакманом выполнил все свои действия
     */
    public boolean isPacmanQueueEmpty();
}
