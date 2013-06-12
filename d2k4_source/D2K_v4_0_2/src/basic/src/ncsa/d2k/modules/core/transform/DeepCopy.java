package ncsa.d2k.modules.core.transform;

import ncsa.d2k.core.modules.*;
import java.io.*;

/**
 * Create a deep copy of a serializable object.
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */
public class DeepCopy
    extends ComputeModule {

  public String[] getInputTypes() {
    return new String[] {
        "java.io.Serializable"
    };
  }

  public String[] getOutputTypes() {
    return new String[] {
        "java.io.Serializable"
    };
  }

  public String getInputInfo(int i) {
    return "Serializable object";
  }

  public String getOutputInfo(int i) {
    return "Deep copy of Serializable object";
  }

  public String getModuleInfo() {
    return "Create a deep copy of the input.";
  }

  public void doit() throws Exception {
    Serializable ser = (Serializable) pullInput(0);

    // copy is achieved by serializing the object and reading it back in

    // serialize the object
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(baos);
    oos.writeObject(ser);
    byte buf[] = baos.toByteArray();
    oos.close();
    // now read it back in
    ByteArrayInputStream bais = new ByteArrayInputStream(buf);
    ObjectInputStream ois = new ObjectInputStream(bais);
    Object copy = ois.readObject();
    ois.close();

    // push out the copy
    pushOutput(copy, 0);
  }

}