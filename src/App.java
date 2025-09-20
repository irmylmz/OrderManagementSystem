
import business.UserController;
import core.Helper;
import entity.User;
import view.DashboardUI;
import view.LoginUI;

public class App {

	public static void main(String[] args) {
		Helper.setTheme();
		LoginUI login = new LoginUI();
		
		/*
		UserController userController = new UserController();
		User user = userController.findByLogin("irm@gmail.com", "135");
		DashboardUI dashboardUI = new DashboardUI(user);
		
		*/
		
	}

}
