import java.io.FileReader;		import java.util.Scanner;
import java.io.IOException;		import java.util.Arrays;
import java.util.Comparator;	import java.io.BufferedWriter;
import java.io.FileWriter;		import java.io.File;
public class AptTracker
{
	private static Scanner in=new Scanner(System.in);
	private static MinMaxPQ rentQueue;
	private static MinMaxPQ feetQueue;
	private static cityHash[] cityHashMap;
	public static void main(String[] args) throws IOException
	{
		rentQueue=new MinMaxPQ(true, new RentComparator());
		feetQueue=new MinMaxPQ(false, new SqFeetComparator());
		cityHashMap=new cityHash[257];
		for(int i=0; i<257; i++){
			cityHashMap[i]=new cityHash();
		}
		readFile("apartments.txt");
		System.out.println("Welcome! You have access to the following options:");
		System.out.println("\t1. Add an apartment");
		System.out.println("\t2. Update an apartment");
		System.out.println("\t3. Remove an apartment");
		System.out.println("\t4. Retrieve the lowest rent apartment");
		System.out.println("\t5. Retrieve the highest square footage apartment");
		System.out.println("\t6. Retrieve the lowest rent apartment by city");
		System.out.println("\t7. Retrieve the highest square footage apartment by city");
		System.out.println("Use numbers to choose which you will do. Use 0 to exit the program");
		apartments gotten; boolean run=true;
		while(run){
			System.out.print("What option would you like to do:\t");
			try{
				int response=Integer.parseInt(in.nextLine());
				switch(response){
					case 0: //exit
						run=false;
						break;
					case 1: //Add
						userInputAddress();
						System.out.println();
						break;
					case 2: //Update
						updateApartment();
						System.out.println();
						break;
					case 3: //Remove
						gotten=removeApartment();
						if(gotten!=null){
							System.out.println("\tRemoved: "+gotten.printString()+"\n");
						}
						break;
					case 4: //LowRent
						gotten=getLowestRent();
						if(gotten!=null){
							System.out.println("\tLowest Rent: "+gotten.printString()+"\n");
						}
						break;
					case 5: //HighSqFeet
						gotten=getHighestSqFeet();
						if(gotten!=null){
							System.out.println("\tHighest Square Feet: "+gotten.printString()+"\n");
						}
						break;
					case 6: //LowRentCity
						gotten=getLowestRentCity();
						if(gotten!=null){
							System.out.print("\tLowest Rent within "+gotten.getCity()+": ");
							System.out.println(gotten.printString()+"\n");
						}else{
							System.out.print("\tUnable to find lowest rent within that city as there are ");
							System.out.println("no apartments associated with that city within the system.");
						}
						break;
					case 7: //HighSqFeetCity
						gotten=getHighSqFeetCity();
						if(gotten!=null){
							System.out.print("\tHighest Square feet within "+gotten.getCity()+": ");
							System.out.println(gotten.printString()+"\n");
						}else{
							System.out.print("\tUnable to find highest square feet within that city as");
							System.out.print(" there are no apartments associated with that city ");
							System.out.println("within the system.");
						}
						break;
					default: //Everything else
						System.out.println("Not a valid option.\n");
						break;
				}
			}catch(NumberFormatException exception){ //Should you crash, count as everything else
				System.out.println("Not a valid option.");
			}
		}
		System.out.print("\nWould you like to write to a file (yes/no): ");
		String response=in.nextLine();
		if(response.equals("yes")){ //Writes the altered queue to a file
			System.out.print("What would you like the neame of the file to be (add .txt): ");
			response=in.nextLine();
			writeFile(response);
		}
		System.out.println("Have a good day!");
	}

	private static void userInputAddress() 
	{
		String address="";
		System.out.print("\tEnter the street address:\t\t");
		address+=in.nextLine(); address+=":";
		System.out.print("\tEnter the apartment number:\t\t");
		address+=in.nextLine(); address+=":";
		System.out.print("\tEnter the city:\t\t");
		address+=in.nextLine(); address+=":";
		System.out.print("\tEnter the zip code:\t\t");
		address+=in.nextLine(); address+=":";
		System.out.print("\tEnter the cost of rent:\t\t");
		address+=in.nextLine(); address+=":";
		System.out.print("\tEnter the square feet of the apartment:\t\t");
		address+=in.nextLine();
		System.out.println("\t\tAdded: "+(new apartments(address)).printString());
		rentQueue.insert(new apartments(address));
		feetQueue.insert(new apartments(address));
		putCityHash(new apartments(address));
	}

	private static void updateApartment()
	{
		if(rentQueue.isEmpty()&&feetQueue.isEmpty()){
			System.out.println("\tUnable to update as there are no apartments within the system.");
		}else{
			System.out.print("\tEnter the street address:\t\t");
			String street=in.nextLine();
			System.out.print("\tEnter the apartment number:\t\t");
			String number=in.nextLine();
			System.out.print("\tEnter the zip code:\t\t");
			int zip=Integer.parseInt(in.nextLine());
			int rentLoc=rentQueue.contains(street, number, zip);
			int feetLoc=feetQueue.contains(street, number, zip);
			System.out.println("\t\tFound: "+rentQueue.get(rentLoc).printString());
			System.out.print("\tWould you like to update the rent (yes/no): ");
			String response=in.nextLine();
			if(response.equals("yes")){
				System.out.print("\tWhat is the new rent: ");
				double newRent=Double.valueOf(in.nextLine());
				apartments update=rentQueue.remove(rentLoc);
				feetQueue.remove(feetLoc);
				update.updateRent(newRent);
				System.out.println("\t\tUpdated: "+update.printString());
				rentQueue.insert(update);
				feetQueue.insert(update);
				cityHashMap[findCityLoc(update.getCity())].updateRent(street, number, zip, update);
			}
		}
	}

	private static apartments removeApartment()
	{
		if(rentQueue.isEmpty()&&feetQueue.isEmpty()){
			System.out.println("\tUnable to remove as there are no apartments within the system.");
			return null;
		}
		System.out.print("\tEnter the street address:\t\t");
		String street=in.nextLine();
		System.out.print("\tEnter the apartment number:\t\t");
		String number=in.nextLine();
		System.out.print("\tEnter the zip code:\t\t");
		int zip=Integer.parseInt(in.nextLine());
		int rentLoc=rentQueue.contains(street, number, zip);
		int feetLoc=feetQueue.contains(street, number, zip);
		cityHashMap[findCityLoc(rentQueue.remove(rentLoc).getCity())].remove(street, number, zip);
		return feetQueue.remove(feetLoc);
	}

	private static apartments getLowestRent()
	{
		if(rentQueue.isEmpty()){
			System.out.print("Unable to retireve the apartment with lowest rent as there are no apartments");
			System.out.println(" within the system.");
			return null;
		}
		return rentQueue.top();
	}

	private static apartments getHighestSqFeet()
	{
		if(feetQueue.isEmpty()){
			System.out.print("Unable to retireve the apartment with highest square feet as there are ");
			System.out.println("no apartments within the system.");
			return null;
		}
		return feetQueue.top();
	}

	private static apartments getLowestRentCity() 
	{
		System.out.print("\tEnter the city:\t\t");
		String city=in.nextLine(); 
		return cityHashMap[findCityLoc(city)].getLowestRent();
	}

	private static apartments getHighSqFeetCity() 
	{
		System.out.print("\tEnter the city:\t\t");
		String city=in.nextLine();
		return cityHashMap[findCityLoc(city)].getHighestSqFeet();
	}

	private static void putCityHash(apartments input)
	{
		int firstHash=0;
		for(int i=0; i<input.getCity().length(); i++){
			firstHash+=(int)input.getCity().charAt(i);
		}
		int first=firstHash%257;
		if(cityHashMap[first].getCity()==null){
			cityHashMap[first].setCity(input.getCity());
			cityHashMap[first].insert(input);
		}else if(cityHashMap[first].getCity()!=null&&input.getCity().equals(cityHashMap[first].getCity())){
			cityHashMap[first].insert(input);
		}else if(!input.getCity().equals(cityHashMap[first])){
			boolean found=false; int h2=(firstHash%101)+1;
			while(!found){
				first+=h2;
				first%=257;
				if(cityHashMap[first].getCity()==null){
					cityHashMap[first].setCity(input.getCity());
					cityHashMap[first].insert(input);
					found=true;
				}else if(cityHashMap[first].getCity()!=null&&
					input.getCity().equals(cityHashMap[first].getCity())){
					cityHashMap[first].insert(input);
					found=true;
				}
			}
		}
	}

	private static int findCityLoc(String city)
	{
		int firstHash=0;
		for(int i=0; i<city.length(); i++){
			firstHash+=(int)city.charAt(i);
		}
		int first= firstHash%257; String firstLoc;
		if(cityHashMap[first].getCity()==null){
			return first;
		}else if(cityHashMap[first].getCity()!=null&&city.equals(cityHashMap[first].getCity())){
			return first;
		}else if(!city.equals(cityHashMap[first])){
			boolean found=false; int h2=(firstHash%101)+1; firstLoc=cityHashMap[first].getCity();
			while(!found){
				first+=h2;
				first%=257;
				if(cityHashMap[first].getCity()==null){
					found=true;
					return first;
				}else if(cityHashMap[first].getCity()!=null&&city.equals(cityHashMap[first].getCity())){
					found=true;
					return first;
				}else if(cityHashMap[first].equals(firstLoc)){
					return -1;
				}
			}
			return first;
		}
		return -1;
	}

	private static void readFile(String f)
    {
        try {
            FileReader reader=new FileReader(f);
            Scanner in=new Scanner(reader);
            String s; int i=0;
            while(in.hasNextLine()){
                s=in.nextLine();
                if(s.indexOf('#')==-1){
                	feetQueue.insert(new apartments(s));
                	rentQueue.insert(new apartments(s));
                	putCityHash(new apartments(s));
                }
            }
        }catch(IOException exception){}
    }

    private static void writeFile(String fileName) throws IOException
    {   
    	File f=new File(fileName);
    	f.delete();
    	BufferedWriter file=new BufferedWriter(new FileWriter(fileName, true));
    	file.write("# Street address:Apt Number:City:Zip:Price:SqFt\n"); file.close();
		while(rentQueue.getSize()>=1){ 
			file=new BufferedWriter(new FileWriter(fileName, true));
			file.write(rentQueue.deleteTop().revertToString()+"\n");
			file.close();
		}
    }  
}

class cityHash
{
	private String city;
	private MinMaxPQ cityRentQueue;
	private MinMaxPQ cityFeetQueue;
	public cityHash()
	{
		city=null; 
		cityRentQueue=new MinMaxPQ(true, new RentComparator());
		cityFeetQueue=new MinMaxPQ(false, new SqFeetComparator());
	}

	public String getCity()
	{
		return city;
	}

	public void setCity(String in)
	{
		city=in;
	}

	public void insert(apartments in)
	{
		cityRentQueue.insert(in);
		cityFeetQueue.insert(in);
	}

	public void remove(String street, String apartment, int zip)
	{
		apartments removed=cityRentQueue.remove(street, apartment, zip);
		cityFeetQueue.remove(street, apartment, zip);
		if(cityRentQueue.isEmpty()||removed==null){
			cityRentQueue=new MinMaxPQ(true, new RentComparator());
			cityFeetQueue=new MinMaxPQ(false, new SqFeetComparator());
		}
	}

	public void updateRent(String street, String apartment, int zip, apartments updated)
	{
		apartments removed=cityRentQueue.remove(street, apartment, zip);
		cityFeetQueue.remove(street, apartment, zip);
		if(cityRentQueue.isEmpty()||removed==null){
			cityRentQueue=new MinMaxPQ(true, new RentComparator());
			cityFeetQueue=new MinMaxPQ(false, new SqFeetComparator());
		}
		insert(updated);
	}

	public apartments getLowestRent()
	{
		if(cityRentQueue.isEmpty()){
			return null;
		}
		return cityRentQueue.top();
	}

	public apartments getHighestSqFeet()
	{
		if(cityFeetQueue.isEmpty()){
			return null;
		}
		return cityFeetQueue.top();
	}
}

class RentComparator implements Comparator<apartments> 
{
	public int compare(apartments a, apartments b)
	{
		if(a.getRent()<b.getRent()){
			return -1;
		}else if(a.getRent()>b.getRent()){
			return 1;
		}else{
			return 0;
		}
	}
}

class SqFeetComparator implements Comparator<apartments> 
{
	public int compare(apartments a, apartments b)
	{
		if(a.getSquareFeet()<b.getSquareFeet()){
			return -1;
		}else if(a.getSquareFeet()>b.getSquareFeet()){
			return 1;
		}else{
			return 0;
		}
	}
}