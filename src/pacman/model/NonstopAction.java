package pacman.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import pacman.game.IAction;

/**
 * Класс действия.
 * <p>
 * Объединяет особенности движения на север, восток, запад, юг. Ожидание на
 * месте не разрешено.
 * <p>
 * Использует готовый пул клеток игрового поля с целью повторного использования
 * имеющихся объектов.
 */
public enum NonstopAction implements IAction {

    NORTHWARD(0, -1, 90),
    WESTWARD(-1, 0, 180),
    SOUTHWARD(0, 1, 270),
    EASTWARD(1, 0, 360);

    private final int degrees;
    private final int dx;
    private final int dy;
    private final Point basis;
    
    private NonstopAction(int dx, int dy, int deg) {
        this.dx = dx;
        this.dy = dy;
        this.degrees = deg;
        this.basis = new Point(dx, dy);
    }

    private static final List<IAction> values = new ArrayList<IAction>();
    private static final List<IAction[]> corners = new ArrayList<IAction[]>(Arrays.asList(
                new IAction[]{EASTWARD, SOUTHWARD},
                new IAction[]{EASTWARD, NORTHWARD},
                new IAction[]{WESTWARD, SOUTHWARD},
                new IAction[]{WESTWARD, NORTHWARD}));
    private static List<Point> loctionsPool = new ArrayList<Point>();
    
    static {
        values.add(NORTHWARD);
        values.add(WESTWARD);
        values.add(SOUTHWARD);
        values.add(EASTWARD);
    }

    //----------------------------------------------- Статические методы класса

    /**
     * Возвращает итератор по допустимым действиям.
     *
     * @return итератор по действия
     */
    public static List<IAction> getPossibleActions() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Возвращает итератор по углам клетки игрового поля (угол определяется как
     * пара действий передвижения).
     *
     * @return итератор по углам клетки игрового поля
     */
    public static List<IAction[]> getCorners() {
        return Collections.unmodifiableList(corners);
    }
    
    /**
     * Возвращает действие по умолчанию.
     * 
     * @return действие по умолчанию
     */
    public static IAction getDefault() {
        return EASTWARD;
    }
    
    /**
     * Устанавливает пул клеток игрового поля.
     * 
     * @param pool пул клеток игрового поля
     */
    public static void setLocationsPoll(List<Point> pool) {
        NonstopAction.loctionsPool = pool;
    }
    
    /**
     * Возвращает координату после выполнения последовательности действий
     * из исходной координаты {@literal point(x, y)}.
     *
     * @param start координата до действия
     * @param sequence последовательности действий
     * @return координата в результате действия
     */
    public static Point getLocationAfterSequence(Point start, Queue<IAction> sequence) {
        Point location = start;
        if (sequence.isEmpty() == false) {
            Queue<IAction> queue = new LinkedList<IAction>(sequence);
            while (queue.isEmpty() == false) {
                IAction action = queue.remove();
                location = action.getLocationAfterAction(location);
            }
        }
        return location;
    }
    
    //----------------------------------------------- Методы интерфейса IAction

    @Override
    public int getDirectionDegrees() {
        return degrees;
    }

    @Override
    public Point getBasis() {
        return basis;
    }

    @Override
    public int getAxisSign() {
        return (dx + dy);
    }

    @Override
    public Point getLocationAfterAction(Point p) {
        Point newPoint = new Point(p);
        newPoint.translate(dx, dy);
        if (loctionsPool.contains(newPoint)) {
            return loctionsPool.get(loctionsPool.indexOf(newPoint));
        } else {
            return newPoint;
        }
    }

    @Override
    public Point translateWithFactor(Point point, int factor) {
        Point newPoint = new Point(point);
        newPoint.translate(dx * factor, dy * factor);
        return newPoint;
    }

    @Override
    public int getShiftScalar(Point coords) {
        return (dx * coords.x + dy * coords.y);
    }
}
