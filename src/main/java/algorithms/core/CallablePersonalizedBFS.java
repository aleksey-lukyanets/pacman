package algorithms.core;

import java.awt.Point;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.Callable;

/**
 * Поиск в ширину с учётом типа игрока.
 * <p>
 * Реализует выполнение алгоритма поиска в ширину в отдельном потоке с возвратом
 * результата вычислений. Учитывает тип игрока, для которого ведётся расчёт.
 *
 * @param <T> тип действий, предпринимаемых игроками
 */
public class CallablePersonalizedBFS<T>
        extends BreadthFirstSearch<T>
        implements Callable {

    private final IBasicState<T> gameState;
    private final Point start;
    private final Point goal;
    private final int playerId;

    /**
     * Создаёт новый экземпляр алгоритма.
     *
     * @param gameState состояние игры
     * @param start исходное расположение
     * @param goal целевая координата
     * @param playerId идентификатор игрока
     */
    public CallablePersonalizedBFS(IBasicState<T> gameState, Point start, Point goal, int playerId) {
        this.gameState = gameState;
        this.start = start;
        this.goal = goal;
        this.playerId = playerId;
    }

    @Override
    public T call() throws Exception {
        Queue<T> actionsList = getSolution(gameState, start, goal);
        if (actionsList.isEmpty()) {
            return null;
        }
        return actionsList.peek();
    }

    /**
     * Перечень доступных действий. В отличие от метода базового класса,
     * учитывает тип игрока, для которого запрашивается перечень действий.
     *
     * @param state состояние игры
     * @param place расположение
     * @return перечень доступных действий
     */
    @Override
    protected LinkedHashMap<Point, T> getLegalActionsAsMap(IBasicState<T> state, Point place) {
        return state.getLegalActionsAsMapNoKins(place, playerId);
    }
}
