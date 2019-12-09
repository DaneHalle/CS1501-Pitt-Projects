import java.io.FileInputStream;     import java.io.ObjectInputStream;
import java.io.FileOutputStream;    import java.io.ObjectOutputStream;
import java.io.IOException;         import java.lang.ClassNotFoundException;
public class RsaSign
{
    private static final String RESET="\u001B[0m", RED="\033[1;31m", GREEN="\u001B[32m", YELLOW="\u001B[33m";
    public static void main(String[] args)
    {
        if(args.length!=2) {
            System.out.println(RED+"Must have only two arguments. 's' or 'v' and then the filename."+RESET);
            return;
        }
        if(args[0].equals("s")){
            sign(args[1], HashEx.generateHash(args[1]));
        }else if(args[0].equals("v")){
            verify(args[1], HashEx.generateHash(args[1]));
        }else{
            System.out.println(RED+"Invalid flag given. Needs to be 's' or 'v' and then the filename."+RESET);
            return;
        }
    }

    public static void sign(String file, LargeInteger hash)
    {
        try{
            ObjectInputStream privFile=new ObjectInputStream(new FileInputStream("privkey.rsa"));
            try{
                ObjectOutputStream sigOut=new ObjectOutputStream(new FileOutputStream(file+".sig"));
                sigOut.writeObject(hash.modularExp(((LargeInteger)privFile.readObject()), ((LargeInteger)privFile.readObject()))); 
                sigOut.close();
            }catch(Exception e){
                System.err.println(RED+"Could not save signature file."+RESET);
                return;
            }
        }catch(Exception x){
            System.err.println(RED+"privkey.rsa was not found in the directory."+RESET);
            return;
        }
    }

    public static void verify(String file, LargeInteger hash)
    {
        try{
            ObjectInputStream pubFile=new ObjectInputStream(new FileInputStream("pubkey.rsa"));
            try{
                ObjectInputStream signed=new ObjectInputStream(new FileInputStream(file+".sig"));
                if(((LargeInteger)signed.readObject()).modularExp(((LargeInteger)pubFile.readObject()), ((LargeInteger)pubFile.readObject())).subtract(hash).isZero()){
                    System.out.println(GREEN+"The signature is valid."+RESET);
                    return;
                }else{
                    System.out.println(YELLOW+"The signature is not valid."+RESET);
                    return;
                }
            }catch(Exception e){
                System.err.println(RED+file+".sig was not found in the directory."+RESET);
                return;
            }
        }catch(Exception err){
            System.err.println(RED+"pubkey.rsa was not found in the directory."+RESET);
            return;
        }
    }
}