package enc.filter;
 
import java.util.Random;
 
 
public class LazyEncFilter extends ByteMutatorFilter
{
 
    byte encSource = (byte)43;
    Random rand = new Random(1043);
    
    public LazyEncFilter(Filter inputFilter)
    {
        super(inputFilter);
    }
    
 
    
    protected byte changeByte(byte in)
    {
        byte next = (byte)rand.nextInt();
        return (byte)((byte)in ^ (byte)encSource ^ (byte)next);
    }
    
}
 