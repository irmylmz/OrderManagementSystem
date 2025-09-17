package business;

import java.util.ArrayList;

import dao.CustomerDao;
import entity.Customer;


public class CustomerController {
	private final CustomerDao customerDao =  new CustomerDao();
	
	public ArrayList<Customer> findAll(){
		return this.customerDao.findAll();
	}
}
