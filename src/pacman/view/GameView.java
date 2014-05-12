package pacman.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import javax.swing.JPanel;
import pacman.game.GameController;
import pacman.game.IAnimatedGameModel;
import pacman.game.IAnimatedPlayer;
import pacman.game.IAnimatedPlayer.PlayerType;
import pacman.game.IGameField;
import pacman.game.IGameField.CellContent;
import pacman.view.content.Food;
import pacman.view.content.IDrawableContent;
import pacman.view.content.Passage;
import pacman.view.content.Wall;
import pacman.view.players.AbstractPlayerFigure;
import pacman.view.players.ConfusedGhostFigure;
import pacman.view.players.HorrificGhostFigure;
import pacman.view.players.PacmanFigure;

/**
 * Класс графического представления игры.
 * <p>
 * Реализует графическое представление модели игры. Является наблюдателем по
 * отношению к модели игры.
 */
public class GameView extends JPanel implements Observer {

    private IAnimatedGameModel myModel;
    private int mazeCellSize;
    private final TreeMap<PlayerType, AbstractPlayerFigure> playersView = new TreeMap<PlayerType, AbstractPlayerFigure>();
    private final Map<CellContent, IDrawableContent> cellsView = new LinkedHashMap<CellContent, IDrawableContent>();
    private GameController myController;

    /**
     * Создаёт новое графическое представление игры.
     */
    public GameView() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                myController.handleMouseClick(e);
            }
        });
    }

    /**
     *
     * @param model модель игры
     */
    public void setModel(IAnimatedGameModel model) {
        myModel = model;
        mazeCellSize = myModel.getMazeCellSize();
        
        this.setMinimumSize(myModel.getGameCanvasDimension());
        this.setPreferredSize(myModel.getGameCanvasDimension());
        this.setMaximumSize(myModel.getGameCanvasDimension());
        
        playersView.put(PlayerType.PACMAN, new PacmanFigure(mazeCellSize));
        playersView.put(PlayerType.HORRIFIC_GHOST, new HorrificGhostFigure(mazeCellSize));
        playersView.put(PlayerType.CONFUSED_GHOST, new ConfusedGhostFigure(mazeCellSize));
        
        cellsView.put(CellContent.PASSAGE, new Passage(mazeCellSize / 8));
        cellsView.put(CellContent.WALL, new Wall(mazeCellSize / 8));
        cellsView.put(CellContent.FOOD, new Food(mazeCellSize / 5));
        cellsView.put(CellContent.PILLET, new Food(mazeCellSize / 2));
    }

    /**
     * Устанавливает контроллер игры.
     * 
     * @param controller контроллер игры
     */
    public void setController(GameController controller) {
        myController = controller;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (myModel.isRedrawRequired()) {
            repaint();
        }
        if (myModel.isPacmanQueueEmpty()) {
            myController.performPacmanQueueEmpty();
        }
    }
    
    //------------------------------------------------------- Функции рисования
    
    @Override
    public void paintComponent(Graphics g) {
        BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        super.paintComponent(g2d);
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setPaint(Color.GRAY);
        g2d.fillRect(0, 0, myModel.getGameCanvasDimension().width, myModel.getGameCanvasDimension().height);

        drawWalls(g2d);
        drawPlayers(g2d);

        if (myModel.isGameLost() == true) {
            drawGameComplete(g2d, "Вы проиграли");
        } else if (myModel.isGameWon() == true) {
            drawGameComplete(g2d, "Вы победили");
        }

        g2d.dispose();
        g.drawImage(bi, 0, 0, null);
    }

    /**
     * Рисование стен.
     */
    private void drawWalls(Graphics2D g2d) {
        IGameField maze = myModel.getMaze();
        // Цикл по каждой клетке
        for (Point cell : maze.getCells()) {
            for (CellContent content : maze.getCellContent(cell)) {
                cellsView.get(content).drawContent(g2d, myModel, cell, mazeCellSize);
            }
        }
    }

    /**
     * Рисование фигур игроков.
     *
     * @param g2d графический контекст
     */
    private void drawPlayers(Graphics2D g2d) {
        Iterator<PlayerType> it = playersView.navigableKeySet().iterator();
        while (it.hasNext()) {
            PlayerType currentType = it.next();
            for (IAnimatedPlayer player : myModel.getPlayers()) {
                if (player.getType() == currentType) {
                    playersView.get(currentType).drawPlayer(g2d, player);
                }
            }
        }
    }

    /**
     * Рисование экрана, информирующего о победе или поражении.
     */
    private void drawGameComplete(Graphics2D g2d, String text) {
        Dimension gameFrameSize = myModel.getGameCanvasDimension();

        g2d.setPaint(new Color(0, 0, 0, 130));
        g2d.fillRect(0, 0, gameFrameSize.width, gameFrameSize.height);

        FontRenderContext frc = g2d.getFontRenderContext();
        int width, height;
        Font f;

        f = new Font(Font.DIALOG, Font.BOLD, 38);
        g2d.setFont(f);
        width = (int) f.getStringBounds(text, frc).getWidth();
        height = (int) f.getStringBounds(text, frc).getHeight();
        g2d.setPaint(Color.WHITE);
        g2d.drawString(text, (gameFrameSize.width - width) / 2, (gameFrameSize.height - height + 40) / 2);

        text = "клинкните чтобы начать заново";
        f = new Font(Font.DIALOG, Font.PLAIN, 16);
        g2d.setFont(f);
        width = (int) f.getStringBounds(text, frc).getWidth();
        height = (int) f.getStringBounds(text, frc).getHeight();
        g2d.setPaint(Color.WHITE);
        g2d.drawString(text, (gameFrameSize.width - width) / 2, (gameFrameSize.height - height + 90) / 2);
    }
}
