import java.util.Random;			import java.math.BigInteger;  
import java.io.Serializable;
public class LargeInteger implements Serializable
{

//-------------------------------------------------------------------
//							Given Code
//-------------------------------------------------------------------

	private final byte[] ZERO={(byte)0}, ONE={(byte)1};
	private byte[] val;
	/**
	 * Construct the LargeInteger from a given byte array
	 * @param b the byte array that this LargeInteger should represent
	 */
	public LargeInteger(byte[] b)
	{
		val=b;
	}

	public LargeInteger(LargeInteger b)
	{
		val=b.getVal();
	}

	/**
	 * Construct the LargeInteger by generatin a random n-bit number that is
	 * probably prime (2^-100 chance of being composite).
	 * @param n the bitlength of the requested integer
	 * @param rnd instance of java.util.Random to use in prime generation
	 */
	public LargeInteger(int n, Random rnd)
	{
		val=BigInteger.probablePrime(n, rnd).toByteArray();
	}
	
	/**
	 * Return this LargeInteger's val
	 * @return val
	 */
	public byte[] getVal()
	{
		return val;
	}

	/**
	 * Return the number of bytes in val
	 * @return length of the val byte array
	 */
	public int length()
	{
		return val.length;
	}

	/** 
	 * Add a new byte as the most significant in this
	 * @param extension the byte to place as most significant
	 */
	public void extend(byte extension)
	{
		byte[] newv=new byte[val.length+1];
		newv[0]=extension;
		for (int i=0; i<val.length; i++){
			newv[i+1]=val[i];
		}
		val=newv;
	}

	/**
	 * If this is negative, most significant bit will be 1 meaning most 
	 * significant byte will be a negative signed number
	 * @return true if this is negative, false if positive
	 */
	public boolean isNegative()
	{
		return (val[0]<0);
	}

	/**
	 * Computes the sum of this and other
	 * @param other the other LargeInteger to sum with this
	 */
	public LargeInteger add(LargeInteger other)
	{
		byte[] a, b;
		// If operands are of different sizes, put larger first ...
		if (val.length<other.length()){
			a=other.getVal();
			b=val;
		}else {
			a=val;
			b=other.getVal();
		}
		// ... and normalize size for convenience
		if (b.length<a.length){
			int diff=a.length-b.length;
			byte pad=(byte)0;
			if (b[0]<0){
				pad=(byte)0xFF;
			}
			byte[] newb=new byte[a.length];
			for (int i=0; i<diff; i++){
				newb[i]=pad;
			}
			for (int i=0; i<b.length; i++){
				newb[i+diff]=b[i];
			}
			b=newb;
		}
		// Actually compute the add
		int carry=0;
		byte[] res=new byte[a.length];
		for (int i=a.length-1; i>=0; i--){
			// Be sure to bitmask so that cast of negative bytes does not
			//  introduce spurious 1 bits into result of cast
			carry=((int)a[i]&0xFF)+((int)b[i]&0xFF)+carry;
			// Assign to next byte
			res[i]=(byte)(carry&0xFF);
			// Carry remainder over to next byte (always want to shift in 0s)
			carry=carry>>>8;
		}

		LargeInteger res_li=new LargeInteger(res);
		// If both operands are positive, magnitude could increase as a result
		//  of addition
		if (!this.isNegative()&& !other.isNegative()){
			// If we have either a leftover carry value or we used the last
			//  bit in the most significant byte, we need to extend the result
			if (res_li.isNegative()){
				res_li.extend((byte)carry);
			}
		}
		// Magnitude could also increase if both operands are negative
		else if (this.isNegative()&& other.isNegative()){
			if (!res_li.isNegative()){
				res_li.extend((byte)0xFF);
			}
		}
		// Note that result will always be the same size as biggest input
		//  (e.g., -127+128 will use 2 bytes to store the result value 1)
		return res_li;
	}

	/**
	 * Negate val using two's complement representation
	 * @return negation of this
	 */
	public LargeInteger negate()
	{
		byte[] neg=new byte[val.length];
		int offset=0;
		// Check to ensure we can represent negation in same length
		//  (e.g., -128 can be represented in 8 bits using two's 
		//  complement, +128 requires 9)
		if (val[0]==(byte)0x80){ // 0x80 is 10000000
			boolean needs_ex=true;
			for (int i=1; i<val.length; i++){
				if (val[i]!=(byte)0){
					needs_ex=false;
					break;
				}
			}
			// if first byte is 0x80 and all others are 0, must extend
			if (needs_ex){
				neg=new byte[val.length+1];
				neg[0]=(byte)0;
				offset=1;
			}
		}
		// flip all bits
		for (int i=0; i<val.length; i++){
			neg[i+offset]=(byte)~val[i];
		}
		LargeInteger neg_li=new LargeInteger(neg);
		// add 1 to complete two's complement negation
		return neg_li.add(new LargeInteger(ONE));
	}

	/**
	 * Implement subtraction as simply negation and addition
	 * @param other LargeInteger to subtract from this
	 * @return difference of this and other
	 */
	public LargeInteger subtract(LargeInteger other)
	{
		return this.add(other.negate());
	}

//-------------------------------------------------------------------
//							Added Code
//-------------------------------------------------------------------

	/**
	 * Compute the product of this and other
	 * @param other LargeInteger to multiply by this
	 * @return product of this and other
	 */
	public LargeInteger multiply(LargeInteger other)
	{
		byte[] thisData=getVal(), otherData=other.getVal(), scratch; 
		LargeInteger out=new LargeInteger(new byte[1]); 
		boolean negOut=false;
		if(isZero()||other.isZero()){
			return new LargeInteger(ZERO);
		}
		if(isNegative()){
			thisData=negate().getVal(); negOut=true;
		}
		if(other.isNegative()){
			otherData=other.negate().getVal(); 
			if(negOut){
				negOut=false;
			}else{
				negOut=true;
			}
		}
		for(int i=0; i<thisData.length; i++){
			for(int j=0; j<otherData.length; j++){
				int mult=(thisData[i]&255)*(otherData[j]&255), shift=thisData.length-i+otherData.length-j;
				byte upper=(byte)((mult>>8)), low=(byte)(mult&255);
				scratch=new byte[shift];
				if(upper<0){
					scratch=new byte[shift+1];
				}
				scratch[scratch.length-shift+1]=low; scratch[scratch.length-shift]=upper;
				LargeInteger tempInt=new LargeInteger(scratch);
				out=out.add(tempInt);			
			}
		}
		if(negOut){
			out=out.negate(); 
		}
		return out.fix(); 
	}

	/**
	 * Run the Extended Euclidean algorithm on this and other
	 * @param other another LargeInteger
	 * @return an array structured as follows:
	 *   0:  the GCD of this and other
	 *   1:  a valid x value
	 *   2:  a valid y value
	 * such that this*x+other*y=GCD in index 0
	 */
	public LargeInteger[] XGCD(LargeInteger other)
	{
		if(other.isZero()){
			return new LargeInteger[]{this, new LargeInteger(ONE), new LargeInteger(ZERO)};
		}
		LargeInteger[] xgcd=other.XGCD(modulus(other));
		LargeInteger gcd=xgcd[0], x=xgcd[2], y=xgcd[1].subtract(divide(other).multiply(x));
		return new LargeInteger[]{gcd, x, y};
	}

	/**
	 * Compute the result of raising this to the power of y mod n
	 * @param y exponent to raise this to
	 * @param n modulus value to use
	 * @return this^y mod n
	 */
	public LargeInteger modularExp(LargeInteger y, LargeInteger n)
	{
		byte[] exp=y.getVal();
		LargeInteger out=new LargeInteger(ONE);
		for(int i=0; i<exp.length; i++){
			for(int j=7; j>=0; j--){
				out=out.multiply(out).modulus(n);
				if(((exp[i]>>j)&1)==1){
					out=multiply(out).modulus(n);
				}
			}
		}
		return out;
	}

	/**
	 * Checks to see if LargeInteger this is zero or not
	 * @return boolean if this is zero or not
	 */
	public boolean isZero()
	{
		for(int i=0; i<val.length; i++){
			if(val[i]!=0){
				return false;
			}
		}
		return true;
	}

	/**
	 * Compute the result of dividing this by other
	 * @param other divisor in the equation
	 * @return an array structred as follows:
	 * 		0: quotient of this/other
	 * 		1: reaminder of this/other
	 * such that quotient*other+remainder=this
	 */
	private LargeInteger[] divMod(LargeInteger other)
	{
		LargeInteger here=this, div=other;
		if(div.isNegative()){
			div=div.negate();
		}
		if(here.isNegative()){
			here=here.negate();
		}
		if(here.compareTo(div)==0){
			if((isNegative()&&!div.isNegative())||(!isNegative()&&div.isNegative())){
				return new LargeInteger[]{new LargeInteger(ONE).negate(), new LargeInteger(ZERO)};
			}
			return new LargeInteger[]{new LargeInteger(ONE), new LargeInteger(ZERO)};
		}
		if(here.compareTo(div)<0){
			if(isNegative()){
				return new LargeInteger[]{new LargeInteger(ZERO), here.negate()};
			}
			return new LargeInteger[]{new LargeInteger(ZERO), here};
		}
		div=div.shiftLeft((here.length()*8-1), false);
		LargeInteger rem=here, quo=new LargeInteger(ZERO);
		for(int i=0; i<=(here.length()*8-1); i++){
			LargeInteger difference=rem.subtract(div);
			if(difference.isNegative()){
				quo=quo.shiftLeft(1, false);
			}else{
				quo=quo.shiftLeft(1, true); rem=difference;
			}
			div=div.shiftRight();
		}
		quo=quo.fix(); rem=rem.fix();
		if(isNegative()&&!rem.isZero()){
			rem=rem.negate();
		}
		if((isNegative()&&!other.isNegative())||(!isNegative()&&other.isNegative())&&!quo.isZero()){
			quo=quo.negate();
		}
		return new LargeInteger[]{quo, rem};
	}

	/**
	 * Compute the quotient of this divided by other
	 * @param other divisor to use
	 * @return the quotient of this/other
	 */
	private LargeInteger divide(LargeInteger other)
	{
		return divMod(other)[0];
	}

	/**
	 * Compute the modulus of this when modulated by other
	 * @param other modulator to use
	 * @return this%other aka remainder
	 */
	public LargeInteger modulus(LargeInteger other)
	{
		return divMod(other)[1];
	}

	/**
	 * Compares this to other
	 * @param other comparator in the method
	 * @return returns the following:
	 * 		-1 if this < other
	 * 		 0 if this = other
	 * 		 1 if this > other
	 */
	public int compareTo(LargeInteger other)
	{
		LargeInteger aInt=fix(), bInt=other.fix();
		if(!aInt.isNegative()&&bInt.isNegative()){
			return 1;
		}else if(aInt.isNegative()&&!bInt.isNegative()){
			return -1;
		}
		int mul=1;
		if(aInt.isNegative()){
			mul=-1;
		}
		if(aInt.length()>bInt.length()){
			return 1*mul;
		}
		if(aInt.length()<bInt.length()){
			return -1*mul;
		}
		byte[] a=aInt.getVal(), b=bInt.getVal();
		if(aInt.isNegative()){
			a=aInt.negate().getVal(); b=bInt.negate().getVal();
		}
		for(int i=0; i<a.length; i++){
			if((a[i]&0xFF)<(b[i]&0xFF)){
				return -1*mul;
			}else if((a[i]&0xFF)>(b[i]&0xFF)){
				return 1*mul;
			}
		}
		return 0;
	}

	/**
	 * @return the LargeInteger with reduced padding of LargeInteger
	 */
	private LargeInteger fix()
	{	
		int padAmt=0, curBit=7, first;
		byte cur=val[0], pad;
		first=cur>>1; pad=(byte)0;
		if(first==1){
			pad=(byte)255;
		}
		for(int j=1; cur==pad&&j<val.length; j++){
			cur=val[j]; padAmt+=8;
		}
		while((cur>>(curBit&1))==first&&curBit>=0){
			curBit--; padAmt++;
		}
		if((padAmt-1)>8){
			byte[] out=new byte[val.length-(padAmt-1)/8];
			for(int i=0; i<val.length-(padAmt-1)/8; i++){
				out[i]=val[((padAmt-1)/8)+i];
			}
			return new LargeInteger(out);
		}
		return this;
	}

	/**
	 * Computes the LargeInteger of this shifted left by shift
	 * @param shift the ammount to shift the integer
	 * @param shiftWithOne determiens if shifts with one 
	 * @return the shifted version of this
	 */ 
	private LargeInteger shiftLeft(int shift, boolean shiftWithOne)
	{
		if(shift<=0){
			return this;
		}
		int padAmt=0, curBit=7, first;
		byte cur=val[0], pad;
		first=cur>>1; pad=(byte)0;
		if(first==1){
			pad=(byte)255;
		}
		for(int j=1; cur==pad&&j<val.length; j++){
			cur=val[j]; padAmt+=8;
		}
		while((cur>>(curBit&1))==first&&curBit>=0){
			curBit--; padAmt++;
		}
		int leftShift=shift%8, shfitBytes=shift/8+1;
		if(shift%8==0||leftShift<padAmt%8){
			shfitBytes--;
		}
		if(leftShift==0){
			leftShift=8;
		}
		byte[] out=new byte[val.length+shfitBytes]; 
		if(leftShift<padAmt%8){
			for(int i=0; i<val.length-1; i++){				
				byte lPiece=(byte)((val[i]<<leftShift)&(-1<<leftShift)), rPiece=(byte)((val[i+1]>>(8-leftShift))&(127>>(8-leftShift-1)));
				if(leftShift==8){
					lPiece=(byte)((val[i]<<leftShift)&0);
				}
				if((8-leftShift)==0){
					rPiece=(byte)((val[i+1]>>(8-leftShift))&-1);
				}
				out[i]=(byte)(lPiece|rPiece);
			}
			out[val.length-1]=(byte)(val[val.length-1]<<leftShift);
			if(shiftWithOne){
				out[val.length-1]=(byte)(out[val.length-1]|(1<<leftShift)-1);
				if(leftShift==0){
					out[val.length-1]=(byte)(out[val.length-1]|0);
				}else if(leftShift==8){
					out[val.length-1]=(byte)(out[val.length-1]|-1);
				}
				for(int i=val.length; i<out.length; i++){
					out[i]=(byte)(255);
				}
			}
		}else{
			out[0]=(byte)(val[0]>>(8-leftShift));
			for(int i=1; i<val.length; i++){			
				byte lPiece=(byte)((val[i-1]<<leftShift)&(-1<<leftShift)), rPiece=(byte)((val[i]>>(8-leftShift))&(127>>(8-leftShift-1)));
				if(leftShift==8){
					lPiece=(byte)((val[i-1]<<leftShift)&0);
				}
				if((8-leftShift)==0){
					rPiece=(byte)((val[i]>>(8-leftShift))&-1);
				}
				out[i]=(byte)(lPiece|rPiece);
			}
			out[val.length]=(byte)(val[val.length-1]<<leftShift);
			if(shiftWithOne){
				out[val.length]=(byte)(out[val.length]|(1<<leftShift)-1);
				if(leftShift==0){
					out[val.length]=(byte)(out[val.length]|0);
				}else if(leftShift==8){
					out[val.length]=(byte)(out[val.length]|-1);
				}
				for(int i=val.length+1; i<out.length; i++){
					out[i]=(byte)255;
				}
			}
		}
		return new LargeInteger(out);
	}

	/**
	 * Computes the LargeInteger with bits shifted right by one
	 * @return This shifted right by one
	 */
	private LargeInteger shiftRight()
	{
		byte[] out=new byte[val.length];
		int pad=val[0]&1;
		out[0]=(byte)(val[0]>>1);
		for(int i=1; i<val.length; i++){
			byte mask=0;
			if(pad==1){
				mask=(byte)128;
			}
			out[i]=(byte)(val[i]>>1&127|mask); pad=val[i]&1;
		}
		return new LargeInteger(out);
	}
}