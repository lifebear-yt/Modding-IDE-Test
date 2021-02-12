package io.github.railroad.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;

import io.github.railroad.config.LanguageConfig;
import io.github.railroad.utility.UIUtils;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GenerateFabricModWindow {

	public static void displayWindow(LanguageConfig langConfig) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(langConfig.get("window.generateMod.fabric"));
		window.setWidth(400);
		window.setHeight(300);
		window.setResizable(false);

		// Creating Nodes
		Label label = new Label(langConfig.get("menu.generateMod.modId"));
		Label label1 = new Label(langConfig.get("menu.generateMod.name"));
		Label label2 = new Label(langConfig.get("menu.generateMod.package"));
		Label label3 = new Label(langConfig.get("menu.generateMod.version"));
		Label label4 = new Label(langConfig.get("menu.generateMod.title.fabric"));
		label4.setScaleX(2);
		label4.setScaleY(2);
		label4.setScaleZ(2);

		TextField tf = new TextField();
		tf.setMaxSize(200, 20);
		tf.setTranslateY(19);
		TextField tf1 = new TextField();
		tf1.setMaxSize(200, 20);
		tf1.setTranslateY(19);
		TextField tf2 = new TextField();
		tf2.setMaxSize(200, 20);
		tf2.setTranslateY(19);

		//Getting fabric versions
		List<String> versions = new ArrayList();
		JSONArray jsonArray = readJsonFromUrl("https://meta.fabricmc.net/v1/versions/game");
		for (int i = 0; i < jsonArray.length(); i++) {
			if (jsonArray.getJSONObject(i).getBoolean("stable") == true) {
				versions.add(jsonArray.getJSONObject(i).getString("version"));
			}
		}
		//Putting non-stable versions after stable versions
		for (int i = 0; i < jsonArray.length(); i++) {
			if (jsonArray.getJSONObject(i).getBoolean("stable") == false) {
				versions.add(jsonArray.getJSONObject(i).getString("version"));
			}
		}
		ComboBox c = new ComboBox(FXCollections.observableArrayList(versions));
		c.setMinHeight(20);
		c.setMinWidth(80);
		c.setTranslateX(-22);
		c.setTranslateY(18);

		Button btn2 = UIUtils.createButton(langConfig.get("menu.generateMod.confirm"), event -> {
			if (tf1.getText() != null && c.getValue() != null && tf2 != null) {
				if (System.getProperty("java.version").startsWith("1.8")) {
					window.close();
					new FabricMod(tf, tf1, tf2, c, "");
				} else {
					boolean shouldClose = JavaVersionConfirmWindow.displayWindow("Java Version Warning",
							langConfig.get("dialog.versionWarning.prompt.1") + System.getProperty("java.version")
									+ langConfig.get("dialog.versionWarning.prompt.2"),
							langConfig);
					if (shouldClose) {
						window.close();
						new FabricMod(tf, tf1, tf2, c, "");
					}
				}
			} else {
				confirmWindow(langConfig.get("dialog.fillBoxes"));
			}
		});
		btn2.setTranslateX(-35);
		Button btn3 = UIUtils.createButton(langConfig.get("menu.generateMod.cancel"), event -> {
			window.close();
		});
		btn3.setTranslateY(-35);
		btn3.setTranslateX(35);

		// This code is a bit strange. Had to take some shortcuts to prevent nodes from
		// being inaccessible
		VBox layout = new VBox(20);
		layout.getChildren().addAll(label1, label, label2, label3);
		layout.setAlignment(Pos.CENTER_LEFT);
		layout.setTranslateX(60);
		layout.setTranslateY(30);

		VBox layout2 = new VBox(12);
		layout2.getChildren().addAll(tf1, tf, tf2, c);
		layout2.setAlignment(Pos.CENTER);
		layout2.setTranslateX(0);
		layout2.setTranslateY(13);

		VBox layout3 = new VBox(10);
		layout3.getChildren().addAll(btn2, btn3);
		layout3.setAlignment(Pos.CENTER);
		layout3.setTranslateY(25);

		VBox layout4 = new VBox(10);
		layout4.getChildren().addAll(label4);
		layout4.setAlignment(Pos.BOTTOM_CENTER);
		layout4.setTranslateY(20);

		BorderPane border = new BorderPane();
		border.setTop(layout4);
		border.setLeft(layout);
		border.setBottom(layout3);
		border.setCenter(layout2);

		Scene scene = new Scene(border);
		window.setScene(scene);
		window.showAndWait();
		window.centerOnScreen();
	}
	
	public static void confirmWindow(String s) {
		Stage error = new Stage();
		VBox layout = new VBox(10);
		Label boxlabel = new Label(s);
		Button okbtn = UIUtils.createButton("OK", okevent -> {
			error.close();
		});
		layout.getChildren().addAll(boxlabel, okbtn);
		layout.setAlignment(Pos.CENTER);
		Scene scene = new Scene(layout);
		error.setScene(scene);
		error.setWidth(300);
		error.setHeight(100);
		error.showAndWait();
	}
	
	public static JSONArray readJsonFromUrl(String url) {
		try {
			InputStream is = new URL(url).openStream();
			try {
				BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
				String jsonText = readAll(rd);
				JSONArray json = new JSONArray(jsonText);
				return json;
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				is.close();
			}
		} catch (IOException e) {
			return null;
		}
		return null;
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

}