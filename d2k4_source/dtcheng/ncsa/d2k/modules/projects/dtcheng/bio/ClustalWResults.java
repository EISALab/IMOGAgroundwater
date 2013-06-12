/*
 * Created on Oct 7, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package ncsa.d2k.modules.projects.dtcheng.bio;

import java.io.Serializable;

/**
 * @author redman
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class ClustalWResults implements Serializable {
	public String sequences;
	public String [] alignments;
	public String [] tree;
	public String summary;
	public String counts;
}
