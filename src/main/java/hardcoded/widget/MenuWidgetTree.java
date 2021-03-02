package hardcoded.widget;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;

public class MenuWidgetTree extends JPanel {
	private static final long serialVersionUID = 6102348017847998019L;
	
	private final List<ImageWidget> list;
	private JFileChooser fileChooser;
	
	private final List<PanelLabel> children = new ArrayList<>();
	
	public MenuWidgetTree(List<ImageWidget> list) {
		this.list = list;
		
		setBackground(Color.black);
		setLayout(null);
		
		fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
	}
	
	public PanelLabel createChild(ImageWidget widget) {
		PanelLabel test = new PanelLabel(widget);
		
		children.add(test);
		add(test.panel);
		
		updateChildren();
		return test;
	}
	
	public void update() {
		updateChildren();
	}
	
	protected void updateChildren() {
		int y = 0;
		for(PanelLabel label : children) {
			int height = label.panel.getHeight();
			
			label.update();
			label.panel.setLocation(0, y);
			y += height + 5;
		}
	}
	
	private class PanelLabel {
		private final ImageWidget widget;
		private JPanel panel;
		private JTextField path;
		private JTextField y_min;
		private JTextField y_max;
		private JTextField x_min;
		private JTextField x_max;
		private JTextField angle;
		
		public PanelLabel(ImageWidget widget) {
			this.widget = widget;
			panel = new JPanel();
			panel.setSize(212, 115);
			panel.setLayout(null);
			panel.setBackground(Color.darkGray);
			
			int s = 20;
			int w = 211 - s;
			int x = 40;
			int y = 0;
			
			{
				JButton button = new JButton();
				button.setBounds(5, 5, 10, 10);
				button.setFocusable(false);
				button.setBackground(Color.white);
				button.addActionListener((event) -> {
					if(isFolded()) {
						panel.setSize(212, 115);
					} else {
						panel.setSize(212, 20);
					}
					
					updateChildren();
				});
				panel.add(button);
			}
			
			{
				JLabel label = new JLabel("Image");
				label.setForeground(Color.white);
				label.setBounds(s + 1, y, x, 20);
				panel.add(label);
				
				path = new JTextField("");
				path.setBackground(Color.gray);
				path.setEditable(false);
				path.setDisabledTextColor(Color.black);
				path.setBounds(s + x, y + 1, w - x, 18);
				path.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						if(e.getButton() == MouseEvent.BUTTON1) {
							int result = fileChooser.showOpenDialog(MenuWidgetTree.this);
							
							if(result == JFileChooser.APPROVE_OPTION) {
								File file = fileChooser.getSelectedFile();
								path.setText(file.getName());
								
								try {
									widget.setImage(ImageIO.read(file));
								} catch(Exception e1) {
									
								}
							}
						}
					}
				});
				panel.add(path);
			}
			
			y += 19;
			
			{
				JLabel label = new JLabel("y_min");
				label.setForeground(Color.white);
				label.setBounds(s + 1, y, x, 20);
				panel.add(label);
				
				y_min = new JTextField("0");
				y_min.setBackground(Color.gray);
				y_min.setDisabledTextColor(Color.black);
				y_min.setBounds(s + x, y + 1, w - x, 18);
				y_min.setEnabled(false);
				panel.add(y_min);
				
			}
			
			y += 19;
			{
				JLabel label = new JLabel("y_max");
				label.setForeground(Color.white);
				label.setBounds(s + 1, y, x, 20);
				panel.add(label);
				
				y_max = new JTextField("100");
				y_max.setBackground(Color.gray);
				y_max.setDisabledTextColor(Color.black);
				y_max.setBounds(s + x, y + 1, w - x, 18);
				y_max.setEnabled(false);
				panel.add(y_max);
				
			}
			
			y += 19;
			{
				JLabel label = new JLabel("x_min");
				label.setForeground(Color.white);
				label.setBounds(s + 1, y, x, 20);
				panel.add(label);
				
				x_min = new JTextField("0");
				x_min.setBackground(Color.gray);
				x_min.setDisabledTextColor(Color.black);
				x_min.setBounds(s + x, y + 1, w - x, 18);
				x_min.setEnabled(false);
				panel.add(x_min);
				
			}
			
			y += 19;
			{
				JLabel label = new JLabel("x_max");
				label.setForeground(Color.white);
				label.setBounds(s + 1, y, x, 20);
				panel.add(label);
				
				x_max = new JTextField("100");
				x_max.setBackground(Color.gray);
				x_max.setDisabledTextColor(Color.black);
				x_max.setBounds(s + x, y + 1, w - x, 18);
				x_max.setEnabled(false);
				panel.add(x_max);
			}
			
			y += 19;
			{
				JLabel label = new JLabel("angle");
				label.setForeground(Color.white);
				label.setBounds(s + 1, y, x, 20);
				panel.add(label);
				
				angle = new JTextField("0");
				angle.setBackground(Color.gray);
				angle.setDisabledTextColor(Color.black);
				angle.setBounds(s + x, y + 1, w - x, 18);
				angle.setEnabled(false);
				panel.add(angle);
			}
		}
		
		public void update() {
			ImageStretch object = (ImageStretch)widget;
			y_min.setText(String.format("%.2f", object.y_min));
			y_max.setText(String.format("%.2f", object.y_max));
			x_min.setText(String.format("%.2f", object.x_min));
			x_max.setText(String.format("%.2f", object.x_max));
			angle.setText(String.format("%.2f", object.angle));
		}
		
		public boolean isFolded() {
			return panel.getHeight() != 115;
		}
	}
}
