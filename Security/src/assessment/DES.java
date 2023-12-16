package assessment;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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
 *
 * @author ahmed
 */
public class DES {
    
    private SecretKey secretkey; 
   
    public DES() throws NoSuchAlgorithmException 
    {
        generateKey();
    }
    
    
    /**
	* Step 1. Generate a DES key using KeyGenerator 
    */
    
    public void generateKey() throws NoSuchAlgorithmException 
    {
        KeyGenerator keyGen = KeyGenerator.getInstance("DES");
        this.setSecretkey(keyGen.generateKey());        
    }
    
    public byte[] encryptBC (String strDataToEncrypt) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException, ShortBufferException, NoSuchProviderException
    {
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] cipherText = new byte[cipher.getOutputSize(byteDataToEncrypt.length)];      
        int ctLength = cipher.update(byteDataToEncrypt, 0, byteDataToEncrypt.length, cipherText, 0);
        ctLength += cipher.doFinal(cipherText, ctLength);
        return cipherText;
    }
   
    
    public String decryptBC (byte[] strCipherText) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException, ShortBufferException, NoSuchProviderException
    {        
    	Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS7Padding", "BC");
        cipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
        byte[] plainText = new byte[cipher.getOutputSize(strCipherText.length)];
        int ptLength = cipher.update(strCipherText, 0, strCipherText.length, plainText, 0);
        ptLength += cipher.doFinal(plainText, ptLength);
        return new String(plainText);
    }   
 

    /**
     * @return the secretkey
     */
    public SecretKey getSecretkey() {
        return secretkey;
    }

    /**
     * @param secretkey the secretkey to set
     */
    public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }
    
    
    public void setSecretkey(String keyText) {
    	try {
            // Use a KeyGenerator to generate a key
            KeyGenerator keyGen = KeyGenerator.getInstance("DES");
            // Convert the key text to bytes
            byte[] keyBytes = keyText.getBytes(StandardCharsets.UTF_8);
            secretkey = new SecretKeySpec(keyBytes, "DES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public SecretKey decryptKey(byte[] cipherText, String alg)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, ShortBufferException {
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
        byte[] plainText = cipher.doFinal(cipherText);
        return new SecretKeySpec(plainText, 0, plainText.length, alg);
    }
    
    
    public byte[] encryptKey(SecretKey keyToEncrypt)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException,
            NoSuchProviderException, ShortBufferException {
        byte[] keyBytes = keyToEncrypt.getEncoded();
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] cipherText = cipher.doFinal(keyBytes); 
        return cipherText;
    }
    
    
    public byte[] encrypt (String strDataToEncrypt) throws 
		    NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
		    InvalidAlgorithmParameterException, IllegalBlockSizeException, 
		    BadPaddingException, ShortBufferException, NoSuchProviderException{
		byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] cipherText = cipher.doFinal(byteDataToEncrypt); 
        return cipherText;
	}


public String decrypt (byte[] strCipherText) throws 
		    NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
		    InvalidAlgorithmParameterException, IllegalBlockSizeException, 
		    BadPaddingException, ShortBufferException, NoSuchProviderException{        
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());
        byte[] plainText = cipher.doFinal(strCipherText);
        return new String(plainText);
	}      
    
    
    public void encryptFile(byte[] input, File output, SecretKey key) 
    		throws IOException, GeneralSecurityException {
    	Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
    	cipher.init(Cipher.ENCRYPT_MODE, key);
    	writeDataToFile(output, cipher.doFinal(input));
    }
            
    public void decryptFile(byte[] input, File output, SecretKey key) 
    		throws IOException, GeneralSecurityException {
    	Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
    	cipher.init(Cipher.DECRYPT_MODE, key);
    	writeDataToFile(output, cipher.doFinal(input));
    }
            
    private void writeDataToFile(File output, byte[] toWrite)
    		throws IllegalBlockSizeException, BadPaddingException, IOException {
    	FileOutputStream fos = new FileOutputStream(output);
    	fos.write(toWrite);
    	fos.flush();
    	fos.close();
    }
            
    public byte[] getFileInBytes(File f) throws IOException {
    	FileInputStream fis = new FileInputStream(f);
    	byte[] fbytes = new byte[(int) f.length()];
    	fis.read(fbytes);
    	fis.close();
    	return fbytes;
    }
            
    public void writeKeyToFile(String path, byte[] key) throws IOException {
    	File f = new File(path);
    	f.getParentFile().mkdirs();
    	FileOutputStream fos = new FileOutputStream(f);
    	fos.write(key);
    	fos.flush();
    	fos.close();
    }     
            
    public SecretKey getKeyFromFile(String filename, String alg) throws Exception {
    	byte[] keyBytes = Files.readAllBytes(new File(filename).toPath());
    	
    	switch (alg) {
    		case "DES":
    			return new SecretKeySpec(keyBytes, 0, keyBytes.length, "DES");
    		case "AES":
    			return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
    		default:
    			return null;
        		
        }
    }
            
        	
    
}
