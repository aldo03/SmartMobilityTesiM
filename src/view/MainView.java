package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.json.JSONException;

import model.NodePath;
import model.interfaces.IInfrastructureNode;
import model.interfaces.INodePath;
import model.interfaces.msg.IResponsePathMsg;
import model.msg.ResponsePathMsg;
import user.UserDevice;
import utils.json.JSONMessagingUtils;
import utils.messaging.MessagingUtils;

public class MainView extends JFrame implements WindowListener, ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel panel;
	private JPanel simulationPanel;
	private JComboBox<String> nodes;
	private List<IInfrastructureNode> nodesSet;
	private JButton open;
	private JButton startSimulation;
	private List<UserDevice> usersList;
	private JTextField textPath;
	private JTextField textPrefixedTimes;
	private JTextField textNumUsers;
	private JTextField textInitialDelay;
	private JTextField textDelayBetweenUsers;
	private JButton addUsers;
	private JButton viewUsersList;
	private int usersCount;
	
	public MainView(List<IInfrastructureNode> nodes2, List<UserDevice> usersList){
		this.panel = new JPanel();
		this.simulationPanel = new JPanel();
		this.nodesSet = nodes2;
		this.usersList = usersList;
		this.usersCount = usersList.size();
		initGUI();
		initPanel();
		this.addWindowListener(this);
		this.add(this.panel,BorderLayout.NORTH);
		this.add(this.simulationPanel, BorderLayout.CENTER);
	}
	
	private void initPanel(){
		this.nodes = new JComboBox<String>();
		for(IInfrastructureNode n : nodesSet){
			this.nodes.addItem(n.getNodeID());
		}
		this.open = new JButton(" View info ");
		this.open.addActionListener(this);
		this.startSimulation = new JButton(" START SIMULATION ");
		this.startSimulation.addActionListener(this);
		this.viewUsersList = new JButton(" VIEW USERS ");
		this.viewUsersList.addActionListener(this);
		this.setLayout(new BorderLayout());
		this.panel.setOpaque(false);
		this.panel.add(new JLabel("Select the Node you want info on "));
		this.panel.add(this.nodes);
		this.panel.add(this.open);
		this.panel.add(this.startSimulation);
		
		this.textPath = new JTextField(15);
		this.textPrefixedTimes = new JTextField(15);
		this.textNumUsers = new JTextField(3);
		this.textInitialDelay = new JTextField(3);
		this.textDelayBetweenUsers = new JTextField(3);
		this.addUsers = new JButton("ADD USERS");
		this.addUsers.addActionListener(this);
		this.simulationPanel.add(new JLabel("Path:"));
		this.simulationPanel.add(this.textPath);
		this.simulationPanel.add(new JLabel("Prefixed Times:"));
		this.simulationPanel.add(this.textPrefixedTimes);
		this.simulationPanel.add(new JLabel("Num of Users:"));
		this.simulationPanel.add(this.textNumUsers);
		this.simulationPanel.add(new JLabel("Initial Delay:"));
		this.simulationPanel.add(this.textInitialDelay);
		this.simulationPanel.add(new JLabel("Delay Between Users:"));
		this.simulationPanel.add(this.textDelayBetweenUsers);
		this.simulationPanel.add(this.addUsers);
		this.simulationPanel.add(this.viewUsersList);
	}
	
	private void initGUI(){
		this.setResizable(true);
		Dimension d = new Dimension(800,600);
		this.setMaximumSize(d);
		this.setSize(d);
		this.setTitle("Main view");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int)((dim.width) / 2 - (d.getWidth() / 2)) ,
				(int)((dim.height) / 2 - (d.getHeight() / 2)));
	}

	@Override
	public void windowOpened(WindowEvent e) {		
	}

	@Override
	public void windowClosing(WindowEvent e) {		
	}

	@Override
	public void windowClosed(WindowEvent e) {		
	}

	@Override
	public void windowIconified(WindowEvent e) {		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {		
	}

	@Override
	public void windowActivated(WindowEvent e) {		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {		
	}
	
	public void replacePanel(JPanel panel) {
		Container c = this.getContentPane();
		c.removeAll();
		c.add(panel);
		this.repaint();
		panel.updateUI();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(open)){
			NodeView nv = new NodeView(this.nodes.getSelectedItem().toString());
			nv.setVisible(true);
		} else if(e.getSource().equals(startSimulation)){
			for(UserDevice user : usersList){
				user.start();
			}
			usersList.clear();
		} else if(e.getSource().equals(addUsers)){
			List<IInfrastructureNode> listNodes = new ArrayList<>();
			String path = this.textPath.getText();
			Scanner s = new Scanner(path);
			while(s.hasNextInt()){
				int i = s.nextInt();
				listNodes.add(this.nodesSet.get(i-1));
			}
			s.close();
			INodePath nodePath = new NodePath(listNodes);
			
			List<Integer> times = new ArrayList<>();
			String prefTimes = this.textPrefixedTimes.getText();
			Scanner s1 = new Scanner(prefTimes);
			while(s1.hasNextInt()){
				int i = s1.nextInt();
				times.add(i);
			}
			s1.close();
			int numUsers = Integer.parseInt(this.textNumUsers.getText());
			int inDelay = Integer.parseInt(this.textInitialDelay.getText());
			int delayBetween = Integer.parseInt(this.textDelayBetweenUsers.getText());
			int tempDelay = inDelay;
			for(int i =0; i<numUsers;i++){
				IResponsePathMsg msg = new ResponsePathMsg(MessagingUtils.RESPONSE_PATH, "User-Device-"+this.usersCount,Arrays.asList(nodePath), "localhost");
				UserDevice user;
				try {
					user = new UserDevice(nodePath.getPathNodes().get(0), nodePath.getPathNodes().get(nodePath.getPathNodes().size()-1), 
							times, JSONMessagingUtils.getStringfromResponsePathMsg(msg),tempDelay);
					this.usersList.add(user);
					tempDelay+=delayBetween;
					this.usersCount++;
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		} else if(e.getSource().equals(viewUsersList)){
		      ListView v = new ListView(this.usersList);
		      v.setVisible(true);
	    }
	}

}
