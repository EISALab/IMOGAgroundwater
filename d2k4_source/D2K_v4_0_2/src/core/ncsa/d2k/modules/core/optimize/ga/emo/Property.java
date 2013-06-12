package ncsa.d2k.modules.core.optimize.ga.emo;

/**
 * A simple method of viewing the editable properties of a class without using
 * introspection.  Very similar to java beans, but easier to use.
 *
 * Used by the InputParemeters UI module to get the available properties
 * of crossover, mutation, and selection techniques.
 */
public abstract class Property implements java.io.Serializable {
  public static final int INT = 0;
  public static final int DOUBLE = 1;
  public static final int STRING = 2;
  //public static final int BOOLEAN = 3;

  private int type;
  private String name;
  private String description;
  private Object value;

  protected Property(int typ, String nme, String desc, Object defVal) {
    type = typ;
    name = nme;
    description = desc;
    setValue(defVal);
  }

  /**
   * Get the type of this property -- Property.INT, Property.DOUBLE, or Property.STRING.
   * @return
   */
  public final int getType() {
    return type;
  }

  /**
   * Get the name of this property.
   * @return
   */
  public final String getName() {
    return name;
  }

  /**
   * Get the description of this property.
   * @return
   */
  public final String getDescription() {
    return description;
  }

  /**
   * Get the value of this property.
   * @return
   */
  public Object getValue() {
    return value;
  }

  /**
   * Set the value of this property.
   * @param val
   */
  public void setValue(Object val) {
    value = val;
  }
}