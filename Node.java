public class Node
{
    private char info; private Node sibling; private Node child;
    public Node(char infoIn)
    {
        info=infoIn; sibling=null; child=null;
    }

    public Node getSibling()
    {
    	return sibling;
    }
    
    public Node getChild()
    {
        return child;
    }
    
    public char getInfo()
    {
        return info;
    }
    
    public void setSib(Node inSib)
    {
        sibling=inSib;
    }

    public void setChild(Node inChild)
    {
    	child=inChild;
    }
}
