package hardcoded.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import javax.swing.filechooser.FileSystemView;

public class RustSettings {
	private static final String LAST_FILE_PATH = "lastFilePath";
	
	private final Properties settings;
	private final File file;
	
	public RustSettings() {
		File parent = new File(System.getProperty("java.io.tmpdir"));
		file = new File(parent, ".rust.picasso.properties");
		
		settings = new Properties();
		load();
	}
	
	public String getLastFilePath() {
		return settings.getProperty(LAST_FILE_PATH);
	}
	
	public void setLastFilePath(String path) {
		setProperty(LAST_FILE_PATH, path);
	}
	
	private void setProperty(String key, String value) {
		settings.setProperty(key, value);
		save();
	}
	
	private void load() {
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} else {
			try(FileInputStream stream = new FileInputStream(file)) {
				settings.load(stream);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		settings.putIfAbsent(LAST_FILE_PATH, FileSystemView.getFileSystemView().getHomeDirectory().toString());
	}
	
	private void save() {
		try(FileOutputStream stream = new FileOutputStream(file)) {
			settings.store(stream, "");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
