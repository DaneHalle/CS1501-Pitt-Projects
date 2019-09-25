import java.lang.NullPointerException;
import java.util.PriorityQueue;
import java.util.Comparator;
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
     *  returns frequency
     */ 
	public int getFreq()
	{
		return tail.getFreq();
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
				}else{break;}
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

    /** 
     *  The prediction for user that utilizes a PriorityQueue
     */ 
	public String[] makeSmartPredictions(String prefix)
	{
		String endfix=""; boolean worked=true; boolean pri=true;
		String[] output=new String[5]; Smart spot; Integer freq=0; 
		PriorityQueue<Smart> priority=new PriorityQueue<Smart>(6, new WordComparator());
		while(worked && pri && priority.size()<5 && tail!=null){
			if((Integer)tail.getFreq()==null){
				break;
			}
			freq=tail.getFreq(); endfix=makeString();
			if(endfix==null){
				break;
			}else{
				pri=priority.add(new Smart((prefix+endfix), freq));
				worked=removeToNextSib();
			}
		}

		Smart[] priorityPredict=priority.toArray(new Smart[5]);
		for(int i=0; i<priorityPredict.length && priorityPredict[i]!=null; i++){
			output[i]=priorityPredict[i].getWord();
		}
		return output;
	}
}

/** 
 *  A class to add a new compare method
 */ 
class WordComparator implements Comparator<Smart> 
{
	public int compare(Smart a, Smart b)
	{
		if (a.getFreq()<b.getFreq()){
            return 1;
        }else if (a.getFreq()>b.getFreq()){ 
            return -1;
        } 
        return 0; 
        
	}
}

/** 
 *  Used for predictions using a PriorityQueue
 */ 
class Smart 
{
	private String word; private int frequency;
	public Smart(String inWord, int freq)
	{
		word=inWord; frequency=freq;
	}

	public String getWord()
	{
		return word;
	}

	public int getFreq()
	{
		return frequency;
	}
}