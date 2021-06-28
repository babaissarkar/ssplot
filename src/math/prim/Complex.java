package math.prim;

/**
 * @author Subhraman Sarkar
 *
 */
public class Complex implements MathObject<Complex> {
	public double a, b; /* a = real part, b = imaginary part. So the number is a + ib */
	
	public Complex(double a, double b) {
		this.a = a;
		this.b = b;
	}
	
	public Complex(double a) {
		/* Just a real number */
		this.a = a;
		this.b = 0.0;
	}
	
	public Complex() {
		this(0.0, 0.0);
	}
	
	public static Complex fromPolar(double r, double theta) {
		Complex z = new Complex();
		z.a = r * Math.cos(theta);
		z.b = r * Math.sin(theta);
		return z;
	}
	
	public Complex add(Complex z0) {
		Complex z;
		z = new Complex(z0.a + this.a, z0.b + this.b);
		return z;
	}

    public Complex times(double k) {
		Complex z = new Complex();
		z.a = this.a * k;
		z.b = this.b * k;
		return z;
	}
	
	public Complex times(Complex z0) {
		Complex z = new Complex();
		z.a = (this.a * z0.a) - (this.b * z0.b);
		z.b = (this.a * z0.b) + (this.b * z0.a);
		return z;
	}

    /* Convenience method. */
    public Complex multiply(Complex z0) {
        return this.times(z0);
    }
	
	public double modulus() {
		double mod = Math.sqrt(this.a * this.a + this.b * this.b);
		return mod;
	}
	
	public double argument() {
		return Math.atan2(this.b, this.a);
	}
	
	public Complex invert() {
		Complex z = new Complex();
		double m = this.modulus() * this.modulus();
		z.a = this.a / m;
		z.b = (-this.b) / m;
		return z;
	}
	
	public Complex subtract(Complex z0) {
		Complex z = new Complex();
		//z = this.add(z0.times(-1));
		z.a = this.a - z0.a;
		z.b = this.b - z0.b;
		return z;
	}
	
	public Complex divide(Complex z0) {
		Complex z = this.times(z0.invert());
		return z;
	}
	
	public double realPart() {
		return this.a;
	}
	
	public double imagPart() {
		return this.b;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.a);
		if (this.b < 0) {
		    sb.append(" - ");
		} else {
			sb.append(" + ");
		}
		sb.append(Math.abs(this.b));
		sb.append("i");
		return sb.toString();
	}
	
	public static void main(String... args) {
		Complex z1 = new Complex(5, 5);
		System.out.println(z1.toString());
		Complex z2 = new Complex(5, -6);
		System.out.println(z2.toString());
		System.out.println(z2.times(z1).toString());
		System.out.format("%3.6f\n", z1.modulus());
		System.out.format("%3.6f\n", Math.toDegrees(z1.argument()));
		
		System.out.println(z2.invert().toString());
		System.out.println(z1.divide(z2).toString());
	}

}
