package ncsa.d2k.modules.core.datatype.table;

public interface PredictionExample extends Example {

    public int getNumPredictions();

    public void setDoublePrediction(double pred, int p);
    public double getDoublePrediction(int p);

    public void setIntPrediction(int pred, int p);
    public int getIntPrediction(int p);

    public void setFloatPrediction(float pred, int p);
    public float getFloatPrediction(int p);

    public void setShortPrediction(short pred, int p);
    public short getShortPrediction(int p);

    public void setLongPrediction(long pred, int p);
    public long getLongPrediction(int p);

    public void setStringPrediction(String pred, int p);
    public String getStringPrediction(int p);

    public void setCharsPrediction(char[] pred, int p);
    public char[] getCharsPrediction(int p);

    public void setCharPrediction(char pred, int p);
    public char getCharPrediction(int p);

    public void setBytesPrediction(byte[] pred, int p);
    public byte[] getBytesPrediction(int p);

    public void setBytePrediction(byte pred, int p);
    public byte getBytePrediction(int p);

    public void setBooleanPrediction(boolean pred, int p);
    public boolean getBooleanPrediction(int p);

    public void setObjectPrediction(Object pred, int p);
    public Object getObjectPrediction(int p);
}
