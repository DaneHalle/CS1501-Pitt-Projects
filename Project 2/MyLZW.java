/*************************************************************************
 *  Compilation:  javac MyLZW.java
 *  Execution:    java MyLZW - mode < input.txt > outputFile  (compress)
 *  Execution:    java MyLZW + < input.txt > outputFile  (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *************************************************************************/
public class MyLZW 
{
    private static final int R=256;       // number of input chars
    private static int W=9;         // codeword width
    private static int L=512;       // number of codewords = 2^W
    private static char mode='n';   // sets defualt mode as 'nothing'
    private static int notCompressed=0;     private static int compressed=0;
    private static double oldRatio=0.0;     private static double newRatio=0.0;

    public static TST<Integer> initializeTST()
    {
        TST<Integer> output=new TST<Integer>();
        for(int i=0; i<R; i++){
            output.put(""+(char)i, i);
        }
        return output;
    }

    public static void incrementWidth()
    {
        W++;
        if(W>16){
            W=16;
        }else{
            L+=L;
        }
    }

    public static int resetThings()
    {
        W=9;     L=512;
        return R+1;
    }

    public static void updateRatioParts(int length)
    {
        notCompressed+=length*16;   compressed+=W;
    }

    public static void compress() 
    { 
        String input=BinaryStdIn.readString();
        BinaryStdOut.write(mode); //Writes mode character to the file
        TST<Integer> st=initializeTST();    int code=R+1;
        while (input.length()>0) {
            String s=st.longestPrefixOf(input);  // Find max prefix match s.
            BinaryStdOut.write(st.get(s), W);      // Print s's encoding.
            updateRatioParts(s.length());
            int t=s.length();
            if(code==L){
                incrementWidth();
            }
            if (t<input.length() && code<L){    // Add s to symbol table.
                st.put(input.substring(0,t+1), code++);
                oldRatio=(double)notCompressed/compressed;
            }else if(t<input.length() && code>=L){
                switch(mode){
                    case 'n': //Do nothing new
                        break;
                    case 'r': //Resets codebook if code#>=L
                        st=initializeTST();
                        code=resetThings();
                        st.put(input.substring(0, t+1), code++);
                        break;
                    case 'm': //Checks ratio and resets codebook should ratio be above 1.1
                        newRatio=(double)notCompressed/compressed;
                        if(oldRatio/newRatio>1.1){
                            st=initializeTST();
                            code=resetThings();
                            st.put(input.substring(0, t+1), code++);
                        }
                        break;
                    default: //Modes not specifided will default to do nothing
                        break;
                }
            }
            input=input.substring(t);            // Scan past s in input.
        }
        BinaryStdOut.write(R, W);
        BinaryStdOut.close();
    } 

    public static String[] initializeArray()
    {
        String[] output=new String[65536];   int i;
        for(i=0; i<R; i++){
            output[i]=""+(char)i;
        }
        output[i++]="";
        return output;
    }

    public static void expand() 
    {
        String[] st=initializeArray(); int i=R+1;
        mode=BinaryStdIn.readChar(); //Reads mode character from file and sets it to mode
        int codeword=BinaryStdIn.readInt(W);
        if(codeword==R){return;}
        String val=st[codeword];
        while(true){
            updateRatioParts(val.length());
            BinaryStdOut.write(val);
            if(i>=L){
                if(W<16){
                    incrementWidth();
                    oldRatio=(double)notCompressed/compressed;
                }else{
                    switch(mode){
                        case 'n': //Do nothing new
                            break;
                        case 'r': //Resets codebook if code#>=L
                            st=initializeArray();
                            i=resetThings();
                            break;
                        case 'm': //Checks ratio and resets codebook should ratio be above 1.1
                            newRatio=(double)notCompressed/compressed;
                            if(oldRatio/newRatio>1.1){
                                st=initializeArray();
                                i=resetThings();
                            }
                            break;
                        default: //Modes not specifided will default to do nothing
                            break;
                    }
                }
            }
            codeword=BinaryStdIn.readInt(W);
            if(codeword==R){break;}
            String s=st[codeword];
            if(i==codeword){
                s=val+val.charAt(0);
            }
            if(i<L){
                st[i++]=val+s.charAt(0);
                oldRatio=(double)notCompressed/compressed;
            }
            val=s;
        }
        BinaryStdOut.close();
    }

    public static void main(String[] args)
    {
        if(args.length>=2 && args[1].length()==1 && ((args[1].equals("m") || args[1].equals("n") 
            || args[1].equals("r")))){
            mode=args[1].charAt(0);
        }
        if(args[0].equals("-")){
            compress();
        }else if(args[0].equals("+")){
            expand();
        }else{
            throw new IllegalArgumentException("Illegal command line argument");
        }
    }
}