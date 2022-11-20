package algorithms.core;

import java.awt.Point;
import java.util.Queue;

/**
 * Интерфейс поискового алгоритма.
 * 
 * @param <T> тип действий, предпринимаемых игроками
 * @param <S> тип состояний, которыми манипулирует алгоритм
 */
public interface ISearchAlgorithm<T, S extends IBasicState<T>> {
    
    /**
     * Возвращает последовательность действий от старта к цели; порядок
     * перечисления действий - в порядке приближения к цели.
     * 
     * @param gameState состояние игры
     * @param start исходное расположение
     * @param goal целевая координата
     * @param playerId идентификатор игрока, счёт от нуля
     * @return последовательность действий
     */
    public Queue<T> getSolution(S gameState, Point start, Point goal, int playerId);
}
