package core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class Database {
	// Singleton design pattern
	
	private static Database instance = null;
	private Connection connection = null;
	
	Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    String host = dotenv.get("DB_HOST");
    String port = dotenv.get("DB_PORT");
    String name = dotenv.get("DB_NAME");
    String user = dotenv.get("DB_USER");
    String pass = dotenv.get("DB_PASSWORD");
    
    String url = "jdbc:postgresql://" + host + ":" + port + "/" + name + "?sslmode=disable";
    
    private Database() {
    	try {
            // İSTENMİYOR: try-with-resources (bağlantıyı kapatır)
            this.connection = DriverManager.getConnection(url, user, pass);
            System.out.println("✔ Bağlantı başarılı: " + this.connection.getMetaData().getURL());
        } catch (SQLException e) {
            System.err.println("✖ Bağlantı hatası: " + e.getMessage());
            // üst kata net hata verelim ki null connection ile devam edilmesin
            throw new RuntimeException("DB connection failed", e);
        }
    }
    
    private Connection getConnection() {
    	return connection;
    }
    
    public static Connection getInstance() {
    	try {
    		if(instance == null || instance.getConnection().isClosed()) {
        		instance = new Database();
        	}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
    	
    	return instance.getConnection();
    }

}
