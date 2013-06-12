package ncsa.d2k.modules.projects.dtcheng.servio;

import Serialio.*;

public class Global {
  static Object lock = new Object();
  static SerialConfig serCfg;
  static SerialPort p;
  static final String devName = "COM1"; //on Unix use device name e.g. "/dev/ttyS0"
  static int BaudRate = SerialConfig.BR_115200;
  static final int TimeOutWaitPeriod = 100;
  static final long ReadWaitSleepTime = 1;
  static final long CommandWaitLength = 0L; // 16 for direct connection; 80 for wirless through keysan
  static final boolean ReportTimeOuts = true;
  static final boolean WaitForResponse = true;


  static public void initialize() throws Exception {
    Global.serCfg = new SerialConfig(devName);
    Global.serCfg.setBitRate(BaudRate);
    Global.serCfg.setDataBits(SerialConfig.LN_8BITS);
    Global.serCfg.setStopBits(SerialConfig.ST_1BITS);
    Global.serCfg.setParity(SerialConfig.PY_NONE);
    Global.serCfg.setHandshake(SerialConfig.HS_NONE);
    Global.serCfg.setTxLen(4096);
    Global.serCfg.setRxLen(4096);
    Global.p = new SerialPortLocal(Global.serCfg);
    Global.p.setDTR(true); // some devices need DTR set (like the laptop)
    Global.p.setRTS(true); // wireless rs232 needs this
    Global.p.setTimeoutRx(0);
    Global.p.txDrain();
    Global.p.rxFlush();

    System.out.println("p.getConfig().getTxLen() = " + Global.p.getConfig().getTxLen());
    System.out.println("p.getConfig().getRxLen() = " + Global.p.getConfig().getRxLen());
  }
}
