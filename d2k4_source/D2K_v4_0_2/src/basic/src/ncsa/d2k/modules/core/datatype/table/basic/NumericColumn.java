package ncsa.d2k.modules.core.datatype.table.basic;


/**
	NumericColumn is an extension of Column which is implemented by all
	classes which are optimized to represent and access numeric data.
	<br>
*/
public interface NumericColumn extends Column {

	//static final long serialVersionUID = -8231597566891195839L;
	static final long serialVersionUID = -4942612002109477988L;

    //////////////////////////////////////
    //// Accessing Metadata
    /**
       Get the minimum value contained in this list
       @return the minimum value of this list
    */
    public double getMin();

    /**
       Get the maximum value contained in this list
       @return the maximum value of this list
    */
    public double getMax();

    /**
       Sets the value which indicates an empty entry.
       @param emptyVal the value to which an empty entry is set
    /
    public void setEmptyValue( Number emptyVal );

    /**
       Gets the value which indicates an empty entry.
       @return the value of an empty entry as a subclass of Number
    /
    public Number getEmptyValue( );
	*/
}/*NumericColumn*/
