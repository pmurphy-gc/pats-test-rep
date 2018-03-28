package pm.cw.world;

public class Time
{
    
    World world;
    
    public static void main(String[] args)
    {
        Time time = new Time();
        time.initWorld();
       // time.processRound();
    }
    
    public void initWorld()
    {
        world = new World();
        world.cellMax=9;
        world.width=9;
        world.height=9;
        world.grid= new GridSquare[world.height][world.width];
        world.cellLocation = new CellsAtSquare[world.height][world.width];
        for(int h=0; h<world.height; h++)
        {
            for(int w=0; w<world.width; w++)
            {
                world.grid[h][w] = new GridSquare();
                world.cellLocation[h][w] = new CellsAtSquare();
            }
            
        }
    }
}
