public class Node 
{
    private Node sibling; private Node child;
    private char info; private int frequency;
    
    /** 
     *  Initializes values
     */  
    public Node(char infoIn)
    {
        info=infoIn; sibling=null; child=null; frequency=0;
    }

    /** 
     *  returns sibling
     */  
    public Node getSibling()
    {
    	return sibling;
    }
    
    /** 
     *  returns child
     */  
    public Node getChild()
    {
        return child;
    }
    
    /** 
     *  returns Info
     */  
    public char getInfo()
    {
        return info;
    }

    /**
     *  returns frequency
     */ 
    public int getFreq()
    {
        return frequency;
    }
    
    /** 
     *  Sets sibling to value
     */  
    public void setSib(Node inSib)
    {
        sibling=inSib;
    }

    /** 
     *  Sets child to value
     */  
    public void setChild(Node inChild)
    {
    	child=inChild;
    }

    /**
     *  incriments frequency
     */ 
    public void addFreq()
    {
        frequency++;
    }
}