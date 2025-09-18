package core;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class Helper {
	public static void setTheme() {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException 
                       | InstantiationException 
                       | IllegalAccessException 
                       | javax.swing.UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
	}
	
	public static boolean isFieldEmpty(JTextField field) {
		return field.getText().trim().isEmpty();
	}
	
	public static boolean isFieldListEmpty(JTextField [] fields) {
		for(JTextField field : fields) {
			if(isFieldEmpty(field)) return true;
		}
		return false;
	}
	
	public static boolean isEmailValid(String mail) {
		// info@patika.dev
		// @ olacak, @'tan sonra bir değer, @2tan sonra nokta olacak ve bir değer olacak
		
		if(mail == null || mail.trim().isEmpty()) return false;
		
		if(!mail.contains("@")) return false;
		
		String[] parts = mail.split("@");
		if(parts.length != 2) return false;
		
		if(parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) return false;
		
		if(!parts[1].contains(".")) return false;
		
		return true;
 	}
	
	public static void optionPaneDialog() {
		UIManager.put("OptionPane.okButtonText", "OK");
		UIManager.put("OptionPane.yesButtonText", "YES");
		UIManager.put("OptionPane.noButtonText", "NO");
	}
	
	public static void showMessage(String message) {
		String msg, title;
		
		optionPaneDialog();
		
		switch (message) {
		case "fill":
			msg = "Please fill out all fields.";
			title = "ERROR";
			break;
			
		case "done":
			msg = "The transaction was successful!";
			title = "RESULT";
			break;
			
		case "error":
			msg = "An error occurred.";
			title = "ERROR";
			break;

		default:
			msg = message;
			title = "MESSAGE";
			break;
		}
		
		JOptionPane.showMessageDialog(null, msg, title, JOptionPane.INFORMATION_MESSAGE );
		
	}

	
	public static boolean confirm(String str) {
		optionPaneDialog();
		String msg;
		if(str.equals("sure")) {
			msg = "Do you want to perform this action? ";
		}else {
			msg = str;
		}
		return JOptionPane.showConfirmDialog(null, msg,"Are you sure?",JOptionPane.YES_NO_OPTION) == 0;
	}
	
	
	
	
	
	
}
