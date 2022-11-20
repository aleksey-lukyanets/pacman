package algorithms.core;

import java.awt.Point;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Принятие решений по обстановке (реагирующий агент).
 * <p>
 * Реализует алгоритм выбора оптимального действия на основании эвристической
 * оценки действий, возможных из текущего расположения.
 *
 * @param <T> тип действий, предпринимаемых игроками
 */
public class Reflex<T> implements ISearchAlgorithm<T, IExtendedState<T>> {

    /**
     * Создаёт новый экземпляр алгоритма.
     */
    public Reflex() {
    }

    @Override
    public Queue<T> getSolution(IExtendedState<T> gameState, Point start, Point goal, int playerId) {
        Queue<T> solution = new LinkedList<T>();
        solution.add(getAction(gameState, playerId));
        return solution;
    }

    /**
     * Возвращает действие Пакмана.
     * <p>
     * Выбирает действие с максимальной эвристической оценкой из возможных
     * действий Пакмана из текущего расположения.
     *
     * @param state объект состояния игры
     * @param playerId идентификатор игрока
     * @return оптимальное действие
     */
    public T getAction(IExtendedState<T> state, int playerId) {
        // Получить список допустимых действий
        List<T> legalActions = state.getLegalActions(playerId);

        SortedMap<Float, T> scores = new TreeMap<Float, T>();

        // Для каждого действия из списка:
        // а) получить состояние игры, которое получится в результате действия;
        // б) рассчитать оценку состояния-потомка;
        // в) добавить оценку в список оценок
        for (T action : legalActions) {
            IExtendedState<T> leafState = state.getSuccessorState(playerId, action);
            Float value = getBetterEvaluation(leafState);
            scores.put(value, action);
        }

        // Вернуть действие с наибольшей оценкой
        return scores.get(scores.lastKey());
    }

    private float getBetterEvaluation(IExtendedState<T> leafState) {
        return 2 * leafState.getPacmanEvaluation() + leafState.getGameScore();
    }
}
