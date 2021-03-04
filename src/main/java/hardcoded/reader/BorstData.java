package hardcoded.reader;

public class BorstData {
	public final int width;
	public final int height;
	public final BorstShape[] instructions;
	
	public BorstData(int width, int height, BorstShape[] instructions) {
		this.width = width;
		this.height = height;
		this.instructions = instructions;
	}
}
