package pacman.view.players;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import pacman.game.IAnimatedPlayer;

/**
 * Класс фигуры привидения.
 */
public class HorrificGhostFigure extends AbstractPlayerFigure {

    protected final int eyeSize = toBiggerEven(playerSize / 3);             // Размер глаза, в пикселах
    protected final int betweenEyes = eyeSize / 5;            // Расстояние между краяни глаз, в пикселах
    protected final int eyeMiddleSize = toBiggerEven(eyeSize * 3 / 5);      // Размер зрачка, в пикселах
    protected final int skirtFoldSize = playerSize / 5;             // Размер складки юбки по оси X, в пикселах
    
    /**
     * Создаёт новую фигуру привидения.
     * 
     * @param cellSize размер клетки игрового поля
     */
    public HorrificGhostFigure(int cellSize) {
        super(cellSize);
    }
    
    @Override
    public void drawPlayer(Graphics2D g2d, IAnimatedPlayer myPlayer) {
        g2d.setPaint(getColorId(myPlayer));
        
        Point animationCoordinates = getMiddleCoordinate(myPlayer.getLocation());
        Point animationShift = myPlayer.getShiftVector();
        Point figureCenter = new Point(animationCoordinates);
        figureCenter.translate(animationShift.x + (cellSize-playerSize)/2, animationShift.y + (cellSize-playerSize)/2);
        
        Paint tempColor = g2d.getPaint();
        Composite tmpC = g2d.getComposite();

        g2d.fillArc(figureCenter.x,
                figureCenter.y, playerSize, playerSize, 0, 180);
        
        Area ar = new Area(new Rectangle2D.Float(figureCenter.x,
                figureCenter.y + playerSize / 2, playerSize, playerSize / 2));
        for (int i = 0; i < 5; i++) {
            if (i % 2 == 1) {
                Area ell = new Area(new Ellipse2D.Float(figureCenter.x + i * skirtFoldSize,
                        figureCenter.y + playerSize - skirtFoldSize / 2,
                        skirtFoldSize, skirtFoldSize));
                ar.subtract(ell);
            }
            else {
                Area ell = new Area(new Ellipse2D.Float(figureCenter.x + i * skirtFoldSize,
                        figureCenter.y + playerSize - skirtFoldSize / 2,
                        skirtFoldSize, skirtFoldSize));
                ar.add(ell);
            }
        }
        g2d.fill(ar);
        
        g2d.setPaint(Color.WHITE);
        g2d.fillOval(figureCenter.x + playerSize / 2 - eyeSize - betweenEyes,
                figureCenter.y + playerSize / 2 - eyeSize / 2,
                eyeSize, eyeSize);
        g2d.fillOval(figureCenter.x + playerSize / 2 + betweenEyes,
                figureCenter.y + playerSize / 2 - eyeSize / 2,
                eyeSize, eyeSize);
        
        g2d.setPaint(Color.BLACK);
        g2d.fillOval(figureCenter.x + playerSize / 2 - eyeSize - betweenEyes + (eyeSize - eyeMiddleSize) / 2,
                figureCenter.y + playerSize / 2 - eyeSize / 2 + (eyeSize - eyeMiddleSize) / 2,
                eyeMiddleSize, eyeMiddleSize);
        g2d.fillOval(figureCenter.x + playerSize / 2 + betweenEyes + (eyeSize - eyeMiddleSize) / 2,
                figureCenter.y + playerSize / 2 - eyeSize / 2 + (eyeSize - eyeMiddleSize) / 2,
                eyeMiddleSize, eyeMiddleSize);
        
        g2d.setComposite(tmpC);
        g2d.setPaint(tempColor);
    }
}
