package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import core.Database;
import entity.Customer;

public class CustomerDao {
private Connection connection;
	
	public CustomerDao() {
		this.connection = Database.getInstance();
	}
	
	public boolean save(Customer customer) {
		String query = "INSERT INTO customer " +
	            "(" +
	            "name," +
	            "type," +
	            "phone," +
	            "mail," +
	            "address" +
	            ")" +
	            " VALUES (?,?,?,?,?)";
		
		try {
			PreparedStatement pr = this.connection.prepareStatement(query);
			pr.setString(1, customer.getName());
			pr.setString(2, customer.getType().toString());
			pr.setString(3, customer.getPhone());
			pr.setString(4, customer.getMail());
			pr.setString(5, customer.getAddress());
			return pr.executeUpdate() != -1;
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean update(Customer customer) {
	    String query = "UPDATE customer SET " +
	            "name = ?, " +
	            "type = ?, " +
	            "phone = ?, " +
	            "mail = ?, " +
	            "address = ? " +
	            "WHERE id = ?";

	    try {
	        PreparedStatement pr = this.connection.prepareStatement(query);
	        pr.setString(1, customer.getName());
	        pr.setString(2, customer.getType().toString());
	        pr.setString(3, customer.getPhone());
	        pr.setString(4, customer.getMail());
	        pr.setString(5, customer.getAddress());
	        pr.setInt(6, customer.getId()); // id şart!

	        return pr.executeUpdate() != -1;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean delete(int id) {
		String query = "DELETE FROM customer WHERE id = ?";
		try {
			PreparedStatement pr = this.connection.prepareStatement(query);
			pr.setInt(1, id);
			return pr.executeUpdate() != -1;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return true;
		
	}
	
	public Customer getById(int id) {
		Customer customer = null;
		String query = "SELECT * FROM customer WHERE id = ?";
		try {
			PreparedStatement pr = this.connection.prepareStatement(query);
			pr.setInt(1, id);
			ResultSet rSet = pr.executeQuery();
			if(rSet.next()) customer = this.match(rSet);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return customer;
 	}
	
	public Customer match(ResultSet rs) throws SQLException {
	    Customer customer = new Customer();
	    customer.setId(rs.getInt("id"));
	    customer.setName(rs.getString("name"));
	    customer.setType(Customer.TYPE.valueOf(rs.getString("type")));
	    customer.setPhone(rs.getString("phone"));
	    customer.setMail(rs.getString("mail"));
	    customer.setAddress(rs.getString("address"));
	    return customer;
	}
	
	public ArrayList<Customer> findAll() {
	    ArrayList<Customer> customers = new ArrayList<>();
	    try {
	        ResultSet rs = this.connection.createStatement().executeQuery("SELECT * FROM customer");
	        while (rs.next()) {
	        	customers.add(this.match(rs));
	        }
	    } catch (SQLException exception) {
	        exception.printStackTrace();
	    }
	    return customers;
	}

}
