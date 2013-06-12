package ncsa.d2k.modules.core.datatype.table;

public interface MutableExample extends Example {
    public double setInputDouble(int i, double value);
    public double setOutputDouble(int i, double value);
    public MutableExample copy();
}
