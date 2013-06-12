package ncsa.d2k.modules.core.optimize.ga.nsga;

import java.io.Serializable;
import ncsa.d2k.modules.core.optimize.ga.*;

/**
 * this class manages teh pareto front. Items can be added to the front,
 * but not removed, as this does not seem necessary. We can also reset
 * all the counts.
 */
public class ParetoFront extends Object implements Serializable {
        int [][] paretoFront = null;
        int [] count = null;
        int frontCount = 0;
        /**
         * the current implementation will manage an array.
         */
        public ParetoFront (int popsize) {
                paretoFront = new int [popsize][];
                count = new int [popsize];
                this.reset ();
        }

        /**
         * returns the size of each front.
         * @returns the size of each front.
         */
        public int [] getCounts () {
                return count;
        }

        /**
         * returns the fronts
         * @returns the fronts.
         */
        public int [][] getFronts () {
                return paretoFront;
        }

        /**
         * returns the front at said index
         * @param index the index of the front to get.
         * @returns the front at index.
         */
        public int [] getFront (int which) {
                return paretoFront [which];
        }

        /**
         * reset all the counts to zero
         */
        void reset () {
                for (int i = 0 ; i < count.length ;i++)
                        count [i] = 0;
                frontCount = 0;
        }
        /**
         * Compress the array down to it's smallest possible size.
         */
        public void compress () {
                for (int i = 0 ; i < frontCount ;i++) {
                        int [] arry = new int [count [i]];
                        System.arraycopy(paretoFront [i], 0, arry, 0, count [i]);
                        paretoFront [i] = arry;
                }
        }

        /**
         * reset all the counts to zero
         */
        public String toString () {
                StringBuffer sb = new StringBuffer (1024);
                sb.append ("-----Pareto fronts----\n");
                for (int i = 0 ; i < frontCount ;i++) {
                        for (int j = 0 ; j < count[i] ; j++) {
                                if (j > 0)
                                        sb.append (',');
                                sb.append (paretoFront [i][j]);
                        }
                        sb.append ("\n");
                }
                return sb.toString();
        }

        /**
         * Make sure the array space for the front has been initialized.
         */
        public void addFront () {
                if (paretoFront [frontCount] == null)
                        paretoFront [frontCount] = new int [count.length];
                count [frontCount] = 0;
                frontCount++;
        }

        /**
         * Add another member to the front indexed by frontIndex.
         * @param frontIndex the index of the front to add the member to.
         * @param member the index of the member.
         */
        public void addMemberToFront (int frontIndex, int member) {
                paretoFront [frontIndex] [count [frontIndex]++] = member;
        }

        /**
         * Returns the number of individuals in the front at i.
         * @param i the index of the front.
         * @returns the number of individuals in front at i.
         */
         public int getFrontSize (int i) {
                return count [i];
        }

        /**
         * Returns the number of fronts.
         * @returns the number of fronts.
         */
         public int getNumFronts () {
                return frontCount;
        }
}
