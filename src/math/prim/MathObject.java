package math.prim;

/** Any object can be used as an element of the GenMatrix class,
 *  if it implements this interface, with T replaced by that class.
 *  For an example of its implementation, see Complex.java. */

interface MathObject<T> {
    
    public T add(T m);
    public T subtract(T m);
    public T multiply(T m);

}
