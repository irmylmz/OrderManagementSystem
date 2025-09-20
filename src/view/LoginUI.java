package view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import business.UserController;
import core.Helper;
import entity.User;


public class LoginUI extends JFrame{
	private JPanel container;
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblTitle;
    private UserController userController;
	
	public LoginUI() {
		this.userController = new UserController();
		// container panel
        container = new JPanel(new GridBagLayout());
        this.setContentPane(container);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);

        // Başlık
        lblTitle = new JLabel("Order Management System", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        gc.fill = GridBagConstraints.HORIZONTAL;
        container.add(lblTitle, gc);

        // Email label
        gc.gridy = 1; gc.gridx = 0; gc.gridwidth = 1;
        gc.fill = GridBagConstraints.NONE; gc.anchor = GridBagConstraints.LINE_END;
        container.add(new JLabel("E-mail: "), gc);

        // Email field
        txtEmail = new JTextField(20);
        gc.gridx = 1; gc.anchor = GridBagConstraints.LINE_START;
        container.add(txtEmail, gc);

        // Password label
        gc.gridy = 2; gc.gridx = 0; gc.anchor = GridBagConstraints.LINE_END;
        container.add(new JLabel("Password: "), gc);

        // Password field
        txtPassword = new JPasswordField(20);
        gc.gridx = 1; gc.anchor = GridBagConstraints.LINE_START;
        container.add(txtPassword, gc);

        // Login button
        btnLogin = new JButton("Log in");
        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        container.add(btnLogin, gc);
        
        btnLogin.addActionListener(e -> {
        	JTextField[] checklist = {this.txtEmail, this.txtPassword};
        	if(!Helper.isEmailValid(this.txtEmail.getText())) {
        		Helper.showMessage("Please enter a valid email address.");    //System.out.println("Please enter a valid email address.");
        	}else if(Helper.isFieldListEmpty(checklist)) {
        		Helper.showMessage("fill");                                   //System.out.println("Please fill out all fields.");
        	}else {
        		User user = this.userController.findByLogin(txtEmail.getText(), txtPassword.getText());
        		if(user == null) {
        			Helper.showMessage("The user you entered could not be found.");
        		}else {
        			this.dispose();
        			DashboardUI dashboardUI = new DashboardUI(user);
        		}
    
        	}
        });

        // Frame ayarları
        this.setTitle("Order Management System - Login");
        this.setSize(400, 250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Ortala
        int x = (Toolkit.getDefaultToolkit().getScreenSize().width - this.getSize().width) / 2;
        int y = (Toolkit.getDefaultToolkit().getScreenSize().height - this.getSize().height) / 2;
        this.setLocation(x, y);

        this.setVisible(true);
	}
}
