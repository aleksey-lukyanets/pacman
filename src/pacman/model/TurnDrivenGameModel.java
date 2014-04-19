package pacman.model;

import algorithms.core.ISearchAlgorithm;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import pacman.game.IAction;

/**
 * Модель игры по турам.
 * <p>
 * Игра ведётся по турам, в каждом из которых игроки делают по одному ходу.
 * Пакман делает первый ход в туре, получая действие из очереди действий
 * {@link pacmanActionsQueue}; если очередь действий пуста, то об этом
 * уведомляется контроллер игры, который должен предоставить новые действия.
 * После запуска действия Пакмана (и во время его анимации) рассчитываются и
 * запускаются действия привидений. Новый тур игры начинается, когда все игроки
 * выполнили свои действия.
 * <p>
 * Реализация:<ol>
 * <li> Инициализировать список игроков в {@link initializePlayers};
 * <li> Возвращать идентификатор Пакмана в {@link getPacmanId};
 * <li> Изменять статус хода игрока через {@link setPlayerStatusMoving} и
 * {@link setPlayerStatusDone};
 * <li> Расчитывать последовательность действий Пакмана через заданный алгоритм
 * в {@link getPacmanActionsSequence};
 * <li> Исполнять действия игроков в {@link startPlayerAction};
 * <li> Рассчитывать и запускать действия привидений в {@link letGhostsAct};
 * <li> При необходимости выполнять действия по завершении тура в {@link performTurnFinished}.</ol>
 */ 
public abstract class TurnDrivenGameModel
        extends AbstractGameModel
        implements Runnable {
    
    // Статус хода игрока
    private final Map<Integer, PlayerStatus> playerActionStatus = new ConcurrentHashMap<Integer, PlayerStatus>();
    
    // Очередь действий Пакмана
    private final BlockingQueue<IAction> pacmanActionsQueue = new LinkedBlockingQueue<IAction>();
    private final List<Integer> playersId = new ArrayList<Integer>();
    private TurnStatus turnStatus = TurnStatus.PACMAN_ACTION;
    
    /**
     * Создаёт новую модель игры.
     *
     * @param gameField карта игрового поля
     */
    public TurnDrivenGameModel(GameFieldMap gameField) {
        super(gameField);
    }

    /**
     * Статус исполнения хода игроком.
     */
    private enum PlayerStatus {WAITING, MOVING, DONE};
    
    /**
     * Фаза выполнения тура игры.
     */
    private enum TurnStatus {NOBODY_ACTED, PACMAN_ACTION, EVERYBODY_ACTED};
    
    //---------------------------------------------------- Методы инициализации
    
    @Override
    public int reinitializePlayers() {
        players.clear();
        initializePlayers();
        initializeTurnData();
        return getPacmanId();
    }
    
    /**
     * Повторно инициализирует специфические для этого класса данные.
     */
    private void initializeTurnData() {
        playersId.clear();
        for (int i = 0; i < (totalGhosts + 1); i++) {
            playersId.add(i);
        }
        playerActionStatus.clear();
        pacmanActionsQueue.clear();
        turnStatus = TurnStatus.NOBODY_ACTED;
        clearActionsComplete();
    }

    /**
     * Инициализация списка игроков.
     */
    protected abstract void initializePlayers();

    /**
     * Возвращает идентификатор игрока-Пакмана.
     * 
     * @return индекс игрока-Пакмана в списке <code>players</code>
     */
    protected abstract int getPacmanId();

    //---------------------------------------- Управление передвижением игроков
    
    @Override
    public void performPacmanAction(ISearchAlgorithm algorithm, Point goal) {
        synchronized (pacmanActionsQueue) {
            if (pacmanActionsQueue.isEmpty()) {
                Queue<IAction> sequence = getPacmanActionsSequence(algorithm, goal);
                pacmanActionsQueue.addAll(sequence);
            }
        }
    }
    
    /**
     * Устанавливает состояние игрока в "передвигается".
     * <p>
     * Потоково-безопасен. Должен вызываться последним действием метода.
     * 
     * @param playerId идентификатор игрока
     */
    protected void setPlayerStatusMoving(int playerId) {
        setPlayerActionStatus(playerId, PlayerStatus.MOVING);
    }
    
    /**
     * Устанавливает состояние игрока в "ход сделан".
     * <p>
     * Потоково-безопасен. Должен вызываться последним действием метода.
     * 
     * @param playerId идентификатор игрока
     */
    protected void setPlayerStatusDone(int playerId) {
        setPlayerActionStatus(playerId, PlayerStatus.DONE);
    }
    
    /**
     * Устанавливает статус исполнения хода игроком.
     * @param playerId идентификатор игрока
     * @param newStatus статус исполнения хода игроком
     */
    private void setPlayerActionStatus(int playerId, PlayerStatus newStatus) {
        synchronized (playerActionStatus) {
            playerActionStatus.put(playerId, newStatus);
            playerActionStatus.notify();
        }
    }
    
    @Override
    public final void run() {//<editor-fold defaultstate="collapsed">
        while (true) {
            /*
             * Обработка фаз тура игры
             */
            switch (turnStatus) {
                
                // Начало тура: ни один игрок не начинал передвижение
                case NOBODY_ACTED:
                {
                    synchronized (playerActionStatus) {
                        // Ожидать, пока все игроки будут переведены в режим ожидания
                        while (!isEverybodyWaiting()) {
                            try {
                                playerActionStatus.wait();
                            } catch (InterruptedException ie) {
                            }
                        }
                    }
                    turnStatus = TurnStatus.PACMAN_ACTION;
                }
                break;
                
                // Запуск хода Пакмана с последующими ходами привидений
                case PACMAN_ACTION:
                {
                    if (pacmanActionsQueue.isEmpty()) {
                        setPacmanActionsDone();                     // Запросить новые ходы Пакмана
                    }
                    try {
                        IAction action = pacmanActionsQueue.take();
                        startPlayerAction(getPacmanId(), action); // Запуск хода Пакмана
                    } catch (InterruptedException ie) {
                    }
                    letGhostsAct();                                             // Расчёт и запуск ходов привидений
                    turnStatus = TurnStatus.EVERYBODY_ACTED;
                }
                break;
                    
                // Завершение тура: все игроки исполнили своих ходы
                case EVERYBODY_ACTED:
                {
                    // Ожидать, пока все игроки завершат ходы
                    synchronized (playerActionStatus) {
                        while (!isEverybodyActingDone()) {
                            try {
                                playerActionStatus.wait();
                            } catch (InterruptedException ie) {
                            }
                        }
                    }
                    performTurnFinished();                                      // Завершить тур
                    reportChanged();                                            // Уведомить об изменении данных модели
                    clearActionsComplete();                                     // Перевести игроков в режим ожидания
                    turnStatus = TurnStatus.NOBODY_ACTED;
                }
                break;
            }
            
            // Если игра завершена победой или поражением,
            // то завершить выполнение
            if (isGameComplete()) {
                break;
            }
        }
        //</editor-fold>
    }

    private boolean isEverybodyActingDone() {
        synchronized (playerActionStatus) {
            for (int id : playersId) {
                if (playerActionStatus.get(id) != PlayerStatus.DONE) {
                    return false;
                }
            }
            return true;
        }
    }

    private boolean isEverybodyWaiting() {
        synchronized (playerActionStatus) {
            for (int id : playersId) {
                if (playerActionStatus.get(id) != PlayerStatus.WAITING) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Очистить все флаги завершения ходов игроками в текущем туре игры.
     */
    private void clearActionsComplete() {
        synchronized (playerActionStatus) {
            for (int playerId : playersId) {
                setPlayerActionStatus(playerId, PlayerStatus.WAITING);
            }
        }
    }
    
    //----------------------------------------- Реализация передвижения игроков
    
    /**
     * Рассчитывает и возвращает последовательность ходов Пакмана.
     * 
     * @param algorithm алгоритм для расчёта действия
     * @param goal координата целевого расположения на игровом поле
     * @return последовательность ходов Пакмана
     */
    protected abstract Queue<IAction> getPacmanActionsSequence(ISearchAlgorithm algorithm, Point goal);
    
    /**
     * Запускает выполнение хода игроком.
     * 
     * После успешного запуска выполнения хода статус хода игрока должен быть
     * установлен в значение {@link PlayerStatus.MOVING} функцией
     * {@link setPlayerActionStatus}. В остальных случаях должен быть установлен
     * статус хода {@link PlayerStatus.DONE}.
     * 
     * @param playerId идентификатор игрока
     * @param action ход игрока
     */
    protected abstract void startPlayerAction(int playerId, IAction action);

    /**
     * Вычисление и запуск ходов привидений.
     * <p>
     * Каждое привидение выполняет строго один ход.
     * <p>
     * Инструкции по установке статуса хода см. {@link startPlayerAction}.
     */
    protected abstract void letGhostsAct();

    /**
     * Завершение тура игры.
     */
    protected abstract void performTurnFinished();
}
