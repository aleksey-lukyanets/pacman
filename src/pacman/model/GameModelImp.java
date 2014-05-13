package pacman.model;

import algorithms.core.CallablePersonalizedBFS;
import algorithms.core.ISearchAlgorithm;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import pacman.game.IAction;
import pacman.game.IAnimatedPlayer;

/**
 * Реализация модели игры по турам.
 * <p>
 * Реализует модель игры с выделением в отдельные потоки алгоритмических
 * расчётов и перерисовки графики. Доступ к изменению данных со стороны анимации
 * {@link ActionAnimator} и состояния игры {@link GameState} предоставляет
 * через соответствующих посредников.
 */
public class GameModelImp extends TurnDrivenGameModel {

    // Исполнитель расчёта ходов привидений. Один исполняемый поток для
    // последовательного расчёта хода каждого привидения
    private final ExecutorService computationThreadService;
    
    // Исполнитель анимации
    private final ExecutorService animationThreadService;
    
    // Посредник для получения изменений от состояния игры
    private final ModelDataMediator gameStateMediator = new ModelDataMediator();
    
    // Перечень привидений, бездействующих в текущем туре игры
    private final List<Integer> ghostsMissTurn;
    
    private final Map<Integer, ActionAnimator> animators = new HashMap<Integer, ActionAnimator>();
    private Player pacman;
    private int pacmanId;
    
    /**
     * Создаёт новую модель игры.
     *
     * @param gameField карта игрового поля
     */
    public GameModelImp(GameFieldMap gameField) {
        super(gameField);
        ghostsMissTurn = new ArrayList<Integer>();
        computationThreadService = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "ghost computation");
                return t;
            }});
        animationThreadService = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "player animator");
                return t;
            }});
    }

    //-------------------------------------------------- Вспомогательные классы
    
    /**
     * Посредник для взаимодействия с состоянием игры {@link GameState}.
     */
    private class ModelDataMediator implements IModelDataMediator {//<editor-fold defaultstate="collapsed">

        @Override
        public void setPlayerLocation(int playerId, Point newLocation) {
            players.get(playerId).setLocation(newLocation);
        }
        
        @Override
        public void refreshGhostsConfused(Map<Integer, Boolean> isGhostConfused) {
            for (int ghostId : isGhostConfused.keySet()) {
                players.get(ghostId).setConfused(isGhostConfused.get(ghostId));
            }
        }

        @Override
        public void setConfusedGhostEaten(int playerId) {
            ghostsMissTurn.add(playerId);
        }
        
        @Override
        public void refreshFood(Collection<Point> food) {
            myMaze.refreshFood(food);
        }
        
        @Override
        public void refreshPillets(Collection<Point> pillets) {
            myMaze.refreshPillets(pillets);
        }
        //</editor-fold>
    }
    
    /**
     * Посредник для взаимодействия с аниматором {@link ActionAnimator}.
     */
    private class AnimationMediator implements IAnimationMediator {//<editor-fold defaultstate="collapsed">
        
        private final int playerId;
        
        public AnimationMediator(int playerId) {
            this.playerId = playerId;
        }
        
        @Override
        public void notifyAnimationComplete() {
            setPlayerStatusDone(playerId);
        }
        
        @Override
        public void redrawGraphics() {
            redrawView();
        }
        //</editor-fold>
    }

    //---------------------------------- Реализация методов TurnDrivenGameModel
    
    @Override
    protected void initializePlayers() {
        animators.clear();
        pacman = new Player(IAnimatedPlayer.PlayerType.PACMAN, 0, gameFieldMap.getStartLocations()[0], getPossibleActions().get(0));
        addPlayer(pacman);
        pacmanId = players.lastIndexOf(pacman);
        for (int i = 0; i < totalGhosts; i++) {
            addPlayer(new Player(
                    IAnimatedPlayer.PlayerType.HORRIFIC_GHOST,
                    (i + 1),
                    gameFieldMap.getStartLocations()[i + 1],
                    getPossibleActions().get(0)));
        }
    }

    /**
     * Добавление игрока к игре.
     * Добавляется в список игроков, снабжается исполнителем анимации.
     * @param player игрок
     */
    private void addPlayer(Player player) {
        players.add(player);
        int playerId = players.indexOf(player);
        animators.put(playerId, new ActionAnimator(player, new AnimationMediator(playerId), getMazeCellSize()));
    }
    
    @Override
    protected int getPacmanId() {
        return pacmanId;
    }
    
    @Override
    protected Queue<IAction> getPacmanActionsSequence(ISearchAlgorithm algorithm, Point goal) {
        return algorithm.getSolution(gameState, pacman.getLocation(), goal, pacmanId);
    }
    
    @Override
    protected void startPlayerAction(int playerId, IAction action) {
        // Если действия не передано - завершить ход игрока
        if (action == null) {
            setPlayerStatusDone(playerId);
            return;
        }
        // Если ячейка непроходима - завершить ход игрока
        Point playerLocation = players.get(playerId).getLocation();
        if (!myMaze.isCellMovable(action.getLocationAfterAction(playerLocation))) {
            setPlayerStatusDone(playerId);
            return;
        }
        
        gameState.performPlayerAction(playerId, action, gameStateMediator);  // Исполнить действие

        ActionAnimator a = animators.get(playerId);
        a.setAction(action);
        animationThreadService.execute(a);                              // Запустить анимацию
    }

    @Override
    protected void letGhostsAct() {//<editor-fold defaultstate="collapsed">
        for (Player player : players) {
            int playerId = players.indexOf(player);
            
            // Для нормального привидения - охота на Пакмана
            if (player.getType() == IAnimatedPlayer.PlayerType.HORRIFIC_GHOST) {
                Future<IAction> result = computationThreadService.submit(new CallablePersonalizedBFS<IAction>(
                        gameState,
                        player.getLocation(),
                        pacman.getLocation(),
                        playerId));
                IAction action = null;
                try {
                    action = result.get();
                } catch (InterruptedException ex) {
                } catch (ExecutionException ex) {
                }
                if ((action != null)) {
                    startPlayerAction(playerId, action);
                }
                // Если ход игрока не был запущен успешно
                else {
                    setPlayerStatusDone(playerId);
                }
            }
            // Для контуженного привидения - выбор случайного действия из возможных
            else if (player.getType() == IAnimatedPlayer.PlayerType.CONFUSED_GHOST) {
                HashMap<Point, IAction> actions
                        = gameState.getLegalActionsAsMapNoKins(player.getLocation(), playerId);
                // Если привидение может передвигаться
                if (!actions.isEmpty() && !ghostsMissTurn.contains(playerId)) {
                    Collection<IAction> s = actions.values();
                    Random r = new Random();
                    IAction a = new ArrayList<IAction>(s).get(r.nextInt(s.size()));
                    startPlayerAction(playerId, a);
                }
                // Если ход игрока не был запущен успешно
                else {
                    setPlayerStatusDone(playerId);
                }
            }
        }
        //</editor-fold>
    }
    
    @Override
    protected void performTurnFinished() {
        if (!ghostsMissTurn.isEmpty()) {
            for (int playerId : ghostsMissTurn) {
                players.get(playerId).setLocation(gameFieldMap.getStartLocations()[playerId]);
            }
            ghostsMissTurn.clear();
        }
        gameState.performTurnFinished(gameStateMediator);
    }
}
