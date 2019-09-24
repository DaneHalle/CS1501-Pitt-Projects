import java.lang.NullPointerException;
public class DLL
{
	private Stack tail;

    /** 
     *  Initializes values
     */  
	public DLL()
	{
		tail=null;
	}

    /** 
     *  Initializes values
     */  
	public DLL(Stack in)
	{
		tail=in;
	}

    /** 
     *  Initializes values
     */  
	public DLL(Node in)
	{
		Stack here=new Stack(in);
		tail=here;
	}

    /** 
     *  returns tail
     */  
	public Stack getTail()
	{
		return tail;
	}

    /** 
     *  Adds value to the stack
     */  
	public void addToStack(Stack in)
	{
		if(tail==null){
			tail=in;
		}else{
			in.setPrevious(tail);
			tail.setNext(in); tail=in;
		}
	}

    /** 
     *  Adds value to the stack
     */  
	public void addToStack(Node in)
	{
		Stack insert=new Stack(in);
		if(tail==null){
			tail=insert;
		}else{
			insert.setPrevious(tail);
			tail.setNext(insert); tail=insert;
		}
	}

    /** 
     *  Removes top value from the stack
     */  
	public void removeFromStack()
	{
		if(tail!=null && tail.getPrevious()!=null){
			Stack newTail=tail.getPrevious();
			newTail.removeNext(); tail.removePrevious();
			tail=newTail;
		}else if(tail!=null && tail.getPrevious()==null){
			tail=null;
		}
	}

    /** 
     *  Checks to see if tail has sibling, if not, removes to previous node and repeats
     */  
	public boolean removeToNextSib()
	{	
		try{
			Node sibling=null;
			if(tail.getInfo().getSibling()!=null){
				sibling=tail.getInfo().getSibling();
				removeFromStack(); addToStack(sibling);
				while(sibling.getChild()!=null){
					addToStack(sibling.getChild());
					sibling=sibling.getChild();
				}
				if(tail.getPrevious()==null){
					return true;
				}
			}else{
				removeFromStack(); removeToNextSib();
			}
			return true;
		}catch(NullPointerException exception){
			return false;
		}
	}

    /** 
     *  returns head of stack
     */  
	public Stack getHead()
	{
		Stack spot=tail;
		while(spot.getPrevious()!=null){
			spot=spot.getPrevious();
		}
		return spot;
	}

    /** 
     *  Makes a string based on the stack
     */  
	public String makeString()
	{
		try{
			Stack head=getHead();
			String output="";
			while(head.getNext()!=null){
				if(head.getInfo().getInfo()!='^'){
					output+=head.getInfo().getInfo();
					head=head.getNext();
				}else{
					break;
				}
			}
			return output;
		}catch(NullPointerException exception){
			return null;
		}
	}

    /** 
     *  Fills String[] with predictions
     */  
	public String[] makePredictions(String prefix)
	{
		String endfix=""; boolean worked=true;
		String[] output=new String[5];
		for(int i=0; i<5; i++){
			endfix=makeString();
			if(endfix==null || !worked){
				output[i]=null;
				break;
			}else{
				output[i]=prefix+endfix;
				worked=removeToNextSib();
			}
		}
		return output;
	}
}