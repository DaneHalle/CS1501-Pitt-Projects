public class apartments
{
	private String streetAddress;
	private String apartmentNumber;
	private String city;
	private int zipCode;
	private double rent;
	private int sqFeet;
	public apartments(String in)
	{
		streetAddress=findColon(in);
		in=in.substring(in.indexOf(':')+1);
		apartmentNumber=findColon(in);
		in=in.substring(in.indexOf(':')+1);
		city=findColon(in);
		in=in.substring(in.indexOf(':')+1);
		zipCode=Integer.parseInt(findColon(in));
		in=in.substring(in.indexOf(':')+1);
		rent=Double.valueOf(findColon(in));
		in=in.substring(in.indexOf(':')+1);
		sqFeet=Integer.parseInt(findColon(in));

	}

	private String findColon(String in)
	{
		if(in.indexOf(':')==-1){
			return in;
		}
		return in.substring(0,in.indexOf(':'));
	}

	public void updateRent(double in)
	{
		rent=in;
	}

	public String getStreetAddress()
	{
		return streetAddress;
	}

	public String getApartmentNumber()
	{
		return apartmentNumber;
	}

	public String getCity()
	{
		return city;
	}

	public int getZipCode()
	{
		return zipCode;
	}

	public double getRent()
	{
		return rent;
	}

	public int getSquareFeet()
	{
		return sqFeet;
	}

	public void print()
	{
		System.out.println(streetAddress+" "+apartmentNumber+" "+city+" "+zipCode+" "+rent+" "+sqFeet);
	}

	public String printString()
	{
		return ""+streetAddress+" "+apartmentNumber+" "+city+" "+zipCode+" "+rent+" "+sqFeet;
	}
}