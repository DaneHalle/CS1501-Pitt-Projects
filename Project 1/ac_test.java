import java.util.Scanner; import java.lang.Math;
import java.io.FileReader; import java.io.IOException;
import java.io.PrintWriter; import java.io.FileNotFoundException;
import java.util.InputMismatchException; import java.io.FileWriter;
import java.io.BufferedWriter; import java.io.File;
public class ac_test
{
    /** 
     *  Main method for the program that does a lot. Makes empty two DLB tries.
     *  One for dictionary and one for user_history. It then initializes some other things.
     *  It fills the dictionary DLB with the text found in dictionary.txt. Then it makes a
     *  File object for the user_history.txt. It attempts to fill the user DLB with the 
     *  text found in the user_history if it should exist and then deletes the file. 
     *  Within the while loop a lot more happens. It prompts a response from the user and
     *  parses out what to do form there. It will end program, accept string up until the 
     *  point the user has printed, predict words based on the prefix, and choose a word. 
     *  After every prediction the program prints the time it took for predictions to be
     *  found in seconds. After the user exits, the user DLB is parsed through and printed
     *  to a new user_history.txt file.
     */ 
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
                System.out.printf("Average time:\t%06f s\nYeet!%n", (totalTime/searchCount));
                break;
            }else if(response.equals("")){
            }else if(word.equals("")&&response.equals("$")){
                System.out.println("  No word found. Please enter characters to fill out a word.\n");
            }else if(response.equals("$")){
                System.out.println("  WORD COMPLETED:\t"+word+"\n");
                user.addTo(word);  word="";
            }else if(response.equals("%")){
                System.out.println("  Prefix cleared\n");
                word="";
            }else if(response.equals("1") || response.equals("2") || response.equals("3") || 
                response.equals("4") || response.equals("5")){
                try{
                    switch(response){
                        case "1":
                            if(finalPredictions[0]!=null){
                                System.out.println("  WORD COMPLETED:\t"+finalPredictions[0]+"\n");
                                user.addTo(finalPredictions[0]); finalPredictions=new String[5];
                                dictPredict=new String[5]; word="";
                            }else{
                                System.out.println("  No predictions available.\n");
                            }
                            break;
                        case "2":
                            if(finalPredictions[1]!=null){
                                System.out.println("  WORD COMPLETED:\t"+finalPredictions[1]+"\n");
                                user.addTo(finalPredictions[1]); finalPredictions=new String[5];
                                dictPredict=new String[5];  word="";
                            }else{
                                System.out.println("  No predictions available.\n");
                            }
                            break;
                        case "3":
                            if(finalPredictions[2]!=null){
                                System.out.println("  WORD COMPLETED:\t"+finalPredictions[2]+"\n");
                                user.addTo(finalPredictions[2]); finalPredictions=new String[5];
                                dictPredict=new String[5];  word="";
                            }else{
                                System.out.println("  No predictions available.\n");
                            }
                            break;
                        case "4":
                            if(finalPredictions[3]!=null){
                                System.out.println("  WORD COMPLETED:\t"+finalPredictions[3]+"\n");
                                user.addTo(finalPredictions[3]); finalPredictions=new String[5];
                                dictPredict=new String[5];  word="";
                            }else{
                                System.out.println("  No predictions available.\n");
                            }
                            break;
                        case "5":
                            if(finalPredictions[4]!=null){
                                System.out.println("  WORD COMPLETED:\t"+finalPredictions[4]+"\n");
                                user.addTo(finalPredictions[4]); finalPredictions=new String[5];
                                dictPredict=new String[5];  word="";
                            }else{
                                System.out.println("  No predictions available.\n");
                            }
                            break;
                        default:
                            System.out.println("  No predictions available.\n");
                            break;
                    }
                }catch(NullPointerException exception){
                    System.out.println("  No predictions available.\n");
                }
            }else{
                word+=response; userPredictions=new String[5];
                dictPredict=new String[5]; finalPredictions=new String[5];
                timeBefore=System.nanoTime();
                userPredictions=user.getSmartPrefix(word);
                dictPredict=dictionary.getFromPrefix(word);
                try{
                    if(userPredictions!=null){
                        for(int u=0; u<5; u++){
                            if(userPredictions[u]!=null && userPredictions[u].startsWith(word)){
                                finalPredictions[spot]=userPredictions[u]; spot++;
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
                            finalPredictions[spot]=dictPredict[d]; spot++;
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
                }else{
                    System.out.println("Predictions:");
                    for(int a=0; a<5; a++){
                        if(finalPredictions[a]==null){break;}
                        System.out.printf("\t(%d) %-10s",(a+1), finalPredictions[a]);
                    }
                }
                System.out.printf("%n(%06f s)%n%n",timeAfter);
                totalTime+=timeAfter; searchCount++;
            }
        }
        Node root=user.getFirst(); Node temp=null;  DLL push=new DLL(); boolean going=true;
        String s; int freq=0; 
        int count=0;
        if(root!=null){
            temp=root;
            while(temp!=null){
                push.addToStack(temp); temp=temp.getChild();
            }
            while(going && push.getTail()!=null){
                freq=push.getFreq(); s=push.makeString();
                if(push.makeString()!=null){
                    for(int p=0; p<freq; p++){
                        writeFile(s);
                    }
                }else{break;}
                going=push.removeToNextSib();
            }
        }
	}

    /** 
     *  Reads the file and sends it to the associated DLB
     */ 
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

    /** 
     *  Prompts a response of 1 character from user. 
     */ 
    public static String getResponse()
    {   
        String response="";
        try{
            Scanner in=new Scanner(System.in);
            System.out.print("Enter a character:\t");
            response=in.next("[a-zA-Z1-5$!'%]");
        }catch(InputMismatchException exception){
            System.out.println("\n  Input was not recognized.");
            System.out.println("  Please only do A-Z, a-z, 1-5, !, %, or $ by itself.");
            response="";
        }
        System.out.println();
        return response;
    }

    /** 
     *  Writes to the user_history.txt file
     */ 
    public static void writeFile(String write) throws IOException
    {   
        BufferedWriter file=new BufferedWriter(new FileWriter("user_history.txt", true));
        file.write(write+"\n"); file.close();
    }  
}