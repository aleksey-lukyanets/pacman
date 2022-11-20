package algorithms.core;

import java.awt.Point;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

/**
 * Неинформированный поиск в ширину.
 * <p>
 * Реализация классического алгоритма неинформированного поиска в ширину.
 * <p>
 * Получить решение через {@link getSolution}. После возврата решения доступен
 * перечень вершин открытых в процессе поиска: {@link getLastClosedNodes}.
 * Функция {@link getLegalActions} может быть переопределена.
 * <p>
 * Один экземпляр класса может использоваться для поиска многократно.
 *
 * @param <T> тип действий, предпринимаемых игроками
 */
public class BreadthFirstSearch<T> implements ISearchAlgorithm<T, IBasicState<T>> {

    private final Set<Point> lastClosedNodes = new HashSet<Point>();

    /**
     * Создаёт новый экземпляр алгоритма поиска в ширину.
     */
    public BreadthFirstSearch() {
    }

    @Override
    public Queue<T> getSolution(IBasicState<T> gameState, Point start, Point goal, int playerId) {
        return getSolution(gameState, start, goal);
    }

    /**
     * Возвращает перечень вершин графа открытых в процессе последнего решения.
     * 
     * @return перечень открытых вершин графа
     */
    public final Set<Point> getLastClosedNodes() {
        return lastClosedNodes;
    }

    /**
     * Получить решение.
     * <p>
     * Возвращает последовательность действий, ведущих через проходимые клетки
     * игрового поля от исходных координат <code>start</code> к координатам цели
     * <code>goal</code>.
     * <p>
     * Если проходимый путь от исходной точки до цели найти невозможно, то
     * возвращает пустую очередь. Также возвращает пустую очередь, если
     * координаты цели и исходной точки совпадают.
     *
     * @param gameState состояние игры
     * @param start исходное расположение
     * @param goal целевая координата
     * @return последовательность действий
     */
    public final Queue<T> getSolution(IBasicState<T> gameState, Point start, Point goal) {
        // Решение - список действий
        LinkedList<T> solution = new LinkedList<T>();

        // Упорядоченный набор вершин графа, ожидающих обхода. Добавленные раньше извлекаются первыми.
        // Набор: содержит только уникальные значения, повторное добавление не изменяет положение значения.
        LinkedHashSet<Point> nodesToOpen = new LinkedHashSet<Point>();
        
        // Набор пройденных вершин, только уникальные значения
        Set<Point> passedNodes = new HashSet<Point>();           
        
        // Фрагменты пути: пары [текущая координата]=[действие плюс координата в результате действия]
        Map<Point, SimpleImmutableEntry<Point, T>> path = new HashMap<Point, SimpleImmutableEntry<Point, T>>();
        
        // Если координаты цели и исходной точки совпадают
        if (start.equals(goal)) {
            return solution;
        }

        // Поиск выполняется в два этапа: первый - поиск цели, второй - составление
        // пути к цели из отдельных шагов
        // Исходный состав набора - начальная вершина
        nodesToOpen.add(start);

        // Поочерёдный перебор вершин графа, пока очередная вершина не окажется целью
        while (true) {
            // Если проходимый путь от исходной точки до цели найти невозможно
            if (nodesToOpen.isEmpty()) {
                return solution;
            }

            Point currentNode = nodesToOpen.iterator().next();          // Извлечь очередную вершину

            // Если текущая вершина является целью - завершить поиск
            if (goal.equals(currentNode)) {
                break;
            }

            nodesToOpen.remove(currentNode);                            // Удалить текущую вершину из плана обхода
            passedNodes.add(currentNode);                               // Отметить текущую вершину как пройденную

            // Получить упорядоченный перечень возможных ходов из текущей вершины.
            // Порядок итерации по ходам - важен.
            LinkedHashMap<Point, T> nodeSuccessors = getLegalActionsAsMap(gameState, currentNode);
            Iterator<Entry<Point, T>> it = nodeSuccessors.entrySet().iterator();
            while (it.hasNext()) {
                Entry<Point, T> entry = it.next();
                Point nextNode = entry.getKey();
                if ((!passedNodes.contains(nextNode)) && (!nodesToOpen.contains(nextNode))) {
                    nodesToOpen.add(nextNode);
                    path.put(nextNode, new SimpleImmutableEntry(currentNode, nodeSuccessors.get(nextNode)));
                }
            }
        }

        lastClosedNodes.clear();
        lastClosedNodes.addAll(passedNodes);

        Point node = goal;

        // Составление пути к цели, выполняется через последовательное добавление
        // пройденных шагов в обратном порядке: от цели к исходной позиции
        while (!node.equals(start)) {
            SimpleImmutableEntry<Point, T> pair = path.get(node);
            solution.add(pair.getValue());
            node = pair.getKey();
        }

        Collections.reverse(solution);                                  // Сортировка действий в прямом порядке: от начала к цели

        return solution;
    }

    /**
     * Перечень доступных действий.
     * <p>
     * Возвращает перечень действий, доступных из заданного расположения
     * <code>place</code>, в виде ассоциативного массива пар "расположение в
     * результате действия = действие".
     *
     * @param state состояние игры
     * @param place расположение
     * @return перечень доступных действий
     */
    protected LinkedHashMap<Point, T> getLegalActionsAsMap(IBasicState<T> state, Point place) {
        return state.getLegalActionsAsMap(place);
    }
}
