package pacman.model;

import java.awt.Dimension;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import pacman.launch.GameLauncher;

/**
 * Карта игрового поля.
 */
public class DatabaseFieldMap extends FieldMap {
    
    public DatabaseFieldMap(Connection connection, int mapId) {
        try {
            int cellSize = getCellSizeFromDb(connection, mapId);
            Dimension fieldSize = getFieldSizeFromDb(connection, mapId);
            this.setCellSize(cellSize);
            this.setGameFieldSize(fieldSize);
            this.setGameCanvasSize(new Dimension(fieldSize.width * cellSize, fieldSize.height * cellSize));
            this.setStartLocations(getStartsFromDb(connection, mapId));
            this.setWallsLocations(getWallsFromDb(connection, mapId));
            this.setPilletsLocations(getPilletsFromDb(connection, mapId));
        } catch (Exception ex) {
            Logger.getLogger(GameLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private int getCellSizeFromDb(Connection connection, int mapId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT cell_size FROM \"public\".game_field_maps "
                        + "WHERE id = ?");
        preparedStatement.setInt(1, mapId);
        ResultSet result = preparedStatement.executeQuery();
        result.next();
        
        return result.getInt("cell_size");
    }

    private Dimension getFieldSizeFromDb(Connection connection, int mapId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT field_width, field_height FROM \"public\".game_field_maps "
                        + "WHERE id = ?");
        preparedStatement.setInt(1, mapId);
        ResultSet result = preparedStatement.executeQuery();
        result.next();
        
        return new Dimension(result.getInt("field_width"), result.getInt("field_height"));
    }

    private Point[] getStartsFromDb(Connection connection, int mapId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM \"public\".players_start "
                        + "WHERE map_id = ?");
        preparedStatement.setInt(1, mapId);
        ResultSet result = preparedStatement.executeQuery();
        
        List<Point> starts = new ArrayList<Point>();
        while (result.next()) {
            starts.add(result.getInt("player_index"), new Point(result.getInt("cell_x"), result.getInt("cell_y")));
        }
        
        return starts.toArray(new Point[starts.size()]);
    }

    private Point[] getWallsFromDb(Connection connection, int mapId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM \"public\".map_walls "
                        + "WHERE map_id = ?");
        preparedStatement.setInt(1, mapId);
        ResultSet result = preparedStatement.executeQuery();
        
        Set<Point> walls = new HashSet<Point>();
        while (result.next()) {
            walls.add(new Point(result.getInt("cell_x"), result.getInt("cell_y")));
        }
        
        return walls.toArray(new Point[walls.size()]);
    }

    private Point[] getPilletsFromDb(Connection connection, int mapId) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM \"public\".map_pillets "
                        + "WHERE map_id = ?");
        preparedStatement.setInt(1, mapId);
        ResultSet result = preparedStatement.executeQuery();
        
        Set<Point> walls = new HashSet<Point>();
        while (result.next()) {
            walls.add(new Point(result.getInt("cell_x"), result.getInt("cell_y")));
        }
        
        return walls.toArray(new Point[walls.size()]);
    }
}
