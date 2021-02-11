package io.github.railroad.objects;

import java.io.File;

import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class CreateNewFolderWindow extends AbstractNewFileWindow {
	
	String txt = "";
	
	public CreateNewFolderWindow(String title, String message) {
		super(title, message);
	}

	@Override
	public boolean fileDialogBox(Stage window) {
		DirectoryChooser fileChooser = new DirectoryChooser();
		File file = fileChooser.showDialog(window);
		if (file != null) {
			file.mkdirs();
			this.pathName.setText(file.getAbsolutePath());
			this.txt = file.getAbsolutePath();
			return true;
		}
		fileChooser.setInitialDirectory(new File(""));
		return false; // Return false if "cancel" is selected
	}

}