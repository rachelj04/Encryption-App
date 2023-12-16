package assessment;
import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class DBController {
    static final String JDBC_URL = "jdbc:mysql://securityassessment.coip0kp9f3qh.ap-southeast-2.rds.amazonaws.com"
    		+ ":3306/security";
    static final String USERNAME = "admin";
    static final String PASSWORD = "masterpass"; // null password
    private PasswordAuthentication pa = new PasswordAuthentication();
   
    

    public DBController() {
//            Register JDBC driver
            try {
				Class.forName("com.mysql.cj.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

    }

    public boolean authenticate(String username, String password) {
    	try {
//          Register JDBC driver
          Class.forName("com.mysql.cj.jdbc.Driver");
          // Open a connection

          Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
          // Execute a query
//          System.out.println("Creating statement...");
         
          
//          String sql = "SELECT * FROM users where user_name = \"" + username +"\";";
          
          String query = "SELECT * FROM users where user_name = ?;";
          PreparedStatement statement = connection.prepareStatement(query);
          statement.setString(1, username);
          
	      ResultSet resultSet = statement.executeQuery();
	        
	      if (!resultSet.next()) {
	    	/// Close external resources
	          resultSet.close();
	          statement.close();
	          connection.close();
              return false;
          } else {
        	/// Close external resources
        	  String hashed = resultSet.getString("user_password");
        	  
              resultSet.close();
              statement.close();
              connection.close();
    	      
    	      return pa.authenticate(password.toCharArray(), hashed);
          }
	    
      } catch (ClassNotFoundException | SQLException e) {
          e.printStackTrace();
          return false;
      }
    }
    
    public boolean existUsername(String username) {
    	try {
//          
          // Open a connection

          Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
          // Execute a query
//          System.out.println("Creating statement...");
          
          
          String query = "SELECT * FROM users where user_name = ?;";
          PreparedStatement statement = connection.prepareStatement(query);
          statement.setString(1, username);
          
	      ResultSet resultSet = statement.executeQuery();
	        
	     
	        
	      if (resultSet.next()) {
	    	/// Close external resources
	          resultSet.close();
	          statement.close();
	          connection.close();
              return true;
          } else {
        	/// Close external resources
        	  resultSet.close();
	          statement.close();
	          connection.close();
        	  return false;
          }
	    
      } catch (SQLException e) {
          e.printStackTrace();
          return false;
      }
    }
    
    public boolean register(String username, String password) {
    	try {
//        
          // Open a connection

          Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
          // Execute a query
//          System.out.println("Creating statement...");
         
          String hashed = pa.hash(password.toCharArray());
          String query = "INSERT INTO users(user_name, user_password) VALUES(?, ?)";
          PreparedStatement statement = connection.prepareStatement(query);
          statement.setString(1, username);
          statement.setString(2, hashed);
	      statement.executeQuery();
          statement.close();
          connection.close();
    	  return true;
      } catch (SQLException e) {
          e.printStackTrace();
          return false;
      }
    }
    
    public byte[] loadKey(String username, String keyname) {
    	try {
//        
          // Open a connection

          Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
          // Execute a query
//          System.out.println("Creating statement...");
         
          
//          String sql = "SELECT DECRYPTBYPASSPHRASE('" + passphrase + "', enc_key) FROM saved_keys where user_name = \"" 
//        		  		+ username + "\" AND key_name = \"" + keyname + "\";";
          
          
          
          String query = "SELECT enc_key FROM saved_keys where user_name = ?"
          		+ " AND key_name = ?;";
          PreparedStatement statement = connection.prepareStatement(query);
          statement.setString(1, username);
          statement.setString(2, keyname);
          
	      ResultSet resultSet = statement.executeQuery();
	        
	      if (resultSet.next()) {
	    	/// Close external resources
	    	  byte[] key = resultSet.getBytes("enc_key");
	          resultSet.close();
	          statement.close();
	          connection.close();
              return key;
          } else {
        	/// Close external resources
        	  resultSet.close();
	          statement.close();
	          connection.close();
        	  return null;
          }
	      
      } catch (SQLException e) {
          e.printStackTrace();
          return null;
      }
    }
    
    public boolean saveKey(String username, String alg, String keyname, byte[] key) {
    	try {
//        
          // Open a connection

          Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
          // Execute a query
//          System.out.println("Creating statement...");
//          Statement statement = connection.createStatement();
         
          
          		
          String query = "INSERT INTO saved_keys(user_name, alg, key_name, enc_key) VALUES(?, ?, ?, ?);";
          try (PreparedStatement statement = connection.prepareStatement(query)) {
              statement.setString(1, username);
              statement.setString(2, alg);
              statement.setString(3, keyname);
              statement.setBytes(4, key);

              statement.executeUpdate();
              statement.close();
          }

          connection.close();
          return true;
         
	      
      } catch (SQLException e) {
          e.printStackTrace();
          return false;
      }
    }
    
    
    public boolean existKey(String username, String keyname) {
    	try {
//        
          // Open a connection

          Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
          // Execute a query
//          System.out.println("Creating statement...");
          Statement statement = connection.createStatement();
          String sql = "SELECT * FROM saved_keys where user_name = \"" + username + 
        		  "\" AND key_name = \"" + keyname + "\";";
	        
	      ResultSet resultSet = statement.executeQuery(sql);
	        
	      if (resultSet.next()) {
	    	/// Close external resources
	          resultSet.close();
	          statement.close();
	          connection.close();
              return true;
          } else {
        	/// Close external resources
        	  resultSet.close();
	          statement.close();
	          connection.close();
        	  return false;
          }

      } catch (SQLException e) {
          e.printStackTrace();
          return false;
      }
    }
    
    public String getAlg(String username, String keyname) {
    	try {
//        
          // Open a connection

          Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
          // Execute a query
//          System.out.println("Creating statement...");
          Statement statement = connection.createStatement();
          String sql = "SELECT * FROM saved_keys where user_name = \"" + username + 
        		  "\" AND key_name = \"" + keyname + "\";";
	        
	      ResultSet resultSet = statement.executeQuery(sql);
	        
	      if (resultSet.next()) {
	    	/// Close external resources
	    	  String alg = resultSet.getString("alg");
	          resultSet.close();
	          statement.close();
	          connection.close();
	          return alg;
              
          } else {
        	/// Close external resources
        	  resultSet.close();
	          statement.close();
	          connection.close();
        	 return null;
          }

      } catch (SQLException e) {
          e.printStackTrace();
          return null;
      }
    }
    
    
    
    public boolean saveSettings(String username, String menuColor, String backgroundColor) {
    	try {
//          
            // Open a connection

            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            // Execute a query

            String query1 = "SELECT * FROM settings where user_name = ?";
            PreparedStatement statement1 = connection.prepareStatement(query1);
            statement1.setString(1, username);
             
    	    ResultSet resultSet1 = statement1.executeQuery();
    	      
    	    // If no existing settings 
    	    if (!resultSet1.next()) {
    	    	String query2 = "INSERT INTO settings(user_name, menu_color, background_color) VALUES(?, ?, ?);";
    	        PreparedStatement statement2 = connection.prepareStatement(query2);

    	        statement2.setString(1, username);
    	        statement2.setString(2, menuColor);
    	        statement2.setString(3, backgroundColor);
    	        statement2.executeUpdate();
    	        // Close external resources
    	          
    	        statement2.close();
    	        resultSet1.close();
    	        statement1.close();
    	        connection.close();
    	        return true;
              // Existing settings to update
    	    } else {
    	    	String query2 = "UPDATE settings SET menu_color = ?, background_color = ? WHERE user_name = ?;";
    	    	PreparedStatement statement2 = connection.prepareStatement(query2);
	
    	    	statement2.setString(3, username);
    	    	statement2.setString(1, menuColor);
    	    	statement2.setString(2, backgroundColor);
    	    	statement2.executeUpdate();
            	// Close external resources
    	    	statement2.close();
    	    	resultSet1.close();
    	    	statement1.close();
    	    	connection.close();
    	    	return true;
    	    }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    public Settings loadSettings(String username) {
    	try {
//          
            // Open a connection

            Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            // Execute a query

            String query = "SELECT * FROM settings where user_name = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username);
             
    	    ResultSet resultSet = statement.executeQuery();
    	      
    	    // If no existing settings 
    	    if (!resultSet.next()) {
    	    	
    	        // Close external resources
    	      
    	        resultSet.close();
    	        statement.close();
    	        connection.close();
    	        return new Settings(null, null);
              // Existing settings
    	    } else {
    	    	String s1 = resultSet.getString("menu_color");
    	    	String s2 = resultSet.getString("background_color");
    	    	
    	    	if (s1.equals("null")) {
    	    		s1 = null;
    	    	}
    	    	
    	    	if (s2.equals("null")) {
    	    		s2 = null;
    	    	}
            	// Close external resources
    	    	
    	    	resultSet.close();
    	    	statement.close();
    	    	connection.close();
    	    	return new Settings(s1, s2);
    	    }
        } catch (SQLException e) {
            e.printStackTrace();
            return new Settings(null, null);
        }
    }
    
    
    
    
}
