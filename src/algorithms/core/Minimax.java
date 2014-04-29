package algorithms.core;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Поиск решения через минимакс.
 * <p>
 * Реализует поиск решения в условиях конкуренции, где игрок с индексом
 * <code>pacmanId</code> стремится максимизировать счёт игры, а остальные игроки
 * - его минимизировать. Анализ выполняется на кол-во ходов <code>depth</code>.
 * <p>
 * Поиск выполняется через анализ состояний игры, которые будут получены в
 * результате каждого возможного действия каждого из игроков, на каждом цикле
 * ходов, на глубину в заданное количество ходов. Переход между ходами и
 * игроками осуществляет метод <code>getValue</code>, который также вызывает
 * соответствующие методы выбора наибольших и наименьших оценок.
 * <p>
 * Поддерживается режим включения или отключения альфа-бета-отсечения.
 *
 * @param <T> тип действий, предпринимаемых игроками
 */
public class Minimax<T> implements ISearchAlgorithm<T, IExtendedState<T>> {

    private final float alpha = Float.MIN_VALUE;
    private final float beta = Float.MAX_VALUE;
    private final int depth;                            // Глубина анализа, в ходах
    private final Map<Float, T> actionsTree = new HashMap<Float, T>();
    private final PruningMode pruningMode;                                // 
    
    private int maximizerId;                             // Идентификатор максимизирующего игрока, счёт от нуля
    private int totalAgents;                                // Общее количество агентов

    /**
     * Создаёт новый экземпляр алгоритма.
     * 
     * @param depth глубина анализа, в количестве ходов, 1...+inf
     * @param pruning включение режима альфа-бета-отсечения
     */
    public Minimax(int depth, PruningMode pruning) {
        this.depth = depth;
        this.pruningMode = pruning;
    }
    
    /**
     * Значения режима альфа-бета-отсечения.
     */
    public enum PruningMode {OFF, ON;}

    private enum Operation {
        //<editor-fold defaultstate="collapsed">
        MIN(Float.MAX_VALUE),
        MAX(Float.MIN_VALUE);

        private final float initialValue;

        Operation(float initial) {
            initialValue = initial;
        }

        /**
         * @return начальное значение переменной отбора значений
         */
        public float getInitialValue() {
            return initialValue;
        }
        //</editor-fold>
    }

    @Override
    public Queue<T> getSolution(IExtendedState<T> gameState, Point start, Point goal, int playerId) {
        Queue<T> solution = new LinkedList<T>();
        solution.add(getMaximizingAction(gameState, playerId));
        return solution;
    }

    /**
     * Возвращает оптимальное действие для максимизирующего игрока.
     *
     * @param state состояние игры
     * @param maximizerId индекс максимизирующего игрока, счёт от нуля
     * @return оптимальное действие
     */
    public T getMaximizingAction(IExtendedState<T> state, int maximizerId) {
        totalAgents = state.getPlayersNumber();
        this.maximizerId = maximizerId;
        actionsTree.clear();

        float value = getValue(state, 0, -1, alpha, beta);               // Запуск алгоритма поиска оптимального (для уровня 0) действия с возвратом его цены
        
        //System.out.println("---------------------------------------------------------");
        //System.out.println(actionsTree.values());
        //System.out.println(actionsTree.keySet());
        
        // Если среди возможных действий нет действия с возвращённым поиском значением,
        // значит смерть максимизирующего агента неизбежна
        if (!actionsTree.containsKey(value)) {
            //System.out.println("всё кончено");
            return actionsTree.values().iterator().next();              // Выбрать первое попавшееся действие
        }
        
        T action = actionsTree.get(value);
        //System.out.println("    Оптимальное действие: " + action + " с оценкой " + value);
        
        //System.out.println("");
        //System.out.println("");
        //System.out.println("");

        return action;                      // Возврат оптимального действия
    }

    /**
     * Возвращает оценку состояния игры.
     * <p>
     * Вычисляет оценку состояния игры для заданного игрока и заданного уровня
     * глубины. Работает рекурсивно.
     *
     * @param state состояние игры
     * @param currentDepthLevel текущая глубина, ноль - верхний уровень
     * @param playerId идентификатор игрока, счёт от нуля
     * @return эвристическая оценка состояния игры
     */
    private float getValue(IExtendedState<T> state, int currentDepthLevel, int playerId, float a, float b) {
        // Переключение индекса игрока и уровня глубины:
        // а) если текущий игрок - последний, то перейти к следующему уровню
        // глубины и начать с первого игрока;
        // б) если игрок не последний, то перейти к следующему игроку
        if (playerId + 1 == totalAgents) {
            currentDepthLevel += 1;
            playerId = 0;
            //System.out.println("---- следующий уровень: " + currentDepthLevel);
        } else {
            playerId += 1;
            //System.out.println("следующий агент: " + playerId);
        }

        // Если игра завершена победой или поражением, либо достигнут заданный
        // уровень глубины поиска, то вернуть значение оценочной функции
        if ((state.isWin() == true) || (state.isLose() == true) || (currentDepthLevel == depth)) {
            //System.out.println("получено значение: " + state.getGameScore());
            return state.getGameScore() + state.getPacmanEvaluation();
        }

        // Рассчитать дальнейшую оценку через соответствующую игроку функцию:
        // для максимизирующего использовать выбор максимального значения
        if (playerId == maximizerId) {
            return evaluateMinMax(Operation.MAX, state, currentDepthLevel, playerId, a, b);
        } else {
            return evaluateMinMax(Operation.MIN, state, currentDepthLevel, playerId, a, b);
        }
    }

    /**
     * Возвращает максимальную/минимальную оценку из возможных.
     * <p>
     * Из возможных действий максимизирующего (минимизирующего) игрока выбирает
     * действие с наибольшей (наименьшей) оценкой. Работает рекурсивно.
     *
     * @param op тип операции: максимизация (минимизация)
     * @param state состояние игры
     * @param currentDepthLevel текущая глубина, ноль - верхний уровень
     * @param currentPlayerIndex индекс текущего игрока, счёт от нуля
     * @return выбранное значение значение
     */
    private float evaluateMinMax(Operation op, IExtendedState<T> state, int level, int agentIndex, float a, float b) {
        float v = op.getInitialValue();                     // Инициализация значения

        // Выбор возвращаемого значения:
        // а) если игра завершена победой или поражением, то использовать значение оценочной функции;
        // б) иначе выбрать действие, дающее состояние игры с наибольшей оценкой
        if ((state.isWin() == true) || (state.isLose() == true)) {
            v = state.getGameScore();
        } else {
            // Получить список возможных действий игрока
            List<T> agentActions = state.getLegalActions(agentIndex);
            //System.out.println("агент " + agentIndex + " действия: " + agentActions);

            // Для каждого действия из списка:
            // а) получить состояние игры, которое получится в результате действия;
            // б) рассчитать оценку состояния-потомка;
            // в) выбрать наибольшую оценку
            for (T action : agentActions) {
                IExtendedState<T> leafState = state.getSuccessorState(agentIndex, action);
                //System.out.println("агент " + agentIndex + " действие: " + action);
                float leafStateValue = getValue(leafState, level, agentIndex, a, b);

                switch (op) {
                    case MAX:
                        v = Math.max(v, leafStateValue);
                        //System.out.println("выбрано MAX " + action + " = " + v + " из (" + v + ", " + leafStateValue + ")");
                        // Для верхнего уровня поиска: запомнить действие и его оценку
                        if (level == 0) {
                            actionsTree.put(leafStateValue, action);
                        }
                        if (pruningMode == PruningMode.ON) {
                            if (v > b) {
                                return v;
                            }
                            a = Math.max(a, v);
                        }
                        break;

                    case MIN:
                        v = Math.min(v, leafStateValue);
                        //System.out.println("выбрано MIN " + action + " = " + v + " из (" + v + ", " + leafStateValue + ")");
                        if (pruningMode == PruningMode.ON) {
                            if (v < a) {
                                return v;
                            }
                            b = Math.min(b, v);
                        }
                        break;
                }
            }
        }

        return v;
    }
}
