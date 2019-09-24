public class Node
{
    private char info; private Node sibling; private Node child;

    /** 
     *  Initializes values
     */  
    public Node(char infoIn)
    {
        info=infoIn; sibling=null; child=null;
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
}
