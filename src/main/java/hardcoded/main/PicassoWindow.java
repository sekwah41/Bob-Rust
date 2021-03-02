package hardcoded.main;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PicassoWindow extends JFrame {
	private static final long serialVersionUID = -3841534881068230947L;
	
	private JPanel contentPane;
	private JTextField textField_path;
	private JLabel preview_label;
	
	private JFileChooser fileChooser;
	private BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
	
	private JButton start_button;
	private AreaOld area = new AreaOld();
	
	private JFrame outline_window;
	private MouseAdapter outline_adapter = new MouseAdapter() {
		private int index = 0;
		private boolean work;
		
		public void mousePressed(MouseEvent e) {
			if(index == 0) work = true;
			
			area.setCorner(index++, e.getPoint());
			if(index > 3) {
				try {
					Thread.sleep(400);
				} catch(InterruptedException e1) {
					e1.printStackTrace();
				}
				
				outline_window.setVisible(false);
				index = 0;
				work = false;
			}
		}
		
		public void mouseMoved(MouseEvent e) {
			if(!work) return;
			
			if(index < 4) {
				area.setCorner(index, e.getPoint());
				outline_window.repaint();
			}
		}
	};
	
	public PicassoWindow() {
		setMinimumSize(new Dimension(540, 450));
		setTitle("Rust Picasso");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 538, 458);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		{
			outline_window = new JFrame() {
				private static final long serialVersionUID = 1L;
				
				// private Color transp = new Color(0, 0, 0, 3);
				private BasicStroke weight = new BasicStroke(3);
				public void paint(Graphics gr) {
					if(getBufferStrategy() == null) {
						createBufferStrategy(2);
						return;
					}
					
					super.paint(gr);
					
					Graphics2D g = (Graphics2D)gr;
					g.setStroke(weight);
					g.setColor(Color.red);
					g.drawLine(area.top_left.x, area.top_left.y, area.top_right.x, area.top_right.y);
					g.drawLine(area.top_right.x, area.top_right.y, area.bottom_right.x, area.bottom_right.y);
					g.drawLine(area.bottom_right.x, area.bottom_right.y, area.bottom_left.x, area.bottom_left.y);
					g.drawLine(area.bottom_left.x, area.bottom_left.y, area.top_left.x, area.top_left.y);
				}
			};
			outline_window.setUndecorated(true);
			outline_window.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			outline_window.setBackground(new Color(0, 0, 0, 253));
			outline_window.addMouseListener(outline_adapter);
			outline_window.addMouseMotionListener(outline_adapter);
			outline_window.setOpacity(0.3f);
		}
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {0, 0, 0, 5};
		gbl_panel.rowHeights = new int[] {0, 0, 0, 0, 5};
		gbl_panel.columnWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("Image");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 0;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		textField_path = new JTextField();
		textField_path.setText("<Select Image>");
		textField_path.setFocusable(false);
		textField_path.setDisabledTextColor(Color.BLACK);
		textField_path.setEditable(false);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		panel.add(textField_path, gbc_textField);
		textField_path.setColumns(10);
		
		fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new FileNameExtensionFilter("Images", "png", "jpg", "jpeg", "gif"));
		fileChooser.setMultiSelectionEnabled(false);
		
		JButton btnNewButton = new JButton("Browser");
		btnNewButton.setFocusable(false);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnVal = fileChooser.showOpenDialog(PicassoWindow.this);
				
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					System.out.println("File: " + file);
					textField_path.setText(file.getAbsolutePath());
					
					try {
						image = ImageIO.read(file);
					} catch(IOException e1) {
						e1.printStackTrace();
					}
					
					try {
						double w = image.getWidth();
						double h = image.getHeight();
						
						double r = 300 / h;
						int ww = (int)(w * r);
						int hh = (int)(300);
						
						BufferedImage bi = new BufferedImage(ww, hh, BufferedImage.TYPE_INT_ARGB);
						
						Graphics2D g = bi.createGraphics();
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
						g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
						g.drawImage(image, 0, 0, ww, hh, null);
						
						preview_label.setIcon(new ImageIcon(bi));
					} catch(Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 0;
		panel.add(btnNewButton, gbc_btnNewButton);
		
		JLabel lblNewLabel_1 = new JLabel("Preview");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		preview_label = new JLabel("");
		preview_label.setBorder(new LineBorder(new Color(0, 0, 0)));
		preview_label.setIcon(new ImageIcon(image));
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.gridwidth = 3;
		gbc_lblNewLabel_2.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_2.gridx = 0;
		gbc_lblNewLabel_2.gridy = 2;
		panel.add(preview_label, gbc_lblNewLabel_2);
		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 3;
		gbc_panel_1.insets = new Insets(0, 0, 0, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 3;
		panel.add(panel_1, gbc_panel_1);
		
		JButton btnNewButton_1 = new JButton("Outline");
		btnNewButton_1.setFocusable(false);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
				outline_window.setSize(size);
				outline_window.setVisible(true);
				outline_window.setState(JFrame.MAXIMIZED_BOTH);
			}
		});
		panel_1.add(btnNewButton_1);
		
		start_button = new JButton("Start");
		start_button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					start_button.setEnabled(false);
					Robot robot = null;
					
					try {
						robot = new Robot();
						Thread.sleep(200);
					} catch(Exception e1) {
						e1.printStackTrace();
					}
					
					Point last = null;
					double iw = image.getWidth();
					double ih = image.getHeight();
					
					robot.setAutoDelay(5);
					
					Point tl = area.top_left;
					
					double r = 600 / ih;
					boolean done = false;

					robot.mouseMove(tl.x, tl.y);
					robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
					
					// Idea for each area of pixels on a image create small sub images
					// and try create that tile with the fiewest amount of strokes
					// If we can do that under 10 strokes then we move to the next one
					// Meaning that 5 minutes to paint a full image
					
					boolean has = true;
					for(int y = 0; y < ih; y += 50) {
						if(done) break;
						int ipy = (int)(y * r) + tl.y;
						for(int x = 0; x < iw; x += 50) {
							Point p = MouseInfo.getPointerInfo().getLocation();
							
							if(last != null) {
								double d = last.distance(p);
								if(d > 20) {
									done = true;
									break;
								}
							}
							
							int rgb = image.getRGB(x, y);
							int rc = (rgb & 0xff0000) >>> 16;
							int gc = (rgb & 0x00ff00) >>> 8;
							int bc = (rgb & 0x0000ff);
							
							if(rc > 127) {
								int ipx = (int)(x * r) + tl.x;
								
								last = new Point(ipx, ipy);
								robot.mouseMove(ipx, ipy);
								robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
								robot.mouseMove(ipx + 1, ipy + 1);
								has = true;
							} else {
								if(has) {
									has = false;
									robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
								}
							}
						}
						
						robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
					}

					robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				} finally {
					start_button.setEnabled(true);
				}
			}
		});
		start_button.setFocusable(false);
		panel_1.add(start_button);
		
		JButton btnNewButton_3 = new JButton("Continue");
		btnNewButton_3.setEnabled(false);
		btnNewButton_3.setFocusable(false);
		panel_1.add(btnNewButton_3);
	}

}
