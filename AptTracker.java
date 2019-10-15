import java.io.FileReader;
import java.util.Scanner;
import java.io.IOException;
public class AptTracker
{
	public static void main(String[] args)
	{
		String[] stuff=readFile("apartments.txt");
		apartments[] stuff2=new apartments[100]; int i;
		for(i=0; i<stuff.length; i++){
			if(stuff[i]==null){
				break;
			}
			stuff2[i]=new apartments(stuff[i]);
		}
		for(int j=0; j<i; j++){
			stuff2[j].print();
		}
	}

	public static String[] readFile(String f)
    {
    	String[] out=new String[100];
        try {
            FileReader reader=new FileReader(f);
            Scanner in=new Scanner(reader);
            String s; int i=0;
            while(in.hasNextLine()){
                s=in.nextLine();
                if(s.indexOf('#')==-1){
                	out[i]=s; i++;
                }
            }
        }catch(IOException exception){
            return out;
        }
        return out;
    }
}