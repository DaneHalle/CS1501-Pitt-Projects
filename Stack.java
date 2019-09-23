public class Stack
{
	private Node value;
	private Stack next;
	private Stack previous; 
	public Stack(Node in)
	{
		value=in;
		next=null;
		previous=null;
	}

	public Stack(Node inVal, Stack inNext, Stack inPrev)
	{
		value=inVal;
		next=inNext;
		previous=inPrev;
	}

	public Node getInfo()
	{
		return value;
	}

	public Stack getNext()
	{
		return next;
	}

	public Stack getPrevious()
	{
		return previous;
	}

	public void setNext(Stack inNext)
	{
		next=inNext;
	}

	public void setPrevious(Stack inPrev)
	{
		previous=inPrev;
	}  

	public void removeNext()
	{
		next=null;
	}

	public void removePrevious()
	{
		previous=null; 
	}
}