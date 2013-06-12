//package ncsa.d2k.modules.projects.clutter.rdr;
package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.core.modules.*;
import java.io.*;

/**
 * Create an ARFF File Parser for the specified file.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class CreateARFFParser extends InputModule {

    public String[] getInputTypes() {
        String[] in = {"java.lang.String"};
        return in;
    }

    public String getInputInfo(int i) {
        return "The absolute path to an ARFF file.";
    }

    public String getInputName(int i) {
        return "File Name";
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.file.input.ARFFFileParser"};
        return out;
    }

    public String getOutputName(int i) {
        return "File Parser";
    }

    public String getOutputInfo(int i) {
        return "An ARFF File Parser.";
    }

    public String getModuleInfo() {
        StringBuffer s = new StringBuffer("<p>Overview: ");
        s.append("This module creates an ARFF File Parser for the specified file. ");

        s.append("</p><p>DetailedDescription: ");
        s.append("This module creates an ARFF File Parser that will ");
        s.append("read data from the specified ARFF file. ");
        s.append("An ARFF (Attribute-Relation File Format) file is an ASCII file ");
        s.append("that describes a list of instances sharing a set of attributes. ");
        s.append("ARFF files were developed by the Machine Learning Project at the ");
        s.append("Department of Computer Science of the University of Waikota for use ");
        s.append("with the Weka machine learning software. ");

        s.append("</p><p>Typically the <i>File Parser</i> output port of this ");
        s.append("module is connected to the <i>File Parser</i> input port of ");
        s.append("a module whose name begins with 'Parse File', for example, ");
        s.append("<i>Parse File To Table</i> or  <i>Parse File To Paging Table</i>.");

        s.append("</p><p>Limitations: The module is designed to read valid ARFF files, not ");
        s.append("to validate correctness.  Because of this, the parser performs only a ");
        s.append("minimal amount of data checking.  For example, it does not verify that ");
        s.append("the data instances contain acceptable attribute values. ");
        s.append("The module does not handle the sparse data representation introduced in ");
        s.append("recent versions of the ARFF format.");

        s.append("</p><p>References: <br>");
        s.append("The Weka Project: http://www.cs.waikato.ac.nz/~ml/weka <br>" );
        s.append("The ARFF Format: http://www.cs.waikato.ac.nz/~ml/weka/arff.html " );

        s.append("</p><p>Data Type Restrictions: ");
        s.append("The input to this module must be an ARFF file.");

        s.append("</p><p>Data Handling: ");
        s.append("The module does not destroy or modify the input data.</p>");

        return s.toString();
    }

    public String getModuleName() {
        return "Create ARFF File Parser";
    }

    public void doit() throws Exception {
        String fn = (String)pullInput(0);
        File file = null;

        try {
            file = new File(fn);
        }
        catch (NullPointerException e) {
           throw new FileNotFoundException( getAlias() +
                ": File Name was blank "  +
                "\n" + e );
        }

        boolean fileOk = false;
        try {
            fileOk = file.exists();
        }
        catch (SecurityException e) {
           throw new SecurityException( getAlias() +
                ": Could not open file: " + fn +
                "\n" + e );
        }
        if ( ! fileOk ) {
           throw new FileNotFoundException ( getAlias() +
                ": File does not exist: " + fn );
        }

        ARFFFileParser arff = new ARFFFileParser(file);
        pushOutput(arff, 0);
    }
}

// QA Comments
// 2/14/03 - Handed off to QA by David Clutter
// 2/12/03 - Ruth started QA process. added more to module description
//           module description; added more specific exceptions.
// 2/24/03   vered started second QA. added format check of input file name.
// 2/25/03 - ARFF doesn't require particular suffix, just convention, so
//           removed check.
//         - Added Limitations note in module info outlining what the parser
//           doesn't handle as expected.
//         - Changed exception for file not found to more std format.
//         - QA decided *NOT* to put in basic until missing values handled
//           properly.
// 3/03/03 - Missing values are handled - by ParseFileTo*, not by these.
//           Commit to basic.  WISH for validating parser at some point.
// END QA Comments
