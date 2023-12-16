package assessment;

public class ShiftCipher {
	
	int key;
	
	public ShiftCipher(int k)
	{
		this.key = k;
	}
	
	
	public String decrypt(String cipherText)
	{
		String plainText="";
		
		for(int i=0; i<cipherText.length(); i++) // loop through all characters
		{
			char plainCharacter = cipherText.charAt(i);
			int newPosition = (plainCharacter - key) % 128;
			char cipherCharacter = (char) newPosition;
			plainText += cipherCharacter; // appending this cipher character to the cipherText
			
		}
			
		return plainText;
	}
	
	public String encrypt(String plainText)
	{
		String cipherText="";
		
		for(int i=0; i<plainText.length(); i++) // loop through all characters
		{
			char plainCharacter = plainText.charAt(i);			
			int newPosition = (plainCharacter + key) % 128;
			char cipherCharacter = (char) newPosition;
			cipherText += cipherCharacter;
			
		}
		return cipherText;
	}

	

}
