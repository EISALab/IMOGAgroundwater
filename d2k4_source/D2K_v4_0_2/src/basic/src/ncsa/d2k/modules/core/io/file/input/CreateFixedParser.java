package ncsa.d2k.modules.core.io.file.input;

import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.core.modules.*;
import java.io.*;


/**
 * Create a new fixed file reader.
 */
public class CreateFixedParser extends InputModule {

    public String[] getInputTypes() {
        String[] in = { "java.lang.String",
												"ncsa.d2k.modules.core.datatype.table.Table"};
        return in;
    }

    public String[] getOutputTypes() {
        String[] out = {"ncsa.d2k.modules.core.io.file.input.FixedFileParser"};
        return out;
    }

    public String getInputInfo(int i) {
        switch(i) {
            case(0):
                return "The absolute path to the fixed file.";
            case(1):
                return "A table of metadata describing the fixed file.";
            default:
                return "";
        }
    }

    public String getInputName(int i) {
        switch(i) {
            case(0):
                return "File Name";
            case(1):
                return "Metadata Table";
            default:
                return "";
        }
    }

    public String getOutputInfo(int i) {
        return "A Fixed File Parser for the specified fixed-format file.";
    }

    public String getOutputName(int i) {
        return "Fixed File Parser";
    }

    public String getModuleInfo() {
        StringBuffer sb = new StringBuffer("<p>Overview: ");
        sb.append("This module creates a parser for a fixed-format file. ");

        sb.append("</p><p>Detailed Description: ");
        sb.append("Given a file name and a table of metadata ");
        sb.append("describing the file's layout, this module creates a ");
        sb.append("parser for the file, which must have fixed-length fields. This parser can then be passed to ");
        sb.append("<i>Parse File To Table</i>, for example, to read the file into a Table. ");
        sb.append("Many databases offer the option of writing their contents to fixed-format files. ");
        sb.append("</p>");

        sb.append("<p>");
        sb.append("The table of metadata has up to five columns labeled ");
        sb.append("LABEL, TYPE, START, STOP, and LENGTH, not necessarily in that order. ");
	sb.append("Each row in the table, other than the labels row and an optional data type row, ");
	sb.append("provides information about one attribute (field) that is to be loaded from the fixed-format file. ");
        sb.append("</p>");

        sb.append("<p>");
        sb.append("The LABEL entry, which is optional, contains the label to assign to the attribute.  ");
        sb.append("If the table does not contain a LABEL column, D2K automatically assigns a label to the attribute. ");

        sb.append("</p><p>" );
        sb.append("The TYPE entry specifies the datatype of the attribute.  If no TYPE appears, D2K tries to determine the ");
        sb.append("attribute type automatically. ");

        sb.append("</p><p>" );
        sb.append("The START entry must be present, and gives the starting position of the attribute value in the ");
        sb.append("rows of the fixed-format file. The first position in a row is position 1. ");
        sb.append("Either STOP or LENGTH must be present in the <i>Metadata Table</i>. ");
        sb.append("If both appear in the table, STOP is ignored and LENGTH is used. ");
        sb.append("The STOP entry gives the stopping position of the attribute value in the rows of the fixed-format file. ");
        sb.append("The LENGTH entry specifies the number of positions taken by the attribute ");
	sb.append("value in the rows of the fixed-format file. ");
        sb.append("START and STOP positions are inclusive, hence an attribute with one character will have equal START and STOP ");
        sb.append("entries. ");
        sb.append("</p>");

        sb.append("<p>");
        sb.append("The <i>Metadata Table</i> need not include rows describing all of the fields in the fixed-format file. ");
        sb.append("Only the attributes (fields) that are described by the metadata table will be read by the parser. ");
	sb.append("Typically, the <i>Metadata Table</i> is itself loaded from a file into a <i>Table</i> ");
        sb.append("using D2K modules such as <i>Create Delimited File Parser</i> and <i>Parse File To Table</i> .");

        sb.append("</p><p>Data Type Restrictions: ");
        sb.append("The specified file must be in fixed-format. ");

        sb.append("</p><p>Data Handling: ");
        sb.append("This module does not destroy or modify its input data.");
        sb.append("</p>");

        return sb.toString();
    }

    public String getModuleName() {
       return "Create Fixed-Format Parser";
    }

    public void doit() throws Exception {
        String fileName = (String)pullInput(0);
        Table meta = (Table)pullInput(1);

        File file = new File(fileName);
        if(!file.exists())
            throw new FileNotFoundException(file+" did not exist.");

        FixedFileParser ff = new FixedFileParser(file, meta);
        pushOutput(ff, 0);
    }


}
// QA Comments
// 2/14/03 - Handed off to QA by David Clutter
// 2/16/03 - Anca started QA process. Added fixed format description.
// 2/18/03 - checked into basic.
// 4/16/03 - Ruth updated info to provide more details on what the metadata
//           table columns were used for.  It wasn't clear to her from reading
//           without an example Fixed File and Metadata table how to create one.
// END QA Comments
