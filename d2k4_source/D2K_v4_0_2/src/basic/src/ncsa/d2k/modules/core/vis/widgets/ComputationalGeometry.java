package ncsa.d2k.modules.core.vis.widgets;

/**
 * <code>ComputationalGeometry</code> is intended to encapsulate useful
 * computational geometry calculations (such as that of taking the convex
 * hull of a set of points). As such methods are written, they should be
 * added to this class so that other modules can benefit from them.
 *
 * @author gpape
 */

import java.awt.geom.Point2D;
import java.lang.IllegalArgumentException;
import java.lang.StrictMath;
import java.util.Vector;

public class ComputationalGeometry {

   /**
    * Calculates the convex hull of a <code>Vector</code> of
    * <code>Point2D.Double</code> objects. Its behavior is not defined
    * if duplicate points exist, so take this into consideration before
    * calling this method.
    *
    * @param points     the <code>Point2D.Double</code> objects to be
    *                   considered
    *
    * @return           a <code>Vector</code> array of two elements. The
    *                   first element contains all points found in the
    *                   convex hull; the second contains all points not
    *                   found in the hull
    *
    * @throws IllegalArgumentException      if <code>points</code> contains
    *                                       less than three points
    */
   public static Vector[] convexHull(Vector points)
      throws IllegalArgumentException {

      if (points.size() < 3)
         throw new IllegalArgumentException
         ("The convex hull is not defined for a set of less than 3 points.");

      Vector[] V = new Vector[2];
      Vector inHull = new Vector(), notInHull = new Vector();

      V[0] = inHull;
      V[1] = notInHull;

      /* the convex hull is found via Jarvis' March, also known as the      */
      /* "gift-wrapping" algorithm. the first step in this algorithm is to  */
      /* find the "lowest" point in the set (that with the least            */
      /* y-coordinate). this point is always in the convex hull.            */

      Point2D.Double lowest = (Point2D.Double)points.get(0);

      Point2D.Double candidate;
      for (int i = 1; i < points.size(); i++) {

         candidate = (Point2D.Double)points.get(i);
         if ( (candidate.y < lowest.y) || // least x-coordinate wins a tie
              (candidate.y == lowest.y && candidate.x < lowest.x) ) {

            notInHull.add(lowest);
            lowest = candidate;

         }
         else
            notInHull.add(candidate);

      }

      inHull.add(lowest);

      /* the next step is to find the point with the least polar angle with */
      /* respect to the lowest point. this point is also in the hull.       */

      /* an important note: in ordinary convex hull calculations, one       */
      /* breaks polar angle ties by choosing the furthest point. some       */
      /* applications, however, might project three-dimensional points onto */
      /* two dimensions before calling this method. we need to break ties   */
      /* by choosing the _nearest_ point to accommodate these applications.  */

      Point2D.Double next = (Point2D.Double)notInHull.get(0);
      double least_angle = polarAngle(lowest, next), candidate_angle;

      int point_index = 0;
      for (int i = 1; i < notInHull.size(); i++) {

         candidate = (Point2D.Double)notInHull.get(i);
         candidate_angle = polarAngle(lowest, candidate);

         if ( (candidate_angle < least_angle) ||
              ( (candidate_angle == least_angle) &&
                (lowest.distance(candidate) < lowest.distance(next)) ) ) {

            least_angle = candidate_angle;
            next = candidate;
            point_index = i;

         }

      }

      inHull.add(notInHull.remove(point_index));

      /* we add the lowest point to the list of points not in the hull.     */
      /* it really _is_ in the hull, of course, but now we'll know that     */
      /* we're finished when we encounter it again in that list.            */

      notInHull.add(lowest);

      /* ...and recurse. we require the polar angle between points on the   */
      /* convex hull to be strictly increasing, which allows us to build    */
      /* the hull in one pass (the canonical Jarvis' March requires two).   */

      Point2D.Double current = next;
      double best_angle, best_distance;

      while (true) { // one iteration through the points

         // set variables...
         point_index = 0;
         best_angle = Double.MAX_VALUE;
         best_distance = Double.MAX_VALUE;

         // ...and iterate over all points not in the hull, searching for
         // that point which has the least polar angle with respect to the
         // last point added. this angle must be strictly increasing over
         // all iterations!
         for (int i = 0; i < notInHull.size(); i++) {

            candidate = (Point2D.Double)notInHull.get(i);
            candidate_angle = polarAngle(current, candidate);

            if ( (candidate_angle >= least_angle) &&
                 ( (candidate_angle < best_angle) ||
                     ( (candidate_angle == best_angle) &&
                       (current.distance(candidate) < best_distance) ) ) ) {

               best_angle = candidate_angle;
               best_distance = current.distance(candidate);
               next = candidate;
               point_index = i;

            }

         }

         least_angle = polarAngle(current, next);

         if (next == lowest || notInHull.size() == 0) {
            notInHull.remove(point_index);
            break;
         }
         else {
            current = next;
            inHull.add(notInHull.remove(point_index));
         }

      }

      return V;

   }

   /**
    * Triangulates a single convex polygon with a convex hole. Note carefully
    * that each set of points (both inner and outer hulls) passed to this
    * method must have two important properties: first, that the point with
    * the least y-coordinate is first in the set; second, that the points
    * are sorted in counterclockwise order. These requirements are satisfied
    * for sets returned from the <code>convexHull</code> method.
    *
    * @param outer      a <code>Vector</code> of <code>Point2D.Double</code>
    *                   objects specifying the hull of the polygon
    * @param inner      a similar <code>Vector</code> specifying the hole in
    *                   the polygon.
    *
    * @returns          a <code>Vector</code> of <code>Point2D.Double</code>
    *                   objects. Each group of three points defines a triangle
    *
    * @throws IllegalArgumentException      if <code>outer</code> contains
    *                                       than 3 points or if
    *                                       <code>inner</code> contains none
    */
   public static Vector holeTriangulation(Vector outer, Vector inner)
      throws IllegalArgumentException {

      if (outer == null || inner == null)
         throw new IllegalArgumentException();
      if (outer.size() < 3)
         throw new IllegalArgumentException();

      Vector V = new Vector();
      Point2D.Double candidate = new Point2D.Double();

      switch(inner.size()) {

         case (0):

            for (int i = 1; i < outer.size(); i++) {
               V.add(outer.get(0));
               V.add(outer.get(i));
               V.add(outer.get((i + 1) % outer.size()));
            }

            break;

         case (1):

            for (int i = 0; i < outer.size() - 1; i++) {
               V.add(outer.get(i));
               V.add(outer.get(i + 1));
               V.add(inner.get(0));
            }
            V.add(outer.get(outer.size() - 1));
            V.add(outer.get(0));
            V.add(inner.get(0));

            break;

         case (2):

            Point2D.Double P = (Point2D.Double)inner.get(0),
                           Q = (Point2D.Double)inner.get(1),
                           D1 = new Point2D.Double(), // discontinuities;
                           D2 = new Point2D.Double(); // there are always two

            boolean closerToP = false, foundFirstD = false;

            candidate = (Point2D.Double)outer.get(0);
            if (candidate.distance(P) < candidate.distance(Q))
               closerToP = true;

            V.add(outer.get(0));
            V.add(outer.get(1));
            if (closerToP)
               V.add(P);
            else
               V.add(Q);

            outer.add(outer.get(0));
            for (int i = 1; i < outer.size() - 1; i++) {

               candidate = (Point2D.Double)outer.get(i);

               if (closerToP) {

                  if (candidate.distance(Q) <= candidate.distance(P)) {

                     closerToP = false;
                     if (foundFirstD)
                        D2 = candidate;
                     else
                        D1 = candidate;
                     foundFirstD = true;

                     V.add(candidate);
                     V.add(outer.get(i + 1));
                     V.add(Q);

                  }
                  else {

                     V.add(candidate);
                     V.add(outer.get(i + 1));
                     V.add(P);

                  }

               }
               else {

                  if (candidate.distance(P) <= candidate.distance(Q)) {

                     closerToP = true;
                     if (foundFirstD)
                        D2 = candidate;
                     else
                        D1 = candidate;
                     foundFirstD = true;

                     V.add(candidate);
                     V.add(outer.get(i + 1));
                     V.add(P);

                  }
                  else {

                     V.add(candidate);
                     V.add(outer.get(i + 1));
                     V.add(Q);

                  }

               }

            }

            V.add(P);
            V.add(Q);
            V.add(D1);

            V.add(P);
            V.add(Q);
            V.add(D2);

            break;

         default: // >= 3 points

            int inner_index = 0;

            /* initially, take the first point on the outer hull and find   */
            /* nearest neighbor in the inner hull.                          */

            Point2D.Double first = (Point2D.Double)outer.get(0),
                           nearest = new Point2D.Double();

            double least_distance = Double.MAX_VALUE;

            for (int i = 0; i < inner.size(); i++) {

               candidate = (Point2D.Double)inner.get(i);

               if (candidate.distance(first) < least_distance) {
                  least_distance = candidate.distance(first);
                  inner_index = i;
                  nearest = candidate;
               }

            }

            /* for convenience, we reorder the points of the inner hull so  */
            /* that this nearest point is the first (maintaining            */
            /* counter-clockwise order).                                    */

            for (int i = 0; i < inner_index; i++)
               inner.add(inner.remove(0));

            /* now we must find the point on the outer hull with the        */
            /* largest y coordinate; we later invert polar angle            */
            /* calculations upon reaching it.                               */

            Point2D.Double highest = (Point2D.Double)outer.get(1);
            for (int i = 2; i < outer.size(); i++) {
               candidate = (Point2D.Double)outer.get(i);
               if ( (candidate.y > highest.y) ||
                    (candidate.y == highest.y) && (candidate.x > highest.x) )
                  highest = candidate;
            }

            /* 0 to highest */

            inner_index = 0; double pr, ps;
            Point2D.Double R, S; R = S = new Point2D.Double();
            int outer_index = 0;
            for (int i = 0; i < outer.size(); i++) {

               outer_index = i;

               P = (Point2D.Double)outer.get(i);

               if (P == highest)
                  break;

               Q = (Point2D.Double)outer.get((i + 1) % outer.size());
               R = (Point2D.Double)inner.get(inner_index % inner.size());
               S = (Point2D.Double)inner.get((inner_index + 1) % inner.size());

               pr = polarAngle(P, R);
               ps = polarAngle(P, S);

               if (ps < pr) {
                  if (R != nearest) {
                     V.add(P); V.add(R); V.add(S);
                  }
                  inner_index++;
                  i--;
               }
               else {
                  V.add(P); V.add(Q); V.add(R);
               }

            }

            /* highest to 0 . note that we must invert the polar angle      */
            /* calculations now.                                            */

            for (int i = outer_index; i < outer.size(); i++) {

               P = (Point2D.Double)outer.get(i);
               Q = (Point2D.Double)outer.get((i + 1) % outer.size());
               R = (Point2D.Double)inner.get(inner_index % inner.size());
               S = (Point2D.Double)inner.get((inner_index + 1) % inner.size());

               pr = polarAngle(R, P);
               ps = polarAngle(S, P);

               if (ps < pr) {
                  V.add(P); V.add(R); V.add(S);
                  inner_index++;
                  i--;
               }
               else {
                  V.add(P); V.add(Q); V.add(R);
               }

            }

            /* one potentially troublesome patch left, so we clean up the   */
            /* loose ends separately.                                       */

            P = (Point2D.Double)outer.get(0);
            while (inner_index < inner.size()) {

               R = (Point2D.Double)inner.get(inner_index % inner.size());
               S = (Point2D.Double)inner.get((inner_index + 1) % inner.size());

               V.add(P); V.add(R); V.add(S);
               inner_index++;

            }

            ps = 0; pr = 0;
            if (nearest == R) {

               pr = polarAngle(P, R);
               ps = polarAngle(P, S);

               int i = 1;
               while (pr < ps) {

                  P = (Point2D.Double)outer.get(i++ % outer.size());

                  pr = polarAngle(P, R);
                  ps = polarAngle(P, S);

               }

               V.add(P); V.add(R); V.add(S);

            }
            else if (nearest == S) {

               R = S;
               S = (Point2D.Double)inner.get(inner_index % inner.size());

               pr = polarAngle(P, R);
               ps = polarAngle(P, S);

               int i = 1;
               while (pr < ps) {

                  P = (Point2D.Double)outer.get(i++ % outer.size());

                  pr = polarAngle(P, R);
                  ps = polarAngle(P, S);

               }

               V.add(P); V.add(R); V.add(S);

            }
            if (R == S) {

               S = (Point2D.Double)inner.get((inner_index + 1) % inner.size());

               pr = polarAngle(P, R);
               ps = polarAngle(P, S);

               int i = 1;
               while (pr < ps) {

                  P = (Point2D.Double)outer.get(i++ % outer.size());

                  pr = polarAngle(P, R);
                  ps = polarAngle(P, S);

               }

               V.add(P); V.add(R); V.add(S);

            }

            break;

      }

      return V;

   }

   /**
    * Calculates the polar angle (in radians) of one
    * <code>Point2D.Double</code> object with respect to another in the
    * standard two-dimensional Cartesian plane.
    *
    * @param p          the reference point
    * @param q          the point for which the polar angle relative to the
    *                   reference point is to be determined
    *
    * @return           the polar angle of q relative to p
    */
   public static double polarAngle(Point2D.Double p, Point2D.Double q) {

      double dx = q.x - p.x,
             dy = q.y - p.y;

      if (dx == 0)                       // accounting for the special
         if (dy > 0)                     // cases in StrictMath's atan2
            return StrictMath.PI / 2;    // function
         else
            return StrictMath.PI * 1.5;
      if (dy == 0)
         if (dx > 0)
            return 0;
         else
            return StrictMath.PI;

      double angle = StrictMath.atan2(dy, dx);

      if (angle < 0)
         angle += (StrictMath.PI * 2);

      return angle;

   }

}
