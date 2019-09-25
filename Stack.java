public class Stack
{
	private Node value; private Stack next; private Stack previous;
	private int freq;

    /** 
     *  Initializes values
     */  
	public Stack(Node in)
	{
		value=in; next=null; previous=null; freq=in.getFreq();
	}

    /** 
     *  Initializes values
     */  
	public Stack(Node inVal, Stack inNext, Stack inPrev)
	{
		value=inVal; next=inNext; previous=inPrev; freq=inVal.getFreq();
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
     *  returns frequency
     */ 
	public int getFreq()
	{
		return freq;
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