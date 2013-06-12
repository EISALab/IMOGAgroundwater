package ncsa.d2k.modules.projects.dtcheng.datatype;


import java.io.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;


public class SubstringFileFilter implements java.io.FileFilter {

  String Substring;

  public SubstringFileFilter(String Substring) {
    this.Substring = Substring;
  }


  public boolean accept(File file) {
    if (file.getName().indexOf(Substring) != -1)
      return true;
    else
      return false;
  }

}