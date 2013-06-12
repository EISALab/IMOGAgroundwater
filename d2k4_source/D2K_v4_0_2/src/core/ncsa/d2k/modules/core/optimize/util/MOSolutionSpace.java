package ncsa.d2k.modules.core.optimize.util;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.datatype.table.basic.*;


public class MOSolutionSpace implements SolutionSpace, java.io.Serializable{

        protected Range[] ranges;

        protected ObjectiveConstraints[] objConstraints;

        protected MOSolution[] solutions;

        ////////////////////////
        ///Statistics Fields
        //////////////////////////
        /* the indices of the best solutions, one per objective*/
        int[] bestSolutions;
        /* the indices of the worst solutions, one per objective*/
        int[] worstSolutions;

        /*the average value for each objective for the entire space*/
        double[] averageScores;

        /////////////////////////
        //constructors
        ///////////////////////

        public  MOSolutionSpace(Range[] rgs, ObjectiveConstraints[] ocs){
                ranges=rgs;
                objConstraints=ocs;

                int objCount=ocs.length;
                bestSolutions=new int[objCount];
                worstSolutions=new int[objCount];
                averageScores=new double[objCount];
        }

        ////////////////////
        //utility functions
        ////////////////////
        /*
                initiallizes this solution space's solution array to hold
                the desired number of solutions
        */

        public void createSolutions(int solutionCount){
                solutions=new MOSolution[solutionCount];

                //first, find out what type of solutions to make by checking if
                //every range is of the same type, and if not, making a mixedsolution
                boolean typeFound=false;
                int type=0;
                while(!typeFound&&(type<4)){
                        int tally=0;
                        for(int r=0; r<ranges.length; r++){
                                if(ranges[r].getType()==type)
                                        tally++;
                        }
                        if(tally==ranges.length){
                                typeFound=true;
                        }else{
                                type++;
                        }
                }
                //now make the appropriate solutions
                switch(type){
                        case(Range.INTEGER):{
                                for(int i=0; i<solutionCount; i++){
                                        solutions[i]=new MOIntSolution((IntRange[])ranges, objConstraints);
                                }
                                 break;
                        }
                        case(Range.DOUBLE):{
                                for(int i=0; i<solutionCount; i++){
                                        solutions[i]=new MODoubleSolution((DoubleRange[])ranges, objConstraints);
                                }
                                 break;
                        }
                        case(Range.BINARY):{
                                for(int i=0; i<solutionCount; i++){
                                        solutions[i]=new MOBinarySolution((BinaryRange[])ranges, objConstraints);
                                }
                                 break;
                        }
                        default:{//must be mixed range types
                                for(int i=0; i<solutionCount; i++){
                                        solutions[i]=new MOMixedSolution((Range[])ranges, objConstraints);
                                }
                        }
                }

        }
        /**
         * compute statistics that can be used to measure the success of
         * the population.
         */
        public void computeStatistics () {

                int solCount = solutions.length;
                int objCount=objConstraints.length;

                // save the best, worst individuals and the avg performance.
                double[] bestScores=new double[objCount];
                double[] worstScores=new double[objCount];

                //initialize
                for(int i=0; i<objCount; i++){
                        bestScores[i]=solutions[0].getObjective(i);
                        worstScores[i]=solutions[0].getObjective(i);
                        bestSolutions[i]=0;
                        worstSolutions[i]=0;
                        averageScores[i]=0.0;
                }

                for(int j=1; j<objCount; j++){
                        for (int i = 1; i < solCount; i++) {
                                double currentScore=solutions[i].getObjective(j);
                                // update current statistics
                                averageScores[j]+=currentScore;

                                // Compare to best performer.
                                if (objConstraints[j].compare(bestScores[j],
                                                currentScore) < 0){
                                        bestScores[j] = currentScore;
                                        bestSolutions[j]=i;
                                }

                                // compare to worst performer.
                                if (objConstraints[j].compare (worstScores[j],
                                                currentScore)  > 0){
                                        worstSolutions[j] = i;
                                        worstScores[j]=currentScore;
                                }
                        }
                        averageScores[j]/=solCount;
                }

        }

        public Table getTable(){
                Table vt;

                int rowCount=solutions.length;
                int colCount=ranges.length+objConstraints.length;

                Column[] cols=new Column[colCount];
                int colIndex=0;
                //make a column for each range
                for(int i=0; i<ranges.length; i++){
                        colIndex=i;
                        Column c;

                        if(ranges[i] instanceof DoubleRange){
                                c=new DoubleColumn(rowCount);
                                for(int j=0; j<rowCount; j++){
                                        double d=solutions[j].getDoubleParameter(i);
                                        ((DoubleColumn)c).setDouble(d, j);
                                }
                        }else if(ranges[i] instanceof IntRange){
                                c=new IntColumn(rowCount);
                                for(int j=0; j<rowCount; j++){
                                        int in=(int)solutions[j].getDoubleParameter(i);
                                        ((IntColumn)c).setInt(in, j);
                                }
                        }else if(ranges[i] instanceof BinaryRange){
                                c=new BooleanColumn(rowCount);
                                for(int j=0; j<rowCount; j++){
                                        boolean b=(solutions[j].getDoubleParameter(i)>0);
                                        ((BooleanColumn)c).setBoolean(b, j);
                                }
                        }else{//i guess default to a double column
                                c=new DoubleColumn(rowCount);
                                for(int j=0; j<rowCount; j++){
                                        double d=solutions[j].getDoubleParameter(i);
                                        ((DoubleColumn)c).setDouble(d, j);
                                }
                        }

                        c.setLabel(ranges[i].getName());
                        cols[i]=c;
                }
                //now the objectives
                for(int j=colIndex+1; j<colCount; j++){
                        DoubleColumn objC=new DoubleColumn(rowCount);
                        int currentObjectiveIndex=j-colIndex-1;
                        objC.setLabel(objConstraints[currentObjectiveIndex].getName());
                        for(int k=0; k<rowCount; k++){
                                objC.setDouble(solutions[k].getObjective(currentObjectiveIndex), k);
                        }
                        cols[j]=objC;
                }

                vt= new MutableTableImpl(cols);
                return vt;

        }

        ///////////////////////////
        ///print/status functions
        ////////////////////////////
        public String getSpaceDefinitionString(){
                StringBuffer sb=new StringBuffer();

                sb.append("\nRanges:\n");
                sb.append("\t_Name_\t_Min_\t_Max_\n");
                for(int i=0; i<ranges.length; i++){
                        sb.append("\t");
                        sb.append(ranges[i].getName());
                        sb.append("\t");
                        sb.append(ranges[i].getMin());
                        sb.append("\t");
                        sb.append(ranges[i].getMax());
                        sb.append("\n");
                }
                sb.append("\nObjective Constraint:\n");
                sb.append("\t_Name_\t_Is Maximizing?_\n");
                for(int j=0; j<objConstraints.length; j++){
                        sb.append("\t");
                        sb.append(objConstraints[j].getName());
                        sb.append("\t");
                        sb.append(objConstraints[j].isMaximizing());
                        sb.append("\n");
                }

                return(sb.toString());

        }

        public String statusString(){
                return "MOSolutionSpace Status";
        }
        ////////////////////////////
        ///get/set methods for fields
        ////////////////////////////
        public void setRanges(Range[] paramRanges){
                ranges=paramRanges;
                //b/c the old solutions are no longer valid
                solutions=null;
        }
        public Range[] getRanges(){
                return ranges;
        }

        public void setObjectiveConstraints(ObjectiveConstraints[] ocs){
                objConstraints=ocs;
                int objCount=ocs.length;

                worstSolutions=new int[objCount];
                bestSolutions=new int[objCount];
                averageScores=new double[objCount];
                //b/c the old solutions are no longer valid
                solutions=null;

        }

        public ObjectiveConstraints[] getObjectiveConstraints(){
                return objConstraints;
        }

        public void setSolutions(Solution[] sols){
                solutions=(MOSolution[])sols;
        }
        public Solution[] getSolutions(){
                return solutions;
        }



}
