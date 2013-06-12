package ncsa.d2k.modules.projects.dtcheng.ExampleApplications;


import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.*;


public class CryptoTest {
  public static final int ACTION_ENCRYPT = 1;
  public static final int ACTION_DECRYPT = 2;
  public static final String ENCRYPTION_ALGORITHM = "Blowfish";
  public static final String RANDOM_ALGORITHM = "SHA1PRNG";
  public static final String CHARSET = "US-ASCII";
  public static final String SECRET_MESSAGE = "The crow flies at midnight.";

  public static byte[] readBytes(FileInputStream fis) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buf = new byte[8192];
    int len;
    while ((len = fis.read(buf)) != -1) {
      baos.write(buf, 0, len);
    }
    return baos.toByteArray();
  }


  public static void main(String args[]) throws Exception {
    String sAction = args[0];
    int action = 0;
    if (sAction.equals("e")) {
      action = ACTION_ENCRYPT;
    }
    else if (sAction.equals("d")) {
      action = ACTION_DECRYPT;
    }
    else {
      System.err.println("java Encrypt [de] passphrase");
    }
    // generate a DES key
    String passphrase = args[1];
    SecureRandom rand = SecureRandom.getInstance(RANDOM_ALGORITHM);
    rand.setSeed(passphrase.getBytes(CHARSET));
    KeyGenerator desKg = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
    desKg.init(rand);
    SecretKey key = desKg.generateKey();
    //
    File secretMessageFile = new File("secretMessage.bin");
    if (action == ACTION_ENCRYPT) {
      // now get a DES cipher and encrypt the secret message
      Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      byte[] secretMessageBytes = SECRET_MESSAGE.getBytes(CHARSET);
      byte[] encryptedMessageBytes = cipher.doFinal(secretMessageBytes);
      FileOutputStream fos = new FileOutputStream(secretMessageFile);
      fos.write(encryptedMessageBytes);
    }
    else {
      // now decrypt it
      FileInputStream fis = new FileInputStream(secretMessageFile);
      byte[] encryptedMessageBytes = readBytes(fis);
      Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM);
      cipher.init(Cipher.DECRYPT_MODE, key);
      byte[] decryptedMessageBytes = cipher.doFinal(encryptedMessageBytes);
      String decryptedSecretMessage = new String(decryptedMessageBytes, CHARSET);
      System.out.println(decryptedSecretMessage);
    }
  }
}

