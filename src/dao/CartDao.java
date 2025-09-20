package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import core.Database;
import entity.Cart;


public class CartDao {
	private Connection connection;
	private ProductDao productDao;
	private CustomerDao customerDao;
	
	public CartDao() {
		this.connection = Database.getInstance();
		this.productDao  = new ProductDao();   // <-- EKLE
        this.customerDao = new CustomerDao();
	}

	public ArrayList<Cart> findAll() {
	    ArrayList<Cart> carts = new ArrayList<>();
	    try {
	        ResultSet rs = this.connection.createStatement().executeQuery("SELECT * FROM cart");
	        while (rs.next()) {
	        	carts.add(this.match(rs));
	        }
	    } catch (SQLException exception) {
	        exception.printStackTrace();
	    }
	    return carts;
	}
	
	public Cart match(ResultSet rs) throws SQLException {
	    Cart cart = new Cart();
	    cart.setId(rs.getInt("id"));
	    cart.setCustomerId(rs.getInt("customer_id"));
	    cart.setProductId(rs.getInt("product_id"));
	    cart.setPrice(rs.getBigDecimal("price"));
	    
	    java.sql.Date sqlDate = rs.getDate("date");
	    if (sqlDate != null) {
	        cart.setDate(sqlDate.toLocalDate()); // SQL -> LocalDate
	    }
	    
	    cart.setNote(rs.getString("note"));
	    cart.setCustomer(this.customerDao.getById(cart.getCustomerId()));
	    cart.setProduct(this.productDao.getById(cart.getProductId()));
	    return cart;
	}
	
	public boolean save(Cart cart) {
	    String query = "INSERT INTO cart (customer_id, product_id, price, date, note) VALUES (?,?,?,?,?)";
	    try {
	        PreparedStatement pr = this.connection.prepareStatement(query);
	        pr.setInt(1, cart.getCustomerId());
	        pr.setInt(2, cart.getProductId());
	        pr.setBigDecimal(3, cart.getPrice());
	        pr.setDate(4, java.sql.Date.valueOf(cart.getDate())); 
	        pr.setString(5, cart.getNote());
	        return pr.executeUpdate() != -1;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean delete(int id) {
	    String query = "DELETE FROM cart WHERE id = ?";
	    try {
	        PreparedStatement pr = this.connection.prepareStatement(query);
	        pr.setInt(1, id);
	        return pr.executeUpdate() != -1;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
}
