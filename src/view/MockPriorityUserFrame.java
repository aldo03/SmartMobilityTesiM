package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import user.MockPriorityUser;

public class MockPriorityUserFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel panel;
	
	private JTextField path;
	
	private JTextField receiver;
	
	private JButton sendSbf;
	
	private MockPriorityUser user;
	
	public MockPriorityUserFrame(MockPriorityUser user){
		this.user = user;
		Dimension d = new Dimension(400,200);
		this.setMaximumSize(d);
		this.setSize(d);
		this.panel = new JPanel();
		this.path = new TextFieldWithPlaceholder("Insert path here", Color.BLACK);
		this.receiver = new TextFieldWithPlaceholder("Receiver", Color.BLACK);
		this.panel.add(this.path);
		this.panel.add(this.receiver);
		this.sendSbf = new JButton("Send SBF");
		this.sendSbf.addActionListener(this);
		this.panel.add(this.sendSbf);
		this.add(this.panel);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource().equals(this.sendSbf)){
			List<List<String>> p = new ArrayList<>();
			String[] roads = this.path.getText().split("/");
			for(String s : roads){
				List<String> nodes = new ArrayList<>();
				for(String s1 : s.split(",")){
					nodes.add(s1);
				}
				p.add(nodes);
			}
			String rec = this.receiver.getText();
			this.user.setReceiver(rec);
			this.user.setPath(p);
			this.user.start();
		}
	}
	
	

}
