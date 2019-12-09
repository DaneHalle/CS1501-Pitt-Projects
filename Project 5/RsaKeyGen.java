import java.io.FileOutputStream; 	import java.io.ObjectOutputStream;
import java.io.IOException; 		import java.util.Random;
public class RsaKeyGen
{
    private static final String RESET="\u001B[0m", RED="\033[1;31m";
    public static void main(String[] args) 
    {
        Random rd=new Random();
    	LargeInteger ONE=new LargeInteger(new byte[]{(byte)1});
    	LargeInteger p=new LargeInteger(256, rd), q=new LargeInteger(256, rd), n=p.multiply(q), d;
    	LargeInteger phi=p.subtract(ONE).multiply(q.subtract(ONE)), e=new LargeInteger(phi.length()-1, rd);
        LargeInteger[] xgcd=phi.XGCD(e); d=xgcd[2];
        while(xgcd[0].compareTo(ONE)!=0||d.isNegative()){
            e=new LargeInteger(phi.length()-1, rd); xgcd=phi.XGCD(e);
        	d=xgcd[2];
        }
        try{
            ObjectOutputStream pubOut=new ObjectOutputStream(new FileOutputStream("pubkey.rsa"));
            ObjectOutputStream privOut=new ObjectOutputStream(new FileOutputStream("privkey.rsa")); 
            pubOut.writeObject(e); privOut.writeObject(d); pubOut.writeObject(n); privOut.writeObject(n);
            pubOut.close();  privOut.close();
        }catch(IOException x){
            System.out.println(RED+"Unable to make RSA Keys"+RESET);
            System.exit(1);
        }
    }
}