import java.lang.Character; import java.lang.NullPointerException;
public class DLB 
{
    private Node first;

    /** 
     *  Initializes values
     */  
    public DLB()
    {
        first=null;
    }

    /** 
     *  returns first
     */  
    public Node getFirst()
    {
        return first;
    }

    /** 
     *  Using logic, puts all values of string where needed in the DLB trie
     */  
    public void addTo(String in)
    {
        char[] breakdown=in.toCharArray();
        Node n=new Node(breakdown[0]);
        boolean childNull=false; int j=0;
        if(first==null){
            first=n; j++; childNull=true;
        }
        Node spot=first;
        for(int i=j; i<breakdown.length; i++){
            n=new Node(breakdown[i]);
            if(!childNull){
                if(breakdown[i]!=spot.getInfo()){
                    while(breakdown[i]!=spot.getInfo()){
                        if(spot.getSibling()==null){
                            spot.setSib(n); childNull=true;
                            spot=spot.getSibling();
                        }else{
                            spot=spot.getSibling();
                        }
                    }
                }
                if(breakdown[i]==spot.getInfo() && spot.getChild()!=null){
                    spot=spot.getChild();
                }else if(breakdown[i]==spot.getInfo() && spot.getChild()==null){
                    childNull=true;
                }
            }else{
                spot.setChild(n); spot=spot.getChild();
            }
        }
        Node term=new Node('^');
        if(spot.getChild()==null && spot.getInfo()!='^'){ 
            spot.setChild(term);
            spot=spot.getChild();
        }else{
            while(spot.getSibling()!=null && spot.getInfo()!='^'){
                spot=spot.getSibling();
            }
            if(spot.getInfo()!='^'){
                spot.setSib(term);
                spot=spot.getSibling();
            }
        }
        spot.addFreq();
    }

    /** 
     *  Sets up the stack for user predictions 
     */ 
    public String[] getSmartPrefix(String key)
    {
        try{
            Node spot=first;
            char[] breakdown=key.toCharArray();
            for(int i=0; i<breakdown.length; i++){
                while(spot!=null && breakdown[i]!=spot.getInfo()){
                    spot=spot.getSibling();
                }
                if(breakdown[i]==spot.getInfo()){
                    spot=spot.getChild();
                }else{break;}
            }
            Node temp=spot;
            DLL predict=new DLL(temp);
            while(temp.getChild()!=null){
                temp=temp.getChild(); predict.addToStack(temp);
            }
            String[] predictions=predict.makeSmartPredictions(key);
            return predictions;
        }catch(NullPointerException exception){
            return null;
        }
    }

    /** 
     *  Returns String[] of predictions using stack
     */  
    public String[] getFromPrefix(String key)
    {
        try{
            Node spot=first;
            char[] breakdown=key.toCharArray();
            for(int i=0; i<breakdown.length; i++){
                while(spot!=null && breakdown[i]!=spot.getInfo()){
                    spot=spot.getSibling();
                }
                if(breakdown[i]==spot.getInfo()){
                    spot=spot.getChild();
                }else{break;}
            }
            Node temp=spot;
            DLL predict=new DLL(temp);
            while(temp.getChild()!=null){
                temp=temp.getChild(); predict.addToStack(temp);
            }
            String[] predictions=predict.makePredictions(key);
            return predictions;
        }catch(NullPointerException exception){
            return null;
        }
    }
}