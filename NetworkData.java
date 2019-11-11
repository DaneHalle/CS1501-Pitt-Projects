import java.util.LinkedList;
public class NetworkData
{
	private int startPoint, endPoint, bandwidth, length;
	private double latency;
	private boolean copper;
    private static final String RESET="\u001B[0m";
	private static final String RED="\033[1;31m";
	private static final String PURPLE="\u001B[35m";
	private static final String CYAN="\u001B[36m";
	public NetworkData(String insert, boolean flip, int Max)
	{
		String[] toInsert=insert.split(" ");
		if(flip){
	        endPoint=Integer.parseInt(toInsert[0]);
	        startPoint=Integer.parseInt(toInsert[1]);
	    }else{
	        startPoint=Integer.parseInt(toInsert[0]);
	        endPoint=Integer.parseInt(toInsert[1]);
	    }
	    bandwidth=Integer.parseInt(toInsert[3]);
        length=Integer.parseInt(toInsert[4]);
        verify(Max);
	    if(toInsert[2].toLowerCase().equals("copper")){
        	copper=true;
			latency=Math.floor(((double)length/230000000)*Math.pow(10,11))/100;
        }else if(toInsert[2].toLowerCase().equals("optical")){
        	copper=false;
			latency=Math.floor(((double)length/200000000)*Math.pow(10,11))/100;
        }else{
        	throw new IllegalArgumentException(RED+"Wire type must be \"optical\" or \"copper\""+RESET);
        }
	}

	private void verify(int Max)
	{
		if(startPoint<0||endPoint<0||startPoint>=Max||endPoint>=Max){
			throw new IllegalArgumentException(RED+"Vertex index must be between 0 and "+Max+RESET);
		}
        if(bandwidth<=0){
        	throw new IllegalArgumentException(RED+"Bandwith must be a non-negative integer"+RESET);
        }
        if(length<=0){
        	throw new IllegalArgumentException(RED+"Length must be a non-negative integer"+RESET);
        }
	}

	public int getStart()
	{
		return startPoint;
	}

	public int getEnd()
	{
		return endPoint;
	}

	public int getBandwidth()
	{
		return bandwidth;
	}

	public int getLength()
	{
		return length;
	}

	public boolean isCopper()
	{
		return copper;
	}

	public double getLatency()
	{
		return latency;
	}

	public int getOther(int in) 
	{
		if(in==startPoint){
			return endPoint;
		}else if(in==endPoint){
			return startPoint;
		}else{
			throw new IllegalArgumentException(RED+"Invalid point"+RESET);
		}
    }

	public String toString() 
	{
		String type="Copper";
		if(!copper){
			type="Optical";
		}
		String out=""+PURPLE+"["+CYAN+startPoint+PURPLE+"] <--> ["+CYAN+endPoint+PURPLE+"] with ";
			out+=CYAN+type+PURPLE+" wire. Bandwidth of "+CYAN+bandwidth+PURPLE+" Mb/s."+RESET;
        return out;
    }
}

class NetworkList
{
	private int V, E;
    private LinkedList<NetworkData>[] adjList;
    private static final String RESET="\u001B[0m";
	private static final String RED="\033[1;31m";
    @SuppressWarnings("unchecked")
    public NetworkList(int in) 
    {
        V=in;	E=0;
        adjList=(LinkedList<NetworkData>[])(new LinkedList[V]);
        for(int v=0; v<V; v++){
            adjList[v]=new LinkedList<NetworkData>();
        }
    }

    public void addEdge(NetworkData edge) 
    {
        int v=edge.getStart();	int w=edge.getEnd();
        if (v<0||v>=V){
            throw new IllegalArgumentException(RED+"Vertex is not between 0 and "+(V-1)+RESET);
        }else if(w<0||w>=V){
        	throw new IllegalArgumentException(RED+"Vertex is not between 0 and "+(V-1)+RESET);
        }
        adjList[v].add(edge);	adjList[w].add(edge);
        E++;
    }

    public Iterable<NetworkData> getAtV(int v) 
    {
        if (v<0||v>=V){
	        throw new IllegalArgumentException(RED+"Vertex is not between 0 and "+(V-1)+RESET);
	    }
        return adjList[v];
    }

    public Iterable<NetworkData> getEdges() 
    {
        LinkedList<NetworkData> list=new LinkedList<NetworkData>();
        for(int v=0; v<V; v++){
            for(NetworkData e : getAtV(v)){
                if(e.getEnd()!=v){
                    list.add(e);
                }
            }
        }
        return list;
    }

    public int getV() 
    { 
    	return V; 
    }
    public int getE() 
    { 
    	return E; 
    }
}