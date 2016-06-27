package geometry;

import javax.vecmath.Vector3d;

public class Trapezoid {
	public Vector3d a, b, c, d;
	
	public Trapezoid(Vector3d a, Vector3d b, Vector3d c, Vector3d d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public Trapezoid(double ax, double ay, double az, double bx, double by, double bz, 
			double cx, double cy, double cz, double dx, double dy, double dz) {
		this.a = new Vector3d(ax, az, az);
		this.b = new Vector3d(bx, bz, bz);
		this.c = new Vector3d(cx, cz, cz);
		this.d = new Vector3d(dx, dz, dz);
	}
}
