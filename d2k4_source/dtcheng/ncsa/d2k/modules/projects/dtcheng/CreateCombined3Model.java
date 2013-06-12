package ncsa.d2k.modules.projects.dtcheng;
import ncsa.d2k.modules.core.datatype.model.*;
import ncsa.d2k.core.modules.*;
import ncsa.d2k.modules.projects.dtcheng.datatype.*;

public class CreateCombined3Model extends ComputeModule
  {

  private boolean    Trace = false;
  public  void    setTrace (boolean value) {       this.Trace = value;}
  public  boolean getTrace ()              {return this.Trace;}

  private String ConstructedFeatureName    = "ModelPrediction";
  public  void   setConstructedFeatureName (String value) {       this.ConstructedFeatureName       = value;}
  public  String getConstructedFeatureName ()             {return this.ConstructedFeatureName;}


  public String getModuleInfo()
    {
    return "CreateCombined3Model";
    }
  public String getModuleName()
    {
    return "CreateCombined3Model";
    }

  public String getInputName(int i)
    {
    switch (i)
      {
      case 0: return "Model1";
      case 1: return "Model2";
      case 2: return "Model3";
      }
    return "";
    }
  public String[] getInputTypes()
    {
    String[] types = {"ncsa.d2k.modules.projects.dtcheng.Model",
                      "ncsa.d2k.modules.projects.dtcheng.Model",
                      "ncsa.d2k.modules.projects.dtcheng.Model"};
    return types;
  }
  public String getInputInfo(int i)
    {
    switch (i) {
      case 0: return "Model";
      case 1: return "Model";
      case 2: return "Model";
      default: return "No such input";
    }
  }

  public String getOutputName(int i)
    {
    switch (i)
      {
      case 0: return "Model";
      }
    return "";
    }
  public String[] getOutputTypes()
    {
    String[] types = {"ncsa.d2k.modules.projects.dtcheng.Model"};
    return types;
  }
  public String getOutputInfo(int i)
    {
    switch (i)
      {
      case 0: return "Model";
      default: return "No such output";
      }
  }




   public void doit() throws Exception
    {
    Model model1 = (Model) this.pullInput(0);
    Model model2 = (Model) this.pullInput(1);
    Model model3 = (Model) this.pullInput(2);

    CombinedModel combinedModel = new CombinedModel(null /*examples*/, model1, model2, model3);

    this.pushOutput(combinedModel, 0);



    }


  }
