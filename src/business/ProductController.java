package business;

import java.awt.event.ItemEvent;
import java.util.ArrayList;

import core.Helper;
import core.Item;
import dao.ProductDao;
import entity.Product;

public class ProductController {
private final ProductDao productDao =  new ProductDao();
	
	public ArrayList<Product> findAll(){
		return this.productDao.findAll();
	}
	
	public boolean save(Product product) {
		return this.productDao.save(product);
	}
	
	public Product getById(int id) {
		return this.productDao.getById(id);
	}
	
	public boolean update(Product product) {
		if(this.getById(product.getId())== null) {
			Helper.showMessage(product.getId() + " id registered customer not found.");
			return false;
		}
		return this.productDao.update(product);
	}
	
	public boolean delete(int id) {
		if(this.getById(id) == null) {
			Helper.showMessage(id + " id registered customer not found.");
			return false;
		}
		return this.productDao.delete(id);
	}
	
	public ArrayList<Product> filter(String name, String code, Item isStock) {
	    String query = "SELECT * FROM product";
	    ArrayList<String> whereList = new ArrayList<>();

	    if (name != null && !name.isBlank()) {
	        String safe = name.trim().replace("'", "''");
	        whereList.add("name ILIKE '%" + safe + "%'");
	    }

	    if (code != null && !code.isBlank()) {
	        String safe = code.trim().replace("'", "''");
	        whereList.add("code ILIKE '%" + safe + "%'");
	    }

	    if (isStock != null) {
	        if (isStock.getKey() == 1) {
	            whereList.add("stock > 0");
	        } else if (isStock.getKey() == 2) {
	            whereList.add("stock <= 0");
	        }
	    }

	    if (!whereList.isEmpty()) {
	        String whereQuery = String.join(" AND ", whereList); // <-- aralara boşluk
	        query += " WHERE " + whereQuery;
	    }

	    return this.productDao.query(query);
	}
	
	
	
	
	
	
	
	
	

}
