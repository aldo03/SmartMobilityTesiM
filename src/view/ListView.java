package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import user.UserDevice;

public class ListView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel panel;
	private List<UserDevice> users;
	
	public ListView(List<UserDevice> users){
		this.panel = new JPanel();
		this.users = users;
		initGUI();
		initPanel();
		this.add(new JScrollPane(this.panel));
	}
	
	private void initPanel(){
		this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.Y_AXIS));
		JLabel lbl = new JLabel("THIS IS THE CURRENT LIST OF USERS ");
		lbl.setHorizontalAlignment((int)Component.CENTER_ALIGNMENT);
		this.panel.add(lbl);
		for(int i = 0; i < this.users.size(); i++){
			JLabel l = new JLabel("> "+this.users.get(i).toString()+" <");
			l.setHorizontalAlignment((int)Component.CENTER_ALIGNMENT);
			this.panel.add(l);
		}
		this.panel.setAlignmentX((int)Component.CENTER_ALIGNMENT);
		
		
	}
	
	private void initGUI(){
		this.setResizable(true);
		Dimension d = new Dimension(1000,400);
		this.setMaximumSize(d);
		this.setSize(d);
		this.setTitle("List of Users");
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int)((dim.width) / 2 - (d.getWidth() / 2)) ,
				(int)((dim.height) / 2 - (d.getHeight() / 2)));
	}

}
