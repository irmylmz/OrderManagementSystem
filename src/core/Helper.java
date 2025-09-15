package core;

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

}
