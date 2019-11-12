import java.util.Scanner;   import java.util.Queue;     import java.util.LinkedList;
import java.io.FileReader;	import java.io.IOException; import java.io.FileNotFoundException;	
import java.lang.NumberFormatException;
public class NetworkAnalysis
{
	private static Scanner in=new Scanner(System.in);
	private static NetworkList list;   private static Dijkstra paths;
    private static final String RESET="\u001B[0m";      private static final String RED="\033[1;31m";
	private static final String GREEN="\u001B[32m";     private static final String YELLOW="\u001B[33m";
	private static final String PURPLE="\u001B[35m";    private static final String CYAN="\u001B[36m";
    private static final String GREEN_UNDER="\033[4;32m";
    private static final String GREEN_BOLD="\033[1;32m";
    private static final String YELLOW_UNDER="\033[4;33m";
	public static void main(String[] args)
	{
		list=null;    Prims lowestLatencyMST=null;
		if(args.length>0){
			readFile(args[0]);
			if(list==null){
				System.out.print(RED+"Error processing file. Please make sure name and ");
				System.out.println("format of file are correct."+RESET);
				return;
			}
            paths=new Dijkstra(list);	lowestLatencyMST=new Prims(list);
		}else{
			System.out.println(RED+"You need to give a file in order to work"+RESET);
			return;
		}
		System.out.print(GREEN_UNDER+GREEN+"Welcome! You have access to the following ");
			System.out.println("options:"+RESET);
		int response=0;
		while(true){
			System.out.println(GREEN+"\t1. Find lowest latency between two provided points");
			System.out.println("\t2. Return if the graph is copper-only connected");
			System.out.println("\t3. Find the minimum average latency spanning tree");
			System.out.print("\t4. Determine if the graph would remain connected should ");
				System.out.println("any two points fail");
			System.out.println("\t5. Exit the program"+RESET);
			System.out.print(YELLOW_UNDER+YELLOW+"What option would you like to do:"+RESET+"\t");
			try{
				response=Integer.parseInt(in.nextLine());
				switch(response){
					case 1: //Lowest latency;
	               		getLowestPath();
						break;
					case 2: //Copper-only connected
	              		isCopperConnected();
						break;
					case 3: //Minimum average latency spanning tree
	    				System.out.print(lowestLatencyMST);
						break;
					case 4: //Points fail and still remain connected
	                	checkTwoVerticies();
						break;
					case 5: //exit
						System.out.println(GREEN_BOLD+"Goodbye"+RESET);
						return; 
					default:
						System.out.println(RED+"Not a valid option. Please press 1-5 only."+RESET);
						break;
				}
			}catch(NumberFormatException exception){
				System.out.println(RED+"Not a valid option. Please press 1-5 only."+RESET);
			}
			System.out.print(GREEN_UNDER+GREEN+"\n\nType a number of the action you would ");
				System.out.println("like to do:"+RESET);
		}
	}

	private static void getLowestPath() 
    {
        try{
	    	System.out.print("\t"+GREEN_UNDER+GREEN+"Enter first vertex:"+RESET+"\t");
	        int start=Integer.parseInt(in.nextLine());
	        System.out.print("\t"+GREEN_UNDER+GREEN+"Enter second vertex:"+RESET+"\t");
            int end=Integer.parseInt(in.nextLine());
            if(start==end){
            	System.out.print(PURPLE+"There is no path between the same point. Bandwidth");
            		System.out.println(" available is "+CYAN+"0"+RESET);
            	return;
            }
	        if(start>=0&&end>=0&&start<list.getV()&&end<list.getV()){
	            if(paths.hasPath(start,end)){
	                double minBand=Double.POSITIVE_INFINITY;
	                for(NetworkData edge:paths.path(start,end)){
	                	if(minBand>edge.getBandwidth()){
	                		minBand=edge.getBandwidth();
	                	}
	                    System.out.println(edge);
	                }
	                System.out.print(PURPLE+"Bandwidth available across this ");
	                	System.out.println("path: "+CYAN+(int)minBand+RESET);
	            }else{
	                System.out.println(RED+"No path found between those verticies."+RESET);
	            }
	        }else{
	        	throw new NumberFormatException();
	        }
        }catch(NumberFormatException exception){
        	System.out.println(RED+"Entered verticies need to be between 0 and "+list.getV()+RESET);
        }
    }

    private static void isCopperConnected() 
    {
    	boolean[] visited=new boolean[list.getV()]; 
        Queue<Integer> queue=new LinkedList<Integer>();
        visited[0]=true;	queue.add(0);
        while(!queue.isEmpty()){
            for(NetworkData edge:list.getAtV(queue.remove())){
                if(!visited[edge.getEnd()]&&edge.isCopper()){
                    visited[edge.getEnd()]=true;	queue.add(edge.getEnd());
                }
            }
        }
        boolean isCopper=true;
        for(int i=0; i<visited.length; i++){
            if(!visited[i]){
                isCopper=false;
                break;
            }
        }
        if(isCopper){
            System.out.println(PURPLE+"This graph is copper-only connected."+RESET);
        }else{
            System.out.println(PURPLE+"This graph is not copper-only connected."+RESET);
        }
    }

	private static void checkTwoVerticies() 
	{
        if(list.getV()<=3){
            System.out.print(PURPLE+"There are only "+CYAN+list.getV()+PURPLE+" vertices. Removing any ");
            	System.out.println("two would cause the graph to be disconnected"+RESET);
            return;
        }
        boolean doesNotFail=true;
        for(int start=0; start<list.getV(); start++){
            for(int end=start+1; end<list.getV(); end++){
                if(!verify(start, end)){
                    doesNotFail=false;
                    System.out.print(PURPLE+"It will not be connected if vertex ["+CYAN+start+PURPLE+"] ");
                    	System.out.println("and ["+CYAN+end+PURPLE+"] are removed."+RESET);
                    break;
                }
            }
        }
        if(doesNotFail){
            System.out.println(PURPLE+"It does not fail!"+RESET);
        }
    }

    private static boolean verify(int startVertex, int endVetex) 
    {
        boolean[] visited=new boolean[list.getV()];
        Queue<Integer> queue=new LinkedList<Integer>();
        visited[startVertex]=true;	visited[endVetex]=true;
        int start=0;
        if(startVertex==0&&endVetex==1){
            start=3;
        }else if(startVertex==0){
            start=1;
        }
        visited[start]=true;	queue.add(start);
        while(!queue.isEmpty()){
            for(NetworkData edge:list.getAtV(queue.remove())){
                if(!visited[edge.getEnd()]){
                    visited[edge.getEnd()]=true;	queue.add(edge.getEnd());
                }
            }
        }
        boolean failed=true;
        for(int i=0; i<visited.length; i++){
            if(!visited[i]){
                failed=false;
                break;
            }
        }
        return failed;
    }

	private static void readFile(String f)
    {
        try {
            FileReader reader=new FileReader(f);
            Scanner in=new Scanner(reader);	String s;
            list=new NetworkList(Integer.parseInt(in.nextLine()));
            int max=list.getV();
            while(in.hasNextLine()){
                s=in.nextLine();
                NetworkData edge=new NetworkData(s, false, max);
                list.addEdge(edge);
                NetworkData otherEdge=new NetworkData(s, true, max);
                list.addEdge(otherEdge);
            }
        }catch(Exception exception){
        	System.err.println(exception);	list=null;
        }
    }
}