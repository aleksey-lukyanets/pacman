package pacman.game;

import java.awt.Point;

/**
 * Интерфейс действий игрока.
 * <p>
 * Определяет базовый интерфейс для действий, предпринимаемых игроками в
 * ортогональной системе координат.
 */
public interface IAction {

    /**
     * Возвращает знак направления передвижения.
     *
     * Возвращает <code>+1</code> если ось направления передвижения совпадает с
     * направлением возрастания значений графических координат, <code>-1</code>
     * если направления противоположны, <code>0</code> если действие не влечёт
     * передвижение игрока.
     *
     * @return знак направления передвижения, -1...+1
     */
    public int getAxisSign();

    /**
     * Возвращает единичный вектор передвижения {@literal point(dx, dy)}.
     *
     * @return единичный вектор
     * @see getAxisSign
     */
    public Point getBasis();

    /**
     * Возвращает направление вектора передвижения в градусах.
     * <p>
     * Отсчёт градусов ведётся в соответствии с принятыми в
     * {@link java.awt.Graphics2D} соглашениями: 0 градусов в точке "3 часа",
     * отсчёт положительных углов - против часовой стрелки.
     *
     * @return направление вектора в градусах, 0...360
     */
    public int getDirectionDegrees();

    /**
     * Возвращает координату после выполнения этого действия
     * {@literal (x + dx, y + dy)} из исходной координаты {@literal point(x, y)}.
     *
     * @param point координата до действия
     * @return координата в результате действия
     */
    public Point getLocationAfterAction(Point point);

    /**
     * Возвращает линейное перемещение координаты {@literal point(x, y)} после
     * выполнения этого действия: {@literal (x * dx + y * dy)}.
     *
     * @param point координата до действия
     * @return линейное перемещение в результате действия
     * @see getAxisSign
     */
    public int getShiftScalar(Point point);

    /**
     * Выполняет кратное {@literal factor} приращение координаты
     * {@literal point(x, y)} после выполнения этого действия; возвращает
     * координату вида {@literal (x + dx * factor, y + dy * factor)}.
     *
     * @param point координата до действия
     * @param factor множитель
     * @return координата с учётом кратного приращения
     * @see getAxisSign
     */
    public Point translateWithFactor(Point point, int factor);
}
