package view;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

public class TextFieldWithPlaceholder extends JTextField implements MouseListener{

	  private static final long serialVersionUID = 8222440159525044025L;
	  
	  /**
	   * Creates a new TextFieldWithPlaceholder.
	   * @param text - the hint text to show
	   * @param color - border color
	   */
	  public TextFieldWithPlaceholder(String text, Color color){
	    this.setText(text);
	    //this.setBounds(fromLeft, fromTop, width, height);
	    this.setForeground(Color.GRAY);
	    this.setBorder(BorderFactory.createLineBorder(color));
	    this.addMouseListener(this);
	  }
	  
	  // Deletes the hide text and sets the foreground color
	  private void eraseText(){
	    this.setText("");
	    this.setForeground(Color.BLACK);
	  }

	  @Override
	  public void mouseClicked(MouseEvent e) {
	    this.eraseText();
	  }

	  @Override
	  public void mouseEntered(MouseEvent e) {}

	  @Override
	  public void mouseExited(MouseEvent e) {}

	  @Override
	  public void mousePressed(MouseEvent e) {}

	  @Override
	  public void mouseReleased(MouseEvent e) {}
	  

	}