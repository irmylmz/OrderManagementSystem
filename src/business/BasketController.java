package business;

import java.util.ArrayList;

import dao.BasketDao;
import entity.Basket;

public class BasketController {
	private final BasketDao basketDao = new BasketDao();
	
	public boolean save(Basket basket) {
		return this.basketDao.save(basket);
	}
	
	public ArrayList<Basket> findAll(){
		return this.basketDao.findAll();
	}
	
	public boolean clear() {
		return this.basketDao.clear();
	}
}
