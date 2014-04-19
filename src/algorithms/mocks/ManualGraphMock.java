package algorithms.mocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Граф с ручной установкой топологии. Предназначен для тестирования алгоритмов.
 * <p>
 * Перечень вершин графа передаётся в конструктор, переходы между вершинами
 * добавляются методом {@link addAction}.
 *
 * @param <T> тип действий, предпринимаемых игроками
 * @param <N> тип вершин графа
 */
public class ManualGraphMock<T, N> {

    private final ArrayList<N> graphNodes;
    private final Map<N, LinkedHashMap<N, T>> successors = new HashMap<N, LinkedHashMap<N, T>>();

    /**
     * Создаёт новый граф.
     *
     * @param nodes перечень вершин графа
     */
    public ManualGraphMock(N[] nodes) {
        graphNodes = new ArrayList<N>(nodes.length);
        for (N node : nodes) {
            graphNodes.add(node);
            successors.put(node, new LinkedHashMap<N, T>());
        }
    }

    /**
     * Добавляет однонаправленный переход между вершинами графа, если исходная и
     * конечная вершины графа существуют.
     *
     * @param from исходная вершина
     * @param to конечная вершина
     * @param action действие перехода от исходной вершины к конечной
     */
    public void addAction(N from, N to, T action) {
        if ((successors.containsKey(from)) && (successors.containsKey(to))) {
            successors.get(from).put(to, action);
        }
    }
    
    /**
     * Возвращает перечень вершин графа, доступных из текущей вершины.
     * 
     * @param place текущая вершина
     * @return перечень вершин графа, доступных из текущей вершины
     */
    public LinkedHashMap<N, T> getLegalActions(N place) {
        return successors.get(place);
    }
    
    /**
     * Возвращает <code>true</code>, если вершина <code>node</code> существует.
     * 
     * @param node вершина графа
     * @return <code>true</code>, если вершина <code>node</code> существует
     */
    public boolean isNodeExist(N node) {
        return graphNodes.contains(node);
    }
}
