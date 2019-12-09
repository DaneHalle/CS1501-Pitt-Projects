import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.security.MessageDigest;
public class HashEx
{
    private static final String RESET="\u001B[0m", RED="\033[1;31m";
	public static LargeInteger generateHash(String filename)
	{
		// lazily catch all exceptions...
		try{
			Path path = Paths.get(filename); // read in the file to hash
			byte[] data = Files.readAllBytes(path);
			// create class instance to create SHA-256 hash
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(data); // process the file
			byte[] digest = md.digest(); // generate a hash of the file
			return new LargeInteger(digest);
		}catch(Exception e) {
			System.out.println(RED+e.toString()+RESET);
			return null;
		}
	}
}