import java.lang.NullPointerException;

public class DLL
{
	private Stack tail;
	public DLL()
	{
		tail=null;
	}

	public DLL(Stack in)
	{
		tail=in;
	}

	public DLL(Node in)
	{
		Stack here=new Stack(in);
		tail=here;
	}

	public void addToStack(Stack in)
	{
		if(tail==null){
			tail=in;
		}else{
			in.setPrevious(tail);
			tail.setNext(in); tail=in;
		}
	}

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

	public Stack getHead()
	{
		Stack spot=tail;
		while(spot.getPrevious()!=null){
			spot=spot.getPrevious();
		}
		return spot;
	}

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