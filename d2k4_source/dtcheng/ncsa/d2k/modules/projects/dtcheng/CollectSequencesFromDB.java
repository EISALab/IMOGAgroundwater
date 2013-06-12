package ncsa.d2k.modules.projects.dtcheng;

import ncsa.d2k.modules.projects.dtcheng.primitive.*;
import ncsa.d2k.core.modules.InputModule;

public class CollectSequencesFromDB
    extends InputModule {

  private String ProteomeList = "HUMAN";
  public void setProteomeList(String value) {
    this.ProteomeList = value;
  }

  public String getProteomeList() {
    return this.ProteomeList;
  }

  private boolean UseWholeSequence = false; //PS00022
  public void setUseWholeSequence(boolean value) {
    this.UseWholeSequence = value;
  }

  public boolean getUseWholeSequence() {
    return this.UseWholeSequence;
  }

  private String MotifName = "PS00022"; //PS00022
  public void setMotifName(String value) {
    this.MotifName = value;
  }

  public String getMotifName() {
    return this.MotifName;
  }

  public String getModuleName() {
    return "CollectSequencesFromDB";
  }

  public String getModuleInfo() {
    return "CollectSequencesFromDB";
  }

  public String getInputName(int i) {
    switch (i) {
    }
    return "";
  }

  public String[] getInputTypes() {
    String[] types = {
    };
    return types;
  }

  public String getInputInfo(int i) {
    switch (i) {
    }
    return "";
  }

  public String getOutputName(int i) {
    switch (i) {
      case 0:
        return "motifList";
      default:
        return "No such output";
    }
  }

  public String[] getOutputTypes() {
    String[] types = {
        "java.util.List",
    };
    return types;
  }

  public String getOutputInfo(int i) {
    switch (i) {
      case 0:
        return "motifList";
      default:
        return "No such output";
    }
  }

  public void doit() throws Exception {

    ExtractMotif extractor = new ExtractMotif();

    try {
      Class.forName("oracle.jdbc.driver.OracleDriver");
    }
    catch (java.lang.ClassNotFoundException e) {
      e.printStackTrace();
    }

    String[] ProteomeNames = Utility.parseCSVList(ProteomeList);

    if (!UseWholeSequence)
      this.pushOutput(ExtractMotif.collectMotifs(MotifName, ProteomeNames), 0);
    else
      this.pushOutput(ExtractMotif.collectSequences(MotifName, ProteomeNames), 0);
  }

}
