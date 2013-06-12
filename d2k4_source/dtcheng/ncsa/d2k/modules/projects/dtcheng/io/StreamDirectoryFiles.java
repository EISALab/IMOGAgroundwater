package ncsa.d2k.modules.projects.dtcheng.io;

import ncsa.d2k.core.modules.InputModule;

import java.io.*;
import ncsa.d2k.modules.projects.dtcheng.*;
import ncsa.d2k.modules.projects.dtcheng.datatypes.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.SubstringFileFilter;

public class StreamDirectoryFiles
    extends InputModule {
  //////////////////
  //  PROPERTIES  //
  //////////////////

  private String DirectoryPath = "/";
  public void setDirectoryPath(String value) {
    this.DirectoryPath = value;
  }

  public String getDirectoryPath() {
    return this.DirectoryPath;
  }

  private String FileNameFilter = ".ser";
  public void setFileNameFilter(String value) {
    this.FileNameFilter = value;
  }

  public String getFileNameFilter() {
    return this.FileNameFilter;
  }

  public String getModuleName() {
    return "StreamDirectoryFiles";
  }

  public String getModuleInfo() {
    return "StreamDirectoryFiles";
  }

  public String getInputName(int i) {
    switch (i) {
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = {};
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
      default:
        return "No such input";
    }
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "File";
    }
    return "";
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.io.File"};
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "File";
      default:
        return "No such output";
    }
  }

  public void doit() throws Exception {

    File directory = new File(DirectoryPath);
    SubstringFileFilter filter = new SubstringFileFilter(FileNameFilter);

    File [] files = directory.listFiles(filter);

    if (files != null)
      for (int i = 0; i < files.length; i++) {
        this.pushOutput(files[i], 0);
    }

    this.pushOutput(null, 0);
  }
}
