package algorithms;

import algorithms.mocks.ManualGraphMock;
import algorithms.mocks.ExtendedGraphMock;
import algorithms.core.Minimax;
import algorithms.core.Minimax.PruningMode;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class MinimaxTest {
    
    public MinimaxTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        System.out.println("Minimax:");
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    private enum TestActions {LEFT, RIGHT, CENTER;}

    /**
     * Тест на поиск в небольшом дереве состояний, 2 игрока.
     * 
     * Количество игроков: 2
     * Глубина поиска: 3
     * Исходное состояние: root
     * Выигрышные состояния: [A, C]
     * Проигрышные состояния: [B, D]
     * 
     * Граф состояний:
     * 
     *         root
     *        /    \
     *  minLeft  minRight
     *   / \       /    \
     *  A   B     C   deeper
     *  4   3     2     |
     *                  D
     *                1000
     * 
     * Решение без отсечения: LEFT
     * Открытые вершины без отсечения: [root, minLeft, minRight, deeper, A, B, C, D]
     * 
     * Решение с отсечением: LEFT
     * Открытые вершины с отсечением: [root, minLeft, minRight, A, B, C]
     */
    @Test
    public void smallTree() {//<editor-fold defaultstate="collapsed">
        
        System.out.println("    - Тест на поиск в небольшом дереве состояний, 2 игрока");
        
        /*
        Создание тестового графа состояний
        */
        String root = "root";
        String minLeft = "minLeft";
        String minRight = "minRight";
        String deeper = "deeper";
        String A = "A";
        String B = "B";
        String C = "C";
        String D = "D";
        String[] nodes = new String[]{root, minLeft, minRight, deeper, A, B, C, D};
        
        ManualGraphMock<TestActions, String> graph = new ManualGraphMock<TestActions, String>(nodes);
        graph.addAction(root, minLeft, TestActions.LEFT);
        graph.addAction(root, minRight, TestActions.RIGHT);
        graph.addAction(minLeft, A, TestActions.LEFT);
        graph.addAction(minLeft, B, TestActions.RIGHT);
        graph.addAction(minRight, C, TestActions.LEFT);
        graph.addAction(minRight, deeper, TestActions.RIGHT);
        graph.addAction(deeper, D, TestActions.LEFT);
        
        ExtendedGraphMock<TestActions> mmGraph = new ExtendedGraphMock<TestActions>(graph, root, 2, 0);
        mmGraph.addNodeParams(root, false, false, 0);
        mmGraph.addNodeParams(minLeft, false, false, 0);
        mmGraph.addNodeParams(minRight, false, false, 0);
        mmGraph.addNodeParams(deeper, false, false, 0);
        mmGraph.addNodeParams(A, true, false, 4);
        mmGraph.addNodeParams(B, false, true, 3);
        mmGraph.addNodeParams(C, true, false, 2);
        mmGraph.addNodeParams(D, false, true, 1000);
        
        /*
        Тестирование минимакса без альфа-бета-отсечения.
        */
        ExtendedGraphMock.clearLastClosedNodes();
        Minimax<TestActions> mmNoPruning = new Minimax<TestActions>(3, PruningMode.OFF);
        
        assertEquals("Неверное действие.",
                TestActions.LEFT,
                mmNoPruning.getMaximizingAction(mmGraph, 0));
        
        Set<String> nodesNoPruning = new TreeSet<String>(Arrays.asList(
                new String[]{root, minLeft, minRight, deeper, A, B, C, D}));
        assertEquals("Неверный перечень открытых вершин графа.",
                nodesNoPruning,
                ExtendedGraphMock.getLastClosedNodes());
        
        /*
        Тестирование минимакса с включёным альфа-бета-отсечением.
        */
        ExtendedGraphMock.clearLastClosedNodes();
        Minimax<TestActions> mmPrunningOn = new Minimax<TestActions>(3, PruningMode.ON);
        
        assertEquals("Неверное действие.",
                TestActions.LEFT,
                mmPrunningOn.getMaximizingAction(mmGraph, 0));
        
        Set<String> nodesPruningOn = new TreeSet<String>(Arrays.asList(
                new String[]{root, minLeft, minRight, A, B, C}));
        assertEquals("Неверный перечень открытых вершин графа.",
                nodesPruningOn,
                ExtendedGraphMock.getLastClosedNodes());
        //</editor-fold>
    }
    
    /**
     * Тест на поиск с альфа-бета-отсечением, 2 игрока.
     * 
     * Количество игроков: 2
     * Глубина поиска: 2
     * Исходное состояние: max
     * Выигрышные состояния: [A, B, C, D, E, F, G, H, I]
     * Проигрышные состояния: нет
     * 
     * Граф состояний:
     * 
     *            max
     *       /-/   |   \--\
     *      /      |       \
     *     /       |        \
     *  min1      min2      min3
     *   /|\      /|\       /|\ 
     *  / | \    / | \     / | \
     * A  B  C  D  E  F   G  H  I  
     * 3 12  8  5  4  6  14  1  11
     * 
     * Решение без отсечения: CENTER
     * Открытые вершины без отсечения: [A, B, C, D, E, F, G, H, I, max, min1, min2, min3]
     * 
     * Решение с отсечением: CENTER
     * Открытые вершины с отсечением: [A, B, C, D, E, F, G, H, max, min1, min2, min3]
     */
    @Test
    public void alphaBetaPruning() {//<editor-fold defaultstate="collapsed">

        System.out.println("    - Тест на поиск с альфа-бета-отсечением, 2 игрока");
        
        /*
        Создание тестового графа состояний
        */
        String max = "max";
        String min1 = "min1";
        String min2 = "min2";
        String min3 = "min3";
        String A = "A";
        String B = "B";
        String C = "C";
        String D = "D";
        String E = "E";
        String F = "F";
        String G = "G";
        String H = "H";
        String I = "I";
        String[] nodes = new String[]{max, min1, min2, min3, A, B, C, D, E, F, G, H, I};
        
        ManualGraphMock<TestActions, String> graph = new ManualGraphMock<TestActions, String>(nodes);
        graph.addAction(max, min1, TestActions.LEFT);
        graph.addAction(max, min2, TestActions.CENTER);
        graph.addAction(max, min3, TestActions.RIGHT);
        graph.addAction(min1, A, TestActions.LEFT);
        graph.addAction(min1, B, TestActions.CENTER);
        graph.addAction(min1, C, TestActions.RIGHT);
        graph.addAction(min2, D, TestActions.LEFT);
        graph.addAction(min2, E, TestActions.CENTER);
        graph.addAction(min2, F, TestActions.RIGHT);
        graph.addAction(min3, G, TestActions.LEFT);
        graph.addAction(min3, H, TestActions.CENTER);
        graph.addAction(min3, I, TestActions.RIGHT);
        
        ExtendedGraphMock<TestActions> mmGraph = new ExtendedGraphMock<TestActions>(graph, max, 2, 0);
        mmGraph.addNodeParams(max, false, false, 0);
        mmGraph.addNodeParams(min1, false, false, 0);
        mmGraph.addNodeParams(min2, false, false, 0);
        mmGraph.addNodeParams(min3, false, false, 0);
        mmGraph.addNodeParams(A, true, false, 3);
        mmGraph.addNodeParams(B, true, false, 12);
        mmGraph.addNodeParams(C, true, false, 8);
        mmGraph.addNodeParams(D, true, false, 5);
        mmGraph.addNodeParams(E, true, false, 4);
        mmGraph.addNodeParams(F, true, false, 6);
        mmGraph.addNodeParams(G, true, false, 14);
        mmGraph.addNodeParams(H, true, false, 1);
        mmGraph.addNodeParams(I, true, false, 11);
        
        /*
        Тестирование минимакса без альфа-бета-отсечения.
        */
        ExtendedGraphMock.clearLastClosedNodes();
        Minimax<TestActions> mmNoPruning = new Minimax<TestActions>(2, PruningMode.OFF);
        
        assertEquals("Неверное действие.",
                TestActions.CENTER,
                mmNoPruning.getMaximizingAction(mmGraph, 0));
        
        Set<String> nodesNoPruning = new TreeSet<String>(Arrays.asList(
                new String[]{A, B, C, D, E, F, G, H, I, max, min1, min2, min3}));
        assertEquals("Неверный перечень открытых вершин графа.",
                nodesNoPruning,
                ExtendedGraphMock.getLastClosedNodes());
        
        /*
        Тестирование минимакса с включёным альфа-бета-отсечением.
        */
        ExtendedGraphMock.clearLastClosedNodes();
        Minimax<TestActions> mmPrunningOn = new Minimax<TestActions>(2, PruningMode.ON);
        
        assertEquals("Неверное действие.",
                TestActions.CENTER,
                mmPrunningOn.getMaximizingAction(mmGraph, 0));
        
        Set<String> nodesPruningOn = new TreeSet<String>(Arrays.asList(
                new String[]{A, B, C, D, E, F, G, H, max, min1, min2, min3}));
        assertEquals("Неверный перечень открытых вершин графа.",
                nodesPruningOn,
                ExtendedGraphMock.getLastClosedNodes());
        //</editor-fold>
    }
    
    /**
     * Тест на поиск в симметричном дереве, 3 игрока.
     * 
     * Количество игроков: 3 (max, min, min)
     * Глубина поиска: 3
     * Исходное состояние: a
     * Выигрышные состояния: [d1, d2, d3, d4, d5, d6, d7, d8]
     * Проигрышные состояния: нет
     * 
     * Граф состояний:
     * 
     *             /-----a------\
     *            /              \
     *           /                \
     *         b1                  b2
     *       /    \              /    \
     *    c1        c2        c3        c4
     *   /  \      /  \      /   \     /   \
     *  d1   d2  d3   d4    d5   d6   d7   d8
     *  3    9   10   6     4    7    0    5
     * 
     * Решение без отсечения: LEFT
     * Открытые вершины без отсечения: [a, b1, b2, c1, c2, c3, c4, d1, d2, d3, d4, d5, d6, d7, d8]
     * 
     * Решение с отсечением: LEFT
     * Открытые вершины с отсечением: [a, b1, b2, c1, c2, c3, c4, d1, d2, d3, d4, d5, d6, d7]
     */
    @Test
    public void twoGhosts3Level() {//<editor-fold defaultstate="collapsed">

        System.out.println("    - Тест на поиск в симметричном дереве, 3 игрока");
        
        /*
        Создание тестового графа состояний
        */
        String a = "a";
        String b1 = "b1";
        String b2 = "b2";
        String c1 = "c1";
        String c2 = "c2";
        String c3 = "c3";
        String c4 = "c4";
        String d1 = "d1";
        String d2 = "d2";
        String d3 = "d3";
        String d4 = "d4";
        String d5 = "d5";
        String d6 = "d6";
        String d7 = "d7";
        String d8 = "d8";
        String[] nodes = new String[]{a, b1, b2, c1, c2, c3, c4, d1, d2, d3, d4, d5, d6, d7, d8};
        
        ManualGraphMock<TestActions, String> graph = new ManualGraphMock<TestActions, String>(nodes);
        graph.addAction(a, b1, TestActions.LEFT);
        graph.addAction(a, b2, TestActions.RIGHT);
        graph.addAction(b1, c1, TestActions.LEFT);
        graph.addAction(b1, c2, TestActions.RIGHT);
        graph.addAction(b2, c3, TestActions.LEFT);
        graph.addAction(b2, c4, TestActions.RIGHT);
        graph.addAction(c1, d1, TestActions.LEFT);
        graph.addAction(c1, d2, TestActions.RIGHT);
        graph.addAction(c2, d3, TestActions.LEFT);
        graph.addAction(c2, d4, TestActions.RIGHT);
        graph.addAction(c3, d5, TestActions.LEFT);
        graph.addAction(c3, d6, TestActions.RIGHT);
        graph.addAction(c4, d7, TestActions.LEFT);
        graph.addAction(c4, d8, TestActions.RIGHT);
        
        ExtendedGraphMock<TestActions> mmGraph = new ExtendedGraphMock<TestActions>(graph, a, 3, 0);
        mmGraph.addNodeParams(a, false, false, 0);
        mmGraph.addNodeParams(b1, false, false, 0);
        mmGraph.addNodeParams(b2, false, false, 0);
        mmGraph.addNodeParams(c1, false, false, 0);
        mmGraph.addNodeParams(c2, false, false, 0);
        mmGraph.addNodeParams(c3, false, false, 0);
        mmGraph.addNodeParams(c4, false, false, 0);
        mmGraph.addNodeParams(d1, true, false, 3);
        mmGraph.addNodeParams(d2, true, false, 9);
        mmGraph.addNodeParams(d3, true, false, 10);
        mmGraph.addNodeParams(d4, true, false, 6);
        mmGraph.addNodeParams(d5, true, false, 4);
        mmGraph.addNodeParams(d6, true, false, 7);
        mmGraph.addNodeParams(d7, true, false, 0);
        mmGraph.addNodeParams(d8, true, false, 5);
        
        /*
        Тестирование минимакса без альфа-бета-отсечения.
        */
        ExtendedGraphMock.clearLastClosedNodes();
        Minimax<TestActions> mmNoPruning = new Minimax<TestActions>(3, PruningMode.OFF);
        
        assertEquals("Неверное действие.",
                TestActions.LEFT,
                mmNoPruning.getMaximizingAction(mmGraph, 0));
        
        Set<String> nodesNoPruning = new TreeSet<String>(Arrays.asList(
                new String[]{a, b1, b2, c1, c2, c3, c4, d1, d2, d3, d4, d5, d6, d7, d8}));
        assertEquals("Неверный перечень открытых вершин графа.",
                nodesNoPruning,
                ExtendedGraphMock.getLastClosedNodes());
        
        /*
        Тестирование минимакса с включёным альфа-бета-отсечением.
        */
        ExtendedGraphMock.clearLastClosedNodes();
        Minimax<TestActions> mmPrunningOn = new Minimax<TestActions>(3, PruningMode.ON);
        
        assertEquals("Неверное действие.",
                TestActions.LEFT,
                mmPrunningOn.getMaximizingAction(mmGraph, 0));
        
        Set<String> nodesPruningOn = new TreeSet<String>(Arrays.asList(
                new String[]{a, b1, b2, c1, c2, c3, c4, d1, d2, d3, d4, d5, d6, d7}));
        assertEquals("Неверный перечень открытых вершин графа.",
                nodesPruningOn,
                ExtendedGraphMock.getLastClosedNodes());
        //</editor-fold>
    }
    
    /**
     * Тест на поиск в асимметричном дереве, 2 игрока.
     * 
     * Количество игроков: 2
     * Глубина поиска: 3
     * Исходное состояние: a
     * Выигрышные состояния: [dx, d5, d6, d7, d8]
     * Проигрышные состояния: нет
     * 
     * Граф состояний:
     * 
     *      /-----a------\
     *     /              \
     *    /                \
     *   b1                  b2
     *    |                /    \
     *   cx             c3        c4
     *    |            /   \     /   \
     *   dx           d5   d6   d7   d8
     *  4.01          4    -7   0    5
     * 
     * Решение без отсечения: LEFT
     * Открытые вершины без отсечения: [a, b1, b2, c3, c4, cx, d5, d6, d7, d8, dx]
     * 
     * Решение с отсечением: LEFT
     * Открытые вершины с отсечением: [a, b1, b2, c3, cx, d5, d6, dx]
     */
    @Test
    public void asymmetricTree() {//<editor-fold defaultstate="collapsed">

        System.out.println("    - Тест на поиск в асимметричном дереве, 2 игрока");
        
        /*
        Создание тестового графа состояний
        */
        String a = "a";
        String b1 = "b1";
        String b2 = "b2";
        String cx = "cx";
        String c3 = "c3";
        String c4 = "c4";
        String dx = "dx";
        String d5 = "d5";
        String d6 = "d6";
        String d7 = "d7";
        String d8 = "d8";
        String[] nodes = new String[]{a, b1, b2, cx, c3, c4, dx, d5, d6, d7, d8};
        
        ManualGraphMock<TestActions, String> graph = new ManualGraphMock<TestActions, String>(nodes);
        graph.addAction(a, b1, TestActions.LEFT);
        graph.addAction(a, b2, TestActions.RIGHT);
        graph.addAction(b1, cx, TestActions.CENTER);
        graph.addAction(b2, c3, TestActions.LEFT);
        graph.addAction(b2, c4, TestActions.RIGHT);
        graph.addAction(cx, dx, TestActions.CENTER);
        graph.addAction(c3, d5, TestActions.LEFT);
        graph.addAction(c3, d6, TestActions.RIGHT);
        graph.addAction(c4, d7, TestActions.LEFT);
        graph.addAction(c4, d8, TestActions.RIGHT);
        
        ExtendedGraphMock<TestActions> mmGraph = new ExtendedGraphMock<TestActions>(graph, a, 2, 0);
        mmGraph.addNodeParams(a, false, false, 0);
        mmGraph.addNodeParams(b1, false, false, 0);
        mmGraph.addNodeParams(b2, false, false, 0);
        mmGraph.addNodeParams(cx, false, false, 0);
        mmGraph.addNodeParams(c3, false, false, 0);
        mmGraph.addNodeParams(c4, false, false, 0);
        mmGraph.addNodeParams(dx, true, false, 5);
        mmGraph.addNodeParams(d5, true, false, 4);
        mmGraph.addNodeParams(d6, true, false, -7);
        mmGraph.addNodeParams(d7, true, false, 0);
        mmGraph.addNodeParams(d8, true, false, 5);
        
        /*
        Тестирование минимакса без альфа-бета-отсечения.
        */
        ExtendedGraphMock.clearLastClosedNodes();
        Minimax<TestActions> mmNoPruning = new Minimax<TestActions>(3, PruningMode.OFF);
        
        assertEquals("Неверное действие.",
                TestActions.LEFT,
                mmNoPruning.getMaximizingAction(mmGraph, 0));
        
        Set<String> nodesNoPruning = new TreeSet<String>(Arrays.asList(
                new String[]{a, b1, b2, c3, c4, cx, d5, d6, d7, d8, dx}));
        assertEquals("Неверный перечень открытых вершин графа.",
                nodesNoPruning,
                ExtendedGraphMock.getLastClosedNodes());
        
        /*
        Тестирование минимакса с включёным альфа-бета-отсечением.
        */
        ExtendedGraphMock.clearLastClosedNodes();
        Minimax<TestActions> mmPrunningOn = new Minimax<TestActions>(3, PruningMode.ON);
        
        assertEquals("Неверное действие.",
                TestActions.LEFT,
                mmPrunningOn.getMaximizingAction(mmGraph, 0));
        
        Set<String> nodesPruningOn = new TreeSet<String>(Arrays.asList(
                new String[]{a, b1, b2, c3, cx, d5, d6, dx}));
        assertEquals("Неверный перечень открытых вершин графа.",
                nodesPruningOn,
                ExtendedGraphMock.getLastClosedNodes());
        //</editor-fold>
    }
}
