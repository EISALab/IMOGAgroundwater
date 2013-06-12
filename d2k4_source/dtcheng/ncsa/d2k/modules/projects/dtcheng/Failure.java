package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.projects.dtcheng.*;

public class Failure
  {
  static public void report(String Message) throws Exception
    {
    Exception e = new Exception();
    //e.printStackTrace();

    System.out.println("Failure!!!!!   Error Message = " + Message);

    throw e;
    }
  }

