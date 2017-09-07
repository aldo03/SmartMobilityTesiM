package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import user.MockUserDevice;

public class MainUserView extends JFrame implements ActionListener {
	
	private JButton but;
	private JTextField text;
	private JPanel panel;
	
	public MainUserView(){
		Dimension d = new Dimension(400,100);
		this.setMaximumSize(d);
		this.setSize(d);
		this.text = new TextFieldWithPlaceholder("User ID", Color.GRAY);
		this.but = new JButton("CREATE USER");
		this.but.addActionListener(this);
		this.panel = new JPanel();
		this.panel.add(text);
		this.panel.add(but);
		this.add(panel);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(this.but)){
			MockUserDevice mock1 = new MockUserDevice(this.text.getText());
		}
	}
}
