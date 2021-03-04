package hardcoded.reader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class BorstReader {
	public static BorstData readFile(File file) throws IOException {
		List<BorstShape> list = new ArrayList<>();

		int width = 0;
		int height = 0;
		
		try(BufferedReader reader = new BufferedReader(new FileReader(file))) {
			int opacity = 2;
			int shape = 0;
			
			String line;
			{
				line = reader.readLine();
				String[] parts = line.split(",");
				width = Integer.valueOf(parts[0]);
				height = Integer.valueOf(parts[1]);
			}
			
			while((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				
				// Width, Height
				// x, y
				list.add(new BorstShape(
					Float.valueOf(parts[0]),
					Float.valueOf(parts[1]),
					Integer.valueOf(parts[2]),
					Integer.valueOf(parts[3], 16),
					opacity,
					shape
				));
			}
		} catch(IOException e) {
			throw e;
		}
		
		return new BorstData(width, height, list.toArray(new BorstShape[0]));
	}
}
