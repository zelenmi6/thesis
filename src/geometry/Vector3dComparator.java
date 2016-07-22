package geometry;

import java.util.Comparator;

import javax.vecmath.Vector3d;

public class Vector3dComparator implements Comparator {

	//!TODO algoritmus puvodne pracuje z inty. je potreba osetrit u double to,
	//!TODO co by jako rozdil u intu vysel 0. zde to muze byt 0.000000001 a 
	//!TODO vyjde to teday jako kladne
	public int compare(Object arg0, Object arg1) {
		Vector3d v1 = (Vector3d)arg0;
		Vector3d v2 = (Vector3d)arg1;
		if (v1.x == v2.x) {
			return (v1.y - v2.y) < 0 ? -1 : 1;
		} else {
			return (v1.x - v2.x) < 0 ? -1 : 1;
		}
	}

}
