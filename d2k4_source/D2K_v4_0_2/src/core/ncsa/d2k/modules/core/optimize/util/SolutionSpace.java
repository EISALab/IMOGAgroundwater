package ncsa.d2k.modules.core.optimize.util;
import ncsa.d2k.modules.core.datatype.table.*;
/*
	SolutionSpace


*/

public interface SolutionSpace {

	public void setRanges(Range[] paramRanges);

	public Range[] getRanges();

	public Solution[] getSolutions();

	public void setSolutions(Solution[] sols);

	public void createSolutions(int solutionCount);

	public Table getTable();

	public void computeStatistics();

	public String getSpaceDefinitionString();

	public String statusString();
}
