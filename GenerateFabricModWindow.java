package io.github.railroad.objects;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

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

// TODO: Come up with a cleaner way of doing this
public class GenerateFabricModWindow {

	public static void displayWindow(LanguageConfig langConfig) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(langConfig.get("window.generateMod.fabric"));
		window.setWidth(400);
		window.setHeight(300);
		window.setResizable(false);

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

		List<String> versions = new ArrayList();
		JSONArray jsonArray = readJsonFromUrl("https://meta.fabricmc.net/v1/versions/game");
		for (int i = 0; i < jsonArray.length(); i++) {
			if (jsonArray.getJSONObject(i).getBoolean("stable") == true) {
				versions.add(jsonArray.getJSONObject(i).getString("version"));
			}
		}
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
					generateMod(window, tf, tf1, tf2, c);
				} else {
					boolean shouldClose = JavaVersionConfirmWindow.displayWindow("Java Version Warning",
							langConfig.get("dialog.versionWarning.prompt.1") + System.getProperty("java.version")
									+ langConfig.get("dialog.versionWarning.prompt.2"),
							langConfig);
					if (shouldClose) {
						generateMod(window, tf, tf1, tf2, c);
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

	public static void generateMod(Stage window, TextField tf, TextField tf1, TextField tf2, ComboBox c) {
		// TODO: Get workspace
		File workspace = new File(/* workspace */"");
		if (!workspace.exists()) {
			workspace.mkdirs();
		}
		File proj = new File(workspace.getAbsolutePath() + "\\" + tf1.getText());
		if (!proj.exists()) {
			proj.mkdirs();
		}
		File packageDir = new File(proj + "\\" + "src\\main\\java\\" + tf2.getText().replace(".", "\\"));
		if (!packageDir.exists()) {
			packageDir.mkdirs();
		}
		File main = new File(packageDir + "\\" + tf1.getText() + ".java");
		if (!main.exists()) {
			try {
				main.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		File assetsDir = new File(proj + "\\" + "src\\main\\resources\\assets\\" + tf.getText());
		if (!assetsDir.exists()) {
			assetsDir.mkdirs();
		}
		File dataDir = new File(proj + "\\" + "src\\main\\resources\\data\\" + tf.getText());
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
		File gprop = new File(proj + "\\" + "gradle.properties");
		if (!gprop.exists()) {
			try {
				gprop.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			setGradleProperties(tf1.getText(), tf.getText(), tf2.getText(), c.getValue().toString(),
					proj + "\\" + "gradle.properties");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		window.close();
		// TODO: Setup the other fabric modding stuff here
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

	static InputStream inputStream;

	public static void setGradleProperties(String name, String modID, String packageString, String version, String file)
			throws FileNotFoundException {
		Properties prop = new Properties();
		String propFileName = file;

		inputStream = new FileInputStream(new File(file));

		if (inputStream != null) {
			try {
				prop.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
		}

		JSONArray json = readJsonFromUrl("https://meta.fabricmc.net/v1/versions/loader/" + version);
		JSONObject properties = json.getJSONObject(0);

		String fabricVersion = "0.30.2+1.17";

		URL url;
		try {
			url = new URL("https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/maven-metadata.xml");
			BufferedReader read = new BufferedReader(new InputStreamReader(url.openStream()));
			String inputLine;
			while ((inputLine = read.readLine()) != null)
				if (inputLine.contains("release")) {
					fabricVersion = inputLine.replace(" ", "").replace("<release>", "").replace("</release>", "");
				}
			read.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		prop.setProperty("org.gradle.jvmargs", "-Xmx2G");
		prop.setProperty("minecraft_version", version);
		prop.setProperty("yarn_mappings", properties.getJSONObject("mappings").getString("version"));
		prop.setProperty("loader_version", properties.getJSONObject("loader").getString("version"));
		prop.setProperty("mod_version", "1.0.0");
		prop.setProperty("maven_group", packageString);
		prop.setProperty("archives_base_name", modID);
		prop.setProperty("fabric_version", fabricVersion);

		try {
			prop.store(new FileWriter(new File(file)), "Auto-Generated By Railroad");
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Setting up gradle");
		System.out.println("Created gradle.properties");

		download("gradlew.bat", /* workspace + \\ */"" + name + "\\",
				"https://github.com/FabricMC/fabric-example-mod/raw/master/gradlew.bat");
		download("build.gradle", /* workspace + \\ */"" + name + "\\",
				"https://github.com/FabricMC/fabric-example-mod/raw/master/build.gradle");
		download("settings.gradle", /* workspace + \\ */"" + name + "\\",
				"https://github.com/FabricMC/fabric-example-mod/raw/master/settings.gradle");
		download("gradlew", /* workspace + \\ */"" + name + "\\",
				"https://github.com/FabricMC/fabric-example-mod/raw/master/gradlew");
		download("gradle-wrapper.properties", /* workspace + \\ */"" + name + "\\gradle\\wrapper\\",
				"https://github.com/FabricMC/fabric-example-mod/raw/master/gradle/wrapper/gradle-wrapper.properties");
		download("gradle-wrapper.jar", /* workspace + \\ */"" + name + "\\gradle\\wrapper\\",
				"https://github.com/FabricMC/fabric-example-mod/raw/master/gradle/wrapper/gradle-wrapper.jar");
		download("fabric.mod.json", /* workspace + \\ */"" + name + "\\src\\main\\resources\\",
				"https://github.com/FabricMC/fabric-example-mod/raw/master/src/main/resources/fabric.mod.json");

		File fabricModFile = new File(/* workspace + \\ */"" + name + "\\src\\main\\resources\\fabric.mod.json");
		try {
			InputStream is = new FileInputStream(fabricModFile);
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject fabricMod = new JSONObject(jsonText);
			fabricMod.put("id", modID);
			fabricMod.put("name", name);
			fabricMod.put("icon", "assets/" + modID + "/icon.png");
			fabricMod.getJSONObject("entrypoints").getJSONArray("main").put(0, packageString +"."+ name);
			fabricMod.getJSONObject("depends").put("minecraft", version);
			System.out.println(fabricMod.toString());
			FileWriter writer = new FileWriter(fabricModFile);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser jp = new JsonParser();
            JsonElement je = jp.parse(fabricMod.toString());
            String prettyJsonString = gson.toJson(je).replace("\\u003e\\u003d", ">=");
			writer.write(prettyJsonString);
			System.out.println(prettyJsonString);
			writer.close();
			rd.close();
			is.close();
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		} 

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

	public static void unzip(String zipFile, File destFile) {
		try {
			String fileZip = zipFile;
			File destDir = destFile;
			byte[] buffer = new byte[1024];
			ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				File newFile = newFile(destDir, zipEntry);
				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}

					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());

		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();

		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}

		return destFile;
	}

	public static void download(String name, String destFile, String url) {
		try {
			File proj = new File(destFile);
			if (!proj.exists()) {
				proj.mkdirs();
			}
			URL download = new URL(url);
			BufferedInputStream in = new BufferedInputStream(download.openStream());
			FileOutputStream fileOutputStream = new FileOutputStream(new File(proj.getAbsolutePath() + "\\" + name));
			System.out.append("Downloading");
			byte dataBuffer[] = new byte[1024];
			int bytesRead;
			int dotNum = 0;
			int speed = 0;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				fileOutputStream.write(dataBuffer, 0, bytesRead);
				if (dotNum == 140) {
					dotNum = 0;
					System.out.println(".");
				}
				if (speed == 1000) {
					speed = 0;
					dotNum++;
					System.out.append(".");
				}
				speed++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}