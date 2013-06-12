package ncsa.d2k.modules.core.optimize.ga.emo.functions;

import ncsa.d2k.modules.core.optimize.ga.*;
import ncsa.d2k.modules.core.datatype.table.*;
import ncsa.d2k.modules.core.optimize.util.*;

import java.util.*;
import java.io.*;

/**
 * A function that is calculated using an executable.  To use the executable,
 * the population is first written out to a file.  Then the executable is called.
 * Finally, the result is read in.
 * 
 * The executable, the input file, and the output file must all be located
 * in the current working directory.
 * 
 * A static list of the unique executable/input files already called is kept.  
 * This prevents the same executable/input file combination from being called
 * more than once.  This is useful when one executable generates more than
 * one result file.  The init() method must be called to clear the list.
 */
public abstract class ExecFunction
    extends Function {
  private String execName, inputFile, outputFile;

  public ExecFunction(String name, String exec, String in, String out) {
    super(name);
    execName = exec;
    inputFile = in;
    outputFile = out;
  }

  public String getExecutableName() {
    return execName;
  }

  public void setExecutableName(String newExec) {
    execName = newExec;
  }

  public String getInputFileName() {
    return inputFile;
  }

  public void setInputFileName(String newInput) {
    inputFile = newInput;
  }

  public String getOutputFileName() {
    return outputFile;
  }

  public void setOutputFileName(String newOutput) {
    outputFile = newOutput;
  }

  public void init() {
    execInputsExecuted = new LinkedList();
    inputFileWritten = null;
  }

  public double[] evaluate(Population p, MutableTable mt) throws Exception {
    ExecInputPair eip = new ExecInputPair(
        this.getExecutableName(), this.getInputFileName());
    if (!ExecFunction.execInputsExecuted.contains(eip)) {
      // this exec / input combination has not been
      // called; we must call it.
      if (ExecFunction.inputFileWritten == null) {
        // we must write out the file
        ExecFunction.writePopulation(
            p, this.getInputFileName());

        ExecFunction.inputFileWritten =
            this.getInputFileName();
      }
      else {
        // copy the file
        ExecFunction.copyFile(
            ExecFunction.inputFileWritten, this.getInputFileName());
      }

      ExecFunction.callExecutable(this.getExecutableName());
      ExecFunction.execInputsExecuted.add(eip);
    }
    
    // now read the result	
    double[] res = ExecFunction.readResult(
        this.getOutputFileName(), p.size());

    return res;
  }

  protected static List execInputsExecuted;
  protected static String inputFileWritten;

  protected static class ExecInputPair {
    String exec;
    String input;

    ExecInputPair(String n, String i) {
      exec = n;
      input = i;
    }

    public boolean equals(Object o) {
      ExecInputPair eip = (ExecInputPair) o;
      return (exec.equals(eip.exec)) &&
          (input.equals(eip.input));
    }
    
    public String toString() {
      return "{"+exec+","+input+"}";  
    }
  }
  
  private static final String SPACE = " ";  

  protected static void writePopulation(Population p,
                                        String fileName) throws Exception {

    FileWriter stringFileWriter = new FileWriter(fileName);
    BufferedWriter bw = new BufferedWriter(stringFileWriter);
    PrintWriter pw = new PrintWriter(bw);

    Range[] traits = p.getTraits();
    int numTraits = traits.length;
    int size = p.size();

    // write population size in file
    pw.println(size);

    // write length of each gene/chromosome
    pw.println(numTraits);

    // write genes
    for (int j = 0; j < size; j++) {
      double[] values = p.getMember(j).toDoubleValues();
      for(int k = 0; k < numTraits; k++) {
        pw.print(values[k]);
        pw.print(SPACE);
      }

      pw.println();
    }
    pw.println();
    // close file and streams
    pw.flush();
    bw.flush();
    stringFileWriter.flush();
    pw.close();
    bw.close();
    stringFileWriter.close();
  }

  protected static void callExecutable(String exec) throws Exception {
    ExecRunner er = new ExecRunner();
    er.exec(exec);    
  }

  protected static double[] readResult(String fileName, int size) throws Exception {
    double[] fit = new double[size];

    FileReader stringFileReader = new FileReader(fileName);
    BufferedReader br = new BufferedReader(stringFileReader);

    int i = 0;
    String s = br.readLine();
    while (s != null) {
      fit[i] = Double.parseDouble(s);
      i++;
      s = br.readLine();
    }

    // close file and streams
    stringFileReader.close();
    br.close();

    return fit;
  }

  protected static void copyFile(String src, String dest) throws IOException {
    if (src.equals(dest)) {
      return;
    }

    BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
        dest));

    try {
      byte[] b = new byte[2048];
      int read = 0;

      while ( (read = in.read(b)) != -1) {
        out.write(b, 0, read);
      }

      in.close();
      out.close();
    }
    catch (IOException ioe) {
      in.close();
      out.close();
      throw ioe;
    }
    
  }

}
