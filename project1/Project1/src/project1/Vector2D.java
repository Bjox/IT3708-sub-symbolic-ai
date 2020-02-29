package project1;

/**
 *
 * @author Bjørnar W. Alvestad
 */
public class Vector2D {
	
	/** Vector components. */
	public double x, y;
	
	/** Flag that specifies if this is a null vector. Will only be set if the empty constructor is used. */
	public final boolean nullVector;

	
	/**
	 * Creates a new vector.
	 * @param x
	 * @param y
	 * @param nullVector 
	 */
	public Vector2D(double x, double y, boolean nullVector) {
		this.x = x;
		this.y = y;
		this.nullVector = nullVector;
	}
	
	
	/**
	 * Creates a new vector.
	 * @param x
	 * @param y 
	 */
	public Vector2D(double x, double y) {
		this(x, y, false);
	}

	/**
	 * Creates a new vector initialized to (0,0). Null vector flag will be set.
	 */
	public Vector2D() {
		this(0, 0, true);
	}
	
	
	/**
	 * Copy a vector.
	 * @param orig 
	 */
	public Vector2D(Vector2D orig) {
		x = orig.x;
		y = orig.y;
		nullVector = orig.nullVector;
	}
	
	/**
	 * Add a vector to this vector.
	 * @param v 
	 */
	public Vector2D add(Vector2D v) {
		x += v.x;
		y += v.y;
		return this;
	}
	
	/**
	 * Add multiple vectors to this vector.
	 * @param vs
	 * @return 
	 */
	public Vector2D add(Vector2D... vs) {
		for (Vector2D v : vs) add(v);
		return this;
	}
	
	/**
	 * Subtract a vector from this vector.
	 * @param v
	 * @return 
	 */
	public Vector2D sub(Vector2D v) {
		x -= v.x;
		y -= v.y;
		return this;
	}
	
	/**
	 * Returns a new, scaled version of this vector.
	 * @param r
	 * @return 
	 */
	public Vector2D scaled(double r) {
		return new Vector2D(x * r, y * r);
	}
	
	/**
	 * Scale this vector.
	 * @param r 
	 */
	public Vector2D scale(double r) {
		x *= r;
		y *= r;
		return this;
	}
	
	/**
	 * Returns a new, negated version of this vector.
	 * @return 
	 */
	public Vector2D negated() {
		return new Vector2D(-x, -y);
	}
	
	/**
	 * Negate this vector.
	 * @return 
	 */
	public Vector2D negate() {
		x = -x;
		y = -y;
		return this;
	}
	
	/**
	 * Calculates the length of this vector.
	 * @return 
	 */
	public double length() {
		return Math.sqrt(x*x + y*y);
	}
	
	/**
	 * Normalize this vector.
	 * @return 
	 */
	public Vector2D normalize() {
		double len = length();
		x /= len;
		y /= len;
		return this;
	}

	/**
	 * Calculate the scalar product of two vectors.
	 * @param v
	 * @return 
	 */
	public double scalarProduct(Vector2D v) {
		return x*v.x + y*v.y;
	}
	
	/**
	 * Calculate the angle between two vectors.
	 * @param v
	 * @return 
	 */
	public double getVectorAngle(Vector2D v) {
		return Math.acos(scalarProduct(v) / (length() * v.length()));
	}
	
	
	public Vector2D getPerpendicularVec(boolean down) {
		Vector3D u = new Vector3D(x, y, 0);
		Vector3D v = new Vector3D(0, 0, down ? -1 : 1);
		
		return new Vector2D(
				u.y * v.z - u.z * v.y,
				u.z * v.x - u.x * v.z).normalize();
	}
	
	
	public Vector2D rotate90Deg(boolean dir) {
		double tmp = x;
		x = y;
		y = -tmp;
		
		if (dir) {
			x *= -1;
			y *= -1;
		}
		
		return this;
	}
	
	
	public Vector2D rotate(double rad) {
		double tmp = x;
		x = x * Math.cos(rad) - y * Math.sin(rad);
		y = y * Math.cos(rad) + tmp * Math.sin(rad);
		return this;
	}
	
	
	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}
	
	
	
	private class Vector3D {
		public double x, y, z;

		public Vector3D(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}
	
}
