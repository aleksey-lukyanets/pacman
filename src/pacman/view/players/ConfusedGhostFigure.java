package pacman.view.players;

import java.awt.Color;
import pacman.game.IAnimatedPlayer;

/**
 * Класс фигуры контуженного привидения.
 */
public class ConfusedGhostFigure extends HorrificGhostFigure {

    public ConfusedGhostFigure(int cellSize) {
        super(cellSize);
    }
    
    @Override
    protected Color getColorId(IAnimatedPlayer myPlayer) {
        return getConfusedColor();
    }
}
