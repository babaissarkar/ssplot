package math.prim;

/**
 * @author subhraman
 *
 */
public class Frac implements MathObject<Frac> {
	public int num = 0, den = 1, intg = 0;
	
	public Frac(int a) {
		intg = a;
		num = 0;
		den = 1;
		convertToImproper();
	}
	
	public Frac(int[] frac) {
		if (frac.length == 2) {
			num = frac[0];
			den = frac[1];
		} else if (frac.length > 2) {
			intg = frac[0];
			num = frac[1];
			den = frac[2];
		} else {
			throw new RuntimeException("Number of elements in int[] < 2");
		}
		convertToImproper();
	}

	public Frac(int n, int d) {
		this.num = n;
		this.den = d;
		convertToImproper();
	}
	
	public Frac(int intg, int n, int d) {
		this.intg = intg;
		this.num = n;
		this.den = d;
		convertToImproper();
	}
	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		int[][] a = {{1, 4, 7}, {1, 4, 5}};
		Frac[] af = Frac.parseFracArray(a);
		System.out.println(af[0].toString());
		//System.out.println(af[1].toString());
		System.out.println(af[0].times(af[1]).toString());
	}*/
	
	/**
	 * @param a
	 * @param b
	 * @return
	 * Finds the GCD by
	 * Euclidian algorithm.
	 */
	public static int getGCD(int a, int b) {
		int gcd;
		int r1, r2;
		if (b != 0) {
			r1 = a % b;
		} else {
			r1 = 0;
		}
		if (a != 0) {
			r2 = b % a;
		} else {
			r2 = 0;
		}
		while((r1 != 0) && (r2 != 0)) {
			int rr1 = r1 % r2;
			int rr2 = r2 % r1;
			r1 = rr1;
			r2 = rr2;
		}
		if ((r1 == 0) && (r2 != 0)) {
			gcd = r2;
		} else if ((r1 == 0) && (r2 == 0)) {
			gcd = a; //r1 = r2 = 0 implies a = b = gcd
		} else {
			gcd = r1;
		}
		
		if (gcd == 0) {
			gcd = 1;
		}
		return gcd;
	}
	
	public static int getLCM(int a, int b) {
		int gcd;
		gcd = Frac.getGCD(Math.abs(a), Math.abs(b));
		int lcm;
		lcm = (a * b) / gcd;
		return lcm;
	}
	
	/**
	 * 
	 */
	private void convertToImproper() {
		/*if (num > den) {
			int rem = num % den;
			intg = intg + (num - rem) / den;
			num = rem;
		} else if ((num < den) && (Math.abs(num) > den)) {
			int rem = (-num) % den;
			int intpart = -((-num) - rem) / den;
			intg = intg + intpart;
			num = rem;
		}*/
		num = num + intg * den;
		intg = 0;
	}
	
	public Frac add(Frac b) {
		int[] res = new int[3];
		int den = Frac.getLCM(this.den, b.den);
		int num = (this.num * (den / this.den)) + (b.num * (den / b.den));
		int intg = this.intg + b.intg;
		res[0] = intg;
		res[1] = num;
		res[2] = den;
		return new Frac(res);
	}
	
	public Frac subtract(Frac b) {
		return this.add(b.times(-1));
	}
	
	public Frac times(int a) {
		int[] res = new int[2];
		res[0] = (this.intg * this.den + this.num) * a;
		res[1] = this.den;
		return new Frac(res);
	}
	
	public Frac times(Frac a) {
		int[] res = new int[2];
		int[] buff = new int[2];
		this.num = this.num + this.intg * this.den;
		this.intg = 0;
		a.num = a.num + a.intg * a.den;
		a.intg = 0;
		buff[0] = this.num * a.num;
		buff[1] = this.den * a.den;
		int gcd = getGCD(buff[0], buff[1]);
		res[0] = buff[0] / gcd;
		res[1] = buff[1] / gcd;
		return new Frac(res);
	}

    
    public Frac multiply(Frac a) {
        return this.times(a);
    }
    
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (intg != 0) {
			builder.append(intg);
			builder.append("+");
		}
		builder.append(num);
		if ((den != 1) && (num != 0)) {
			builder.append("/");
			builder.append(den);
		}
		return builder.toString();
	}
	
	public static Frac[] parseFracArray(int[][] arr) {
		Frac[] fracs = new Frac[arr.length];
		for (int i = 0; i < arr.length; i++) {
			int[] fracData = arr[i];
			fracs[i] = new Frac(fracData);
		}
		return fracs;
	}
	
	public static Frac[] parseFracArray(int[] arr) {
		Frac[] fracs = new Frac[arr.length];
		for (int i = 0; i < arr.length; i++) {
			fracs[i] = new Frac(arr[i]);
		}
		return fracs;
	}
	
//	Problematic :
//	public static double getGCD(double a, double b) {
//		changeDecToInt(a, b);
//		int c = (int) a;
//		int d = (int) b;
//		return Frac.getGCD(c, d);
//	}
//	
//	private static int[] changeDecToInt(double n, double d) {
//		String num = Double.toString(n);
//		String den = Double.toString(d);
//		int[] i = new int[2];
//		i[0] = Integer.parseInt(num.substring(num.lastIndexOf(".") + 1, num.length()));
//		i[1] = Integer.parseInt(den.substring(den.lastIndexOf(".") + 1, den.length()));
//		return i;
//	}

}
