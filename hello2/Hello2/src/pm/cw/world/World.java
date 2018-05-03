package pm.cw.world;

import java.util.ArrayList;

import pm.cw.cell.Cell;
import pm.cw.cell.Creature;

public class World
{
    public int cellMax;
    public int width;
    public int height;
    
    public GridSquare[][] grid;
    public Cell[] cells;
    public CellsAtSquare[][] cellLocation;
    public ArrayList<Creature>  creatures;
    
}
