package ncsa.d2k.modules.core.optimize.ga.emo.mutation;

import ncsa.d2k.modules.core.optimize.ga.mutation.*;

public class MutationFactory {

  private static final int MUTATION = 0;
  private static final int REAL_MUTATION = 1;

  public static final int NUM_MUTATION = 2;

  private static final Mutation createMutation(int type) {
    switch(type) {
      case(MUTATION):
        return new EMOMutation();
      case(REAL_MUTATION):
        return new EMORealMutation();
      default:
        return new EMOMutation();
    }
  }

  public static int getBinaryDefault() {
    return MUTATION;
  }

  public static int getRealDefault() {
    return REAL_MUTATION;
  }

  public static Mutation[] createMutationOptions() {
    Mutation[] retVal = new Mutation[NUM_MUTATION];
    for(int i = 0; i < NUM_MUTATION; i++) {
      retVal[i] = createMutation(i);
    }
    return retVal;
  }
}