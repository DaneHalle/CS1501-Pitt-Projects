public class Stack
{
	private Node value; private Stack next; private Stack previous;

    /** 
     *  Initializes values
     */  
	public Stack(Node in)
	{
		value=in; next=null; previous=null;
	}

    /** 
     *  Initializes values
     */  
	public Stack(Node inVal, Stack inNext, Stack inPrev)
	{
		value=inVal; next=inNext; previous=inPrev;
	}

    /** 
     *  returns value
     */  
	public Node getInfo()
	{
		return value;
	}

    /** 
     *  returns next
     */  
	public Stack getNext()
	{
		return next;
	}

    /** 
     *  returns previous
     */  
	public Stack getPrevious()
	{
		return previous;
	}

    /** 
     *  Sets next to value
     */  
	public void setNext(Stack inNext)
	{
		next=inNext;
	}

    /** 
     *  Sets previous to value
     */  
	public void setPrevious(Stack inPrev)
	{
		previous=inPrev;
	}  

    /** 
     *  Removes next
     */  
	public void removeNext()
	{
		next=null;
	}

    /** 
     *  Removes previous
     */  
	public void removePrevious()
	{
		previous=null; 
	}
}