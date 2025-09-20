package business;

import java.util.ArrayList;

import dao.CartDao;
import entity.Cart;

public class CartController {
	private final CartDao cartDao = new CartDao();
	
	public boolean save(Cart cart) {
		return this.cartDao.save(cart);
	}
	
	public ArrayList<Cart> findAll(){
		return this.cartDao.findAll();
	}

}
