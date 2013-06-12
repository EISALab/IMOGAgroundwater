package ncsa.d2k.modules.projects.dtcheng;

import java.io.*;
import ncsa.d2k.modules.projects.dtcheng.io.*;

public class ExtensionFileFilter
    implements java.io.FileFilter {

  String extension;

  public ExtensionFileFilter(String extension) {
    this.extension = extension;
  }

  public boolean accept(File file) {
    if (file.getName().endsWith(extension))
      return true;
    else
      return false;
  }

}