import java.util.Scanner; import java.lang.Math;
import java.io.FileReader; import java.io.IOException;
import java.io.PrintWriter; import java.io.FileNotFoundException;
import java.util.InputMismatchException; import java.io.FileWriter;
import java.io.BufferedWriter; import java.io.File;
public class ac_test
{
	public static void main(String[] args) throws IOException
	{
		Scanner in=new Scanner(System.in);
        DLB dictionary=new DLB(); DLB user=new DLB();
        int searchCount=0; double totalTime=0; long timeBefore=0;
		dictionary=readFile("dictionary.txt", dictionary);
        File user_history=new File("user_history.txt");
        user=readFile("user_history.txt", user);
        user_history.delete();
        String response=""; String word=""; int spot=0;
        String[] dictPredict=new String[5]; String[] userPredictions=new String[5];
        String[] finalPredictions=new String[5]; 
        while(true){
            response=getResponse();
            if(response.equals("!")){
                System.out.printf("Average time: %06f\nYeet!%n", (totalTime/searchCount));
                break;
            }else if(response.equals("$")){
                System.out.println("  WORD COMPLETED:\t"+word+"\n");
                user.addTo(word);  word="";
            }else if(response.equals("1") || response.equals("2") || response.equals("3") || 
                response.equals("4") || response.equals("5")){
                try{
                    switch(response){
                        case "1":
                            System.out.println("  WORD COMPLETED:\t"+finalPredictions[0]+"\n");
                            user.addTo(finalPredictions[0]);
                            finalPredictions=new String[5];
                            dictPredict=new String[5];
                            break;
                        case "2":
                            System.out.println("  WORD COMPLETED:\t"+finalPredictions[1]+"\n");
                            user.addTo(finalPredictions[1]);
                            finalPredictions=new String[5];
                            dictPredict=new String[5];
                            break;
                        case "3":
                            System.out.println("  WORD COMPLETED:\t"+finalPredictions[2]+"\n");
                            user.addTo(finalPredictions[2]);
                            finalPredictions=new String[5];
                            dictPredict=new String[5];
                            break;
                        case "4":
                            System.out.println("  WORD COMPLETED:\t"+finalPredictions[3]+"\n");
                            user.addTo(finalPredictions[3]);
                            finalPredictions=new String[5];
                            dictPredict=new String[5];
                            break;
                        case "5":
                            System.out.println("  WORD COMPLETED:\t"+finalPredictions[4]+"\n");
                            user.addTo(finalPredictions[4]);
                            finalPredictions=new String[5];
                            dictPredict=new String[5];
                            break;
                        default:
                            System.out.println("  No predictions available.");
                            break;
                    }
                }catch(NullPointerException exception){
                    System.out.println("  No predictions available.");
                }
                word="";
            }else if(response.equals("")){
            }else{
                word+=response;
                userPredictions=new String[5];
                dictPredict=new String[5];
                finalPredictions=new String[5];
                timeBefore=System.nanoTime();
                userPredictions=user.getFromPrefix(word);
                dictPredict=dictionary.getFromPrefix(word);
                try{
                    if(userPredictions!=null){
                        for(int u=0; u<5; u++){
                            if(userPredictions[u]!=null && userPredictions[u].startsWith(word)){
                                finalPredictions[spot]=userPredictions[u];
                                spot++;
                            }else{break;}
                        }
                    }else{
                        finalPredictions=dictPredict;
                    }
                    boolean works=true;
                    for(int d=0; d<5; d++){
                        for(int f=0; f<spot && spot<5; f++){
                            if(finalPredictions[f].equals(dictPredict[d]) || dictPredict[d]==null){
                                works=false;
                            }
                        }
                        if(works && spot<5 && dictPredict[d]!=null && dictPredict[d].startsWith(word)){
                            finalPredictions[spot]=dictPredict[d];
                            spot++;
                        }
                        works=true;
                    }
                }catch(NullPointerException exception){
                    finalPredictions=null;
                }
                double timeAfter=((System.nanoTime()-timeBefore)*Math.pow(10,-9));
                spot=0;
                if(finalPredictions==null){
                    System.out.println("Prefix "+word+" was not found.");
                    finalPredictions=new String[5];
                    word="";
                }else{
                    for(int a=0; a<5; a++){
                        if(finalPredictions[a]==null){break;}
                        System.out.printf("\t(%d) %-10s",(a+1), finalPredictions[a]);
                    }
                }
                System.out.printf("%n(%06f)%n",timeAfter);
                totalTime+=timeAfter; searchCount++;
            }
        }
        Node root=user.getFirst(); Node temp=null;  DLL push=null; boolean going=true;
        String s;
        if(root!=null){
            temp=root;
            push=new DLL();
            while(temp!=null){
                push.addToStack(temp);
                temp=temp.getChild();
            }
            while(going){
                s=push.makeString();
                if(push.makeString()!=null){
                    writeFile(s);
                }else{break;}
                going=push.removeToNextSib();
            }
            going=true;
        }
	}

    public static DLB readFile(String f, DLB a)
    {
        try {
            FileReader reader=new FileReader(f);
            Scanner in=new Scanner(reader);
            String s;
            while(in.hasNextLine()){
                s=in.nextLine();
                if(!s.equals("")){
                    a.addTo(s);
                }
            }
        }catch(IOException exception){
            return a;
        }
        return a;
    }

    public static String getResponse()
    {   
        String response="";
        try{
            Scanner in=new Scanner(System.in);
            System.out.print("Enter a character:\t");
            response=in.next("[a-zA-Z1-5$!']");
        }catch(InputMismatchException exception){
            System.out.println("\n  Input was not recognized.");
            System.out.println("  Please only do A-Z, a-z, 1-5, !, or $ by itself.");
            response="";
        }
        System.out.println();
        return response;
    }

    public static void writeFile(String write) throws IOException
    {   
        BufferedWriter file=new BufferedWriter(new FileWriter("user_history.txt", true));
        file.write("\n"+write); file.close();
    }  
}