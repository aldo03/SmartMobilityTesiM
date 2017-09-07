package view;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.*;
import utils.mongodb.MongoDBUtils;

public class NodeView extends JFrame implements WindowListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final static int extraWindowWidth = 500;
	final static String TRAVEL_TIMES = "Travel Times";
    final static String EXPECTED_VEHICLES = "Expected vehicles";
    final static String CURRENT_TIMES = "Current Times";
    
    private String nodeId;
    private JButton refreshTravelTimes,refreshExpectedVehicles,refreshCurrentTimes, refreshSensorValues;
    private JLabel currTimesLbl;
    private JPanel card1, card2, card3, card4;
    private JScrollPane sp1, sp2, sp3;
    private JTable t1, t2, t3;
    private JLabel temperature, humidity;
    private Map<String, List<Integer>> travelTimes, expectedVehicles, currentTimes;
    private Map<String, List<String>> expectedVehiclesTimes;
    
	public NodeView(String nodeId){
		this.nodeId = nodeId;
		initGUI();
		initRefreshButtons();
		initTables();
		this.addComponentToPane(this.getContentPane());
	}
	
	private void initGUI(){
		this.setResizable(false);
		Dimension d = new Dimension(800,600);
		this.setMaximumSize(d);
		this.setSize(d);
		this.setTitle(this.nodeId + " view");	
		
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int)((dim.width) / 2 - (d.getWidth() / 2)) ,
				(int)((dim.height) / 2 - (d.getHeight() / 2)));
	}
	
	private void initRefreshButtons(){
		this.refreshTravelTimes = new JButton("REFRESH");
		this.refreshTravelTimes.addActionListener(this);
		this.refreshExpectedVehicles = new JButton("REFRESH");
		this.refreshExpectedVehicles.addActionListener(this);
		this.refreshCurrentTimes = new JButton("REFRESH");
		this.refreshCurrentTimes.addActionListener(this);
		this.refreshSensorValues = new JButton("REFRESH");
		this.refreshSensorValues.addActionListener(this);
	}
	
	private void initTables(){
		//Create the "cards".
		this.card1 = new JPanel() {
			private static final long serialVersionUID = 1L;
            public Dimension getPreferredSize() {
                Dimension size = super.getPreferredSize();
                size.width += extraWindowWidth;
                return size;
            }
        };
        this.card2 = new JPanel();        
        this.card3 = new JPanel();
        this.card4 = new JPanel();
        this.card4.setLayout(new FlowLayout(FlowLayout.CENTER, 200, 30));
        
		this.travelTimes = MongoDBUtils.getTimeTravels(this.nodeId);
		this.t1 = this.createTable(travelTimes, true, card1, "These are the future Travel Times from node " + this.nodeId + " to its near nodes", this.refreshTravelTimes);
		this.fillTable(travelTimes, true, t1);
		this.sp1 = new JScrollPane(t1, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		this.sp1.repaint();
		
		this.expectedVehicles = MongoDBUtils.getExpectedVehicles(this.nodeId);
		this.getExpectedVehicleTimes(expectedVehicles);
		this.t2 = this.createTable(expectedVehicles, false, card2, "These are the scheduled times of Expected Vehicles", this.refreshExpectedVehicles);
		this.expectedVehiclesTimes = this.getExpectedVehicleTimes(expectedVehicles);
		this.fillTable(expectedVehiclesTimes, t2);
		this.sp2 = new JScrollPane(t2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		this.sp2.repaint();
		
		this.currentTimes = MongoDBUtils.getCurrentTimes(this.nodeId);
		this.currTimesLbl = new JLabel("These are the lastest Travel times towards near nodes");
		this.t3 = this.createTable(currentTimes, false, card3, this.currTimesLbl.getText(), this.refreshCurrentTimes );
		this.fillTable(currentTimes, false, t3);
		this.sp3 = new JScrollPane(t3, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);	
		this.sp3.repaint();
	}
	
	private Map<String, List<String>> getExpectedVehicleTimes(Map<String, List<Integer>> map){
		Map<String, List<String>> expectedVehiclesTimes = new HashMap<String, List<String>>();
		for(Map.Entry<String, List<Integer>> entry : map.entrySet()){
			List<Integer> times = entry.getValue();
			List<String> newTimes = new ArrayList<String>();
			for(Integer i : times){
				LocalDateTime dateTime = LocalDateTime.ofEpochSecond(i, 0, ZoneOffset.UTC);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm:ss,a", Locale.ENGLISH);
				String formattedDate = dateTime.format(formatter);
				newTimes.add(formattedDate);
			}
			expectedVehiclesTimes.put(entry.getKey(), newTimes);
		}
		return expectedVehiclesTimes;
	}
	
	
    public void addComponentToPane(Container pane) {
        JTabbedPane tabbedPane = new JTabbedPane();   
        this.card1.add(this.sp1);
        this.card2.add(this.sp2);
        this.card3.add(this.sp3);
        this.card4.add(new JLabel("You are currently watching real-time info about node " + this.nodeId + " ."));
        this.temperature = new JLabel("This is the current temperature value detected on the node:   " + MongoDBUtils.getTempHum(this.nodeId).getFirst());
        this.card4.add(this.temperature);
        this.humidity = new JLabel("This is the current humidity value detected on the node:   " + MongoDBUtils.getTempHum(this.nodeId).getSecond());
        this.card4.add(this.humidity);
        this.card4.add(this.refreshSensorValues);
        tabbedPane.addTab(TRAVEL_TIMES, card1);
        tabbedPane.addTab(EXPECTED_VEHICLES, card2);
        tabbedPane.addTab(CURRENT_TIMES,card3);
        tabbedPane.addTab("Environmental data", card4);
        pane.add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JTable createTable(Map<String, List<Integer>> tableContent, boolean travelTimes, JPanel p, String s, JButton b){
    	int max = 1;
    	for (String l : tableContent.keySet()){
    		if(tableContent.get(l).size() >= max)
    			max = tableContent.get(l).size()+1;
    	}
    	JTable table = new JTable(tableContent.keySet().size(), max);
    	p.add(new JLabel(s));
    	p.add(b);
    	if(tableContent.keySet().size() > 0){
    		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        	table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("Node");
        	
        	if(travelTimes){
    			int range = 0;
    			for (int i = 1; i < table.getColumnCount(); i++) {
    				table.getTableHeader().getColumnModel().getColumn(i).setHeaderValue(range + "/" + (range + 5));
    				range += 5;
    			}
        	}	
    	}
    	return table;
    }
    
    private JTable createTable(Map<String, List<Integer>> tableContent){
    	int max = 1;
    	for (String l : tableContent.keySet()){
    		if(tableContent.get(l).size() >= max)
    			max = tableContent.get(l).size()+1;
    	}
    	JTable table = new JTable(tableContent.keySet().size(), max);

    	if(tableContent.keySet().size() > 0){
    		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        	table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("Node");
    	}
    	return table;
    }
    
    private JTable createTableStrings(Map<String, List<String>> tableContent){
    	int max = 1;
    	for (String l : tableContent.keySet()){
    		if(tableContent.get(l).size() >= max)
    			max = tableContent.get(l).size()+1;
    	}
    	JTable table = new JTable(tableContent.keySet().size(), max);

    	if(tableContent.keySet().size() > 0){
    		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        	table.getTableHeader().getColumnModel().getColumn(0).setHeaderValue("Node");
    	}
    	return table;
    }
    
    
    private void fillTable(Map<String, List<Integer>> tableContent, boolean travelTimes, JTable table){ 		
    	SwingUtilities.invokeLater(new Runnable(){public void run(){
    	int j = 0;
    	for(String s : tableContent.keySet()){
    		table.setValueAt(s, j, 0);        		
        		for(int k = 1; k < tableContent.get(s).size() + 1; k++){
        			table.setValueAt(tableContent.get(s).get(k-1), j, k);
        		}
        	j++;
    	} 	 
    	}});
    }
    
    private void fillTable(Map<String, List<String>> tableContent, JTable table){
    	SwingUtilities.invokeLater(new Runnable(){public void run(){
    		int j = 0;
        	if(tableContent.entrySet().size() > 0){
        		
        		for(String s : tableContent.keySet()){
            		table.setValueAt(s, j, 0);        		
                		for(int k = 1; k < tableContent.get(s).size() + 1; k++){
                			table.setValueAt(tableContent.get(s).get(k-1), j, k);
                		}
                	j++;
            	}
        	}
    	}});
    	
    	 	 	
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(this.refreshTravelTimes)){		
			this.fillTable(MongoDBUtils.getTimeTravels(this.nodeId), true, t1);
			this.sp1.repaint();
		} else if(e.getSource().equals(this.refreshExpectedVehicles)){
			this.expectedVehicles = MongoDBUtils.getExpectedVehicles(this.nodeId);
			JTable v = this.createTableStrings(this.getExpectedVehicleTimes(this.expectedVehicles));
			sp2.add(v);
			this.fillTable(this.getExpectedVehicleTimes(MongoDBUtils.getExpectedVehicles(this.nodeId)), v);
			this.sp2.setViewportView(v);
		} else if(e.getSource().equals(this.refreshCurrentTimes)){	
			this.currentTimes = MongoDBUtils.getCurrentTimes(this.nodeId);
			JTable t = this.createTable(currentTimes);	
			sp3.add(t);
			this.fillTable(MongoDBUtils.getCurrentTimes(this.nodeId), false, t);
			this.sp3.setViewportView(t);
		} else if(e.getSource().equals(this.refreshSensorValues)){
			this.temperature.setText("This is the current temperature value detected on the node:   " + MongoDBUtils.getTempHum(this.nodeId).getFirst());
			this.humidity.setText("This is the current humidity value detected on the node:   " + MongoDBUtils.getTempHum(this.nodeId).getSecond());
		}
		
	}

}
