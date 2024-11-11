import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GridPartition {
    private double gridSize;
    private Map<String, List<Cell>> gridMap;

    public GridPartition(double gridSize) {
        this.gridSize = gridSize;
        this.gridMap = new HashMap<>();
    }

    //Método para obtener la clave de la cuadrícula basada en las coordenadas
    private String getGridKey(int x, int y) {
        int gridX = (int) Math.floor(x / gridSize);
        int gridY = (int) Math.floor(y / gridSize);
        return gridX + "," + gridY;
    }

    //Añadir una celula a la cuadrícula correspondiente
    public void addCell(Cell cell) {
        String key = getGridKey(cell.getCellXPos(), cell.getCellYPos());
        gridMap.computeIfAbsent(key, k -> new ArrayList<>()).add(cell);
    }

    //Obtener las células en cuadrículas adyacentes (incluyendo la misma)
    public List<Cell> getNearbyCells(Cell cell) {
        List<Cell> nearby = new ArrayList<>();
        int x = cell.getCellXPos();
        int y = cell.getCellYPos();
        int gridX = (int) Math.floor(x / gridSize);
        int gridY = (int) Math.floor(y / gridSize);

        for(int dx = -1; dx <= 1; dx++) {
            for(int dy = -1; dy <= 1; dy++) {
                String key = (gridX + dx) + "," + (gridY + dy);
                if(gridMap.containsKey(key)) {
                    nearby.addAll(gridMap.get(key));
                }
            }
        }
        return nearby;
    }
}

