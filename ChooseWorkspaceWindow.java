package io.github.railroad.objects;

import java.io.File;

import io.github.railroad.Railroad;
import io.github.railroad.config.LanguageConfig;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ChooseWorkspaceWindow extends Stage {
	
	public File workspace;
	
	public ChooseWorkspaceWindow(LanguageConfig langConfig, Railroad railroad) {
		this.setTitle("Choose Workspace");
		
		Label l = new Label("Workspace:");
		VBox v = new VBox(l);
		v.setAlignment(Pos.CENTER);
		
		TextField t = new TextField();
		t.setMaxSize(250, 25);
		VBox v2 = new VBox(t);
		v2.setAlignment(Pos.CENTER);
		
		Button b = new Button("Done");
		b.setOnAction(event -> {
			workspace = new File(t.getText());
			this.close();
			railroad.createComponents(new RailroadTopMenu(langConfig), railroad.mainWindow);
			railroad.createMainWindow();
		});
		VBox v3 = new VBox(b);
		v3.setAlignment(Pos.CENTER);
		
		Button b2 = new Button("Browse");
		b2.setOnAction(event -> {
			new ChooseFolderWindow(t, railroad);
		});
		VBox v4 = new VBox(b2);
		v4.setAlignment(Pos.CENTER);
		
		BorderPane bp = new BorderPane();
		bp.setBottom(v3);
		bp.setCenter(v2);
		bp.setLeft(v);
		bp.setRight(v4);
		
		this.setWidth(400);
		this.setHeight(150);
		this.setResizable(false);
		this.setScene(new Scene(bp));
		this.show();
	}
	
	public File getWorkspace() {
		return this.workspace;
	}

}
