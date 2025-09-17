package dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import core.Database;
import entity.Customer;
import entity.User;

public class CustomerDao {
private Connection connection;
	
	public CustomerDao() {
		this.connection = Database.getInstance();
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
