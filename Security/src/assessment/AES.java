package assessment;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES class for encryption and decryption.
 * 
 * Note: This class uses AES with CBC mode and PKCS7Padding.
 * It is recommended to use a secure random IV for each encryption operation.
 */
public class AES {

	public static final int IV_LEN = 16;
    private SecretKey secretKey;

    public AES() throws NoSuchAlgorithmException {
        generateKey();
    }

    /**
     * Generate an AES key using KeyGenerator
     */
    public void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        this.setSecretkey(keyGen.generateKey());
    }

    /** 
     * Encrypt large text using BouncyCastle.
     */
    public byte[] encryptBC(String strDataToEncrypt)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, ShortBufferException {
    	
    	byte[] iv = new byte[IV_LEN];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, this.getSecretKey(), new IvParameterSpec(iv));
        byte[] cipherText = new byte[cipher.getOutputSize(byteDataToEncrypt.length) + iv.length]; 
        System.arraycopy(iv, 0, cipherText, 0, iv.length);
        byte[] messageCipher = new byte[cipher.getOutputSize(byteDataToEncrypt.length)];
        int ctLength = cipher.update(byteDataToEncrypt, 0, byteDataToEncrypt.length, messageCipher, 0);
        ctLength += cipher.doFinal(messageCipher, ctLength);
        System.arraycopy(messageCipher, 0, cipherText,iv.length,
        messageCipher.length);
        return cipherText;
    }

    /** 
     * Decrypt large text using BouncyCastle.
     */
    public String decryptBC(byte[] cipherText)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, ShortBufferException {
    	
    	byte[] iv = new byte[IV_LEN];
        System.arraycopy(cipherText, 0, iv, 0, iv.length);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, this.getSecretKey(), new IvParameterSpec(iv));
        byte[] messageCipher = new byte[cipherText.length - iv.length];
        System.arraycopy(cipherText, iv.length, messageCipher, 0, cipherText.length - iv.length);
        byte[] plainText = new byte[cipherText.length - iv.length];
        int ptLength = cipher.update(messageCipher, 0, messageCipher.length, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);
        return new String(plainText);

    }

    /**
     * @return the secretKey
     */
    public SecretKey getSecretKey() {
        return secretKey;
    }

    /**
     * @param secretKey the secretKey to set
     */
    public void setSecretkey(SecretKey secretKey) {
        this.secretKey = secretKey;
    }
    
    /**
     * @param keyText the String used to generate SecretKey
     */
    public void setSecretkey(String keyText) {
    	try {
            // Use a KeyGenerator to generate a key
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128); // Specify the key size (in bits)

            // Convert the key text to bytes
            byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);

            secretKey = new SecretKeySpec(keyBytes, "AES");
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
    
}
