package io.github.railroad.objects;

import java.io.File;

import io.github.railroad.Railroad;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class ChooseFolderWindow {
	
	private File chosenFolder;
	
	public ChooseFolderWindow(TextField t, Railroad railroad) {
		DirectoryChooser fileChooser = new DirectoryChooser();
		File file = fileChooser.showDialog(new Stage().getOwner());
		if (file != null) {
			this.chosenFolder = file; 
			t.setText(file.getAbsolutePath());
			railroad.setWorkspace(file);
			// Return true if file is created
		}
		//TODO make a remembering classpath
		fileChooser.setInitialDirectory(new File("")); 
	}
	
	public File getFolder() {
		return this.chosenFolder;
	}

}