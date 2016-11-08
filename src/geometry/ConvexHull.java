package geometry;

import java.util.Arrays;

import javax.vecmath.Vector3d;

public class ConvexHull {

	public static double cross(Vector3d O, Vector3d A, Vector3d B) {
		return (A.x - O.x) * (B.y - O.y) - (A.y - O.y) * (B.x - O.x);
	}

	/**
	 * Finds convex hull of given points. Works with a 3-d vector, however, 
	 * takes only the x and y dimensions into account in order to remain 
	 * consistent with the rest of the program. Might need some further 
	 * refactoring to improve the program's semantics.
	 * @param P
	 * @return
	 */
	public static Vector3d[] convex_hull(Vector3d[] P) {

		if (P.length > 1) {
			int n = P.length, k = 0;
			Vector3d[] H = new Vector3d[2 * n];

			Arrays.sort(P, new Vector3dComparator());

			// Build lower hull
			for (int i = 0; i < n; ++i) {
				while (k >= 2 && cross(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}

			// Build upper hull
			for (int i = n - 2, t = k + 1; i >= 0; i--) {
				while (k >= t && cross(H[k - 2], H[k - 1], P[i]) <= 0)
					k--;
				H[k++] = P[i];
			}
			if (k > 1) {
				H = Arrays.copyOfRange(H, 0, k - 1); // remove non-hull vertices after k; remove k - 1 which is a duplicate
			}
			return H;
		} else if (P.length <= 1) {
			return P;
		} else{
			return null;
		}
	}
}
