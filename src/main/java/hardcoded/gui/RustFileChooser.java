package hardcoded.gui;

import java.io.File;

import javax.swing.JFileChooser;

//TODO: Use javafx for the more system looking file choosers. (Better UX)

/**
 * File chooser for the rust drawing program
 * 
 * @author HardCoded
 */
public class RustFileChooser {
	private final RustWindow window;
	private final RustSettings settings;
	private JFileChooser chooser;

	public RustFileChooser(RustWindow window) {
		this.window = window;
		this.settings = window.settings;
		chooser = new JFileChooser(new File(settings.getLastFilePath()));
		chooser.setMultiSelectionEnabled(false);
	}
	
	public File getSelectedFile() {
		return chooser.getSelectedFile();
	}
	
	public File getCurrentDirectory() {
		return chooser.getCurrentDirectory();
	}
	
	public int showOpenDialog() {
		// Does not seem to work
//		Rectangle rect = window.getParentFrame().getBounds();
//		chooser.setLocation(
//			rect.x + (rect.width / 2) - (fileChooser.getWidth() / 2),
//			rect.y + (rect.height / 2) - (fileChooser.getHeight() / 2)
//		);
		
		chooser.setCurrentDirectory(new File(settings.getLastFilePath()));
		int result = chooser.showOpenDialog(window);
		
		if(result == JFileChooser.APPROVE_OPTION) {
			settings.setLastFilePath(chooser.getCurrentDirectory().toString());
		}
		
		return result;
	}
}
