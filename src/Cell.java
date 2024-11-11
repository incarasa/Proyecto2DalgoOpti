import java.util.List;

public class Cell {
    private int cellId;
    private int cellXPos;
    private int cellYPos;
    private int cellType;
    private List<String> peptidos;
    
    public Cell(int cellId, int cellXPos, int cellYPos, int cellType, List<String> peptidos) {
        this.cellId = cellId;
        this.cellXPos = cellXPos;
        this.cellYPos = cellYPos;
        this.cellType = cellType;
        this.peptidos = peptidos;
    }

    public int getCellId() {
        return cellId;
    }

    public int getCellXPos() {
        return cellXPos;
    }

    public int getCellYPos() {
        return cellYPos;
    }

    public int getCellType() {
        return cellType;
    }

    public List<String> getPeptidos() {
        return peptidos;
    }
    
    @Override
    public String toString() {
        return "Cell [cellId=" + cellId + ", cellXPos=" + cellXPos + ", cellYPos=" + cellYPos + ", cellType=" + cellType
                + "]";
    }

}
