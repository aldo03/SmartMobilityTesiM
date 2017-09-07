package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import model.interfaces.INodePath;
import user.MockUserDevice;

public class MockUserFrame extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField startNode;
	private JTextField endNode;
	private JButton requestPath;
	private JPanel northPanel;
	private JPanel centerPanel;
	private MockUserDevice mockUserDevice;
	private JScrollPane southPanel;
	private JTextArea responsePath;
	private JPanel resPanel;
	
	public MockUserFrame(MockUserDevice mockUserDevice){
		this.mockUserDevice = mockUserDevice;
		Dimension d = new Dimension(400,400);
		this.setMaximumSize(d);
		this.setSize(d);
		this.startNode = new TextFieldWithPlaceholder("Start Node ID", Color.BLACK);
		this.endNode = new TextFieldWithPlaceholder("End Node ID", Color.BLACK);
		this.northPanel = new JPanel();
		this.northPanel.add(this.startNode);
		this.northPanel.add(this.endNode);
		this.centerPanel = new JPanel();
		this.requestPath = new JButton("Request Path");
		this.requestPath.addActionListener(this);
		this.centerPanel.add(this.requestPath);
		this.resPanel = new JPanel();
		this.southPanel = new JScrollPane(this.resPanel);
		this.responsePath = new JTextArea();
		this.resPanel.add(this.responsePath);
		this.add(this.centerPanel, BorderLayout.SOUTH);
		this.add(this.northPanel, BorderLayout.NORTH);
		this.add(this.southPanel, BorderLayout.CENTER);
		
	}
	
	public void setResponsePath(INodePath path){
		this.responsePath.setText(path.toString());
		this.repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(this.requestPath)){
			this.mockUserDevice.setStartNode(this.startNode.getText());
			this.mockUserDevice.setEndNode(this.endNode.getText());
			this.mockUserDevice.start();
		}
	}
}
