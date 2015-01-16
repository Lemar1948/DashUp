import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;


class Global {
	
	public static final int WINDOW_WIDTH = 260;
	public static final int WINDOW_HEIGHT = 460;
	
	public static boolean gameover = false;
	public static boolean keyPressed = false;
	
	public static final int Y_INIT = WINDOW_HEIGHT-70;
	public static int yPos = WINDOW_HEIGHT-70;
	public static boolean jump = false;
	public static boolean DoorsMayMove = false;
	
	public static int[] RightDoorX = {WINDOW_WIDTH/2,WINDOW_WIDTH/2,WINDOW_WIDTH/2};
	public static int[] LeftDoorX = {0,0,0};
	public static int[] doorYmonitor = {0,0,0,0};
	public static int[] doorTypeMonitor = {0,0,0,0};
	public static int doorsNumberMonitor = 0;
	public static int doorsNumber = 0;
	public static int RightDoorY = 0;
	public static int LeftDoorY = 0;
	public static int doorIndex = 0;
	public static int absolutePath = 150;
	
	
}

class Window extends JFrame {

	private static final long serialVersionUID = 1L;
	
	JFrame window = new JFrame("DashUp!");
	PaintPanel ppanel = new PaintPanel();
	
	
	public Window() {
		
		//ppanel.setBackground(Color.red);
		
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		window.setSize(Global.WINDOW_WIDTH, Global.WINDOW_HEIGHT);
		window.setVisible(true);
		//window.setResizable(false);
		
		
		
		window.add(ppanel);
		
		Runner thread = new Runner();
		thread.start();
		
		new OpenDoors(0,20); //(type, sleeptime)
		new OpenDoors(1,15);
		new OpenDoors(2,13);
		
		
		window.addKeyListener(new Listen());
		
	}
	
}

class Listen extends KeyAdapter {

	public void keyPressed(KeyEvent e) {
		if(Global.keyPressed)
			return;
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE) {
			//System.out.println("ENTER");
			Global.jump = true;
			Global.keyPressed = true;
		}
		
	}
}


class DoorsDown extends Thread {
	int doorsDownDistanse = 100;
	int i =0 ;
	public void run() {
		while(Global.jump && i<doorsDownDistanse) {
			Global.LeftDoorY++;
			Global.RightDoorY++;
			i++;
			pause(20);
			//System.out.println("down");
		}
	}
	
	public void pause(int time) {
		try {
			currentThread();
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}

class Mover implements Runnable {
	
	Thread t;
	
	private int jumpheight = 100;
	int i = 0;
	int speed = 1;
	int s = 0;
	
	public Mover() {
		t = new Thread(this);
		t.start();
	}
	
	public void run() {
		jumpUp();
		fallDown();
	}
	
	public void jumpUp() { 
		// Global.yPos = 230  ---> critical point when doors began move down instead of ball jump up
		while(i < jumpheight && Global.yPos != 230 && !Global.gameover) {
			Global.yPos--;
			i++;
			s++;
			if(s == 20) {
				s = 0;
				speed += 2;
			}
			pause(speed);
		}
		while(i < jumpheight && !Global.gameover) {
			// this block responsible for doors moving down
			Global.RightDoorY++;
			Global.LeftDoorY++;
			Global.absolutePath++;
			
			//this used to count collisions
			Global.doorYmonitor[0]++;
			Global.doorYmonitor[1]++;
			Global.doorYmonitor[2]++;
			Global.doorYmonitor[3]++;
			
			i++;
			
			s++;
			if(s == 20) {
				s = 0;
				speed += 2;
			}
			pause(speed);
			Global.jump = true;
		}
		Global.jump = false;
		
	}
	
	public void fallDown() {
		s = 0;
		while(!Global.jump) {
			// this block responsible for doors moving down
			Global.yPos++;
			
			s++;
			if(s == 20 && speed > 2) {
				s = 0;
				speed -= 2;
			}
			pause(speed);
		}
	}
	
	public void pause(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}


class Runner extends Thread {
	
	public void run() {
		while(!Global.gameover) {
			mover();
			checkCollisions();
			pause(1);
			//System.out.println(Global.doorYmonitor[1]);
		}
	}
	
	public void mover() {
		
		if(Global.keyPressed) {
			Global.keyPressed = false;
			//System.out.println("pressed");
			new Mover();
		}
		
		
		if(Global.yPos == 391) {
			System.out.println("break");
			Global.gameover = true;	
		}
		
	}
	
	public void pause(int time) {
		try {
			currentThread();
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void checkCollisions() {
		
		
		
		for(int i = 0; i < 4; i++) {
			if(Global.doorYmonitor[i] == Global.yPos) {
				if(Global.LeftDoorX[Global.doorTypeMonitor[i]] >= -15) {
					Global.gameover = true;
					System.out.println("collision! " + Global.doorTypeMonitor[i]);
					System.out.println("door position: " + Global.doorYmonitor[i]);
					System.out.println("ball position: " + Global.yPos);
				}
			}
		}
		
		//PaintPanel.doors.get(index);
		
		//System.out.println(Global.LeftDoorY);
	}
}


class OpenDoors implements Runnable{
	
	private int type;
	private int slptime;
	private boolean open = true;
	public Thread t;
	
	public OpenDoors(int type, int slptime) {
		
		this.type = type;
		this.slptime = slptime;
		
		t = new Thread(this);
		t.start();
	}
	
	public void run() {
		while(!Global.gameover) {
			if(open) {
				 while(Global.LeftDoorX[type] != -100) {
					Global.LeftDoorX[type] -= 1;
					Global.RightDoorX[type] += 1;
						pause(slptime);
				}
				open = false;
			}
			if(!open) {
				while(Global.LeftDoorX[type] != 0 && !open) {
					Global.LeftDoorX[type] += 1;
					Global.RightDoorX[type] -= 1;
						pause(slptime);
				}
				open = true;
			}
		}
	}
	public void pause(int time) {
		try {
			
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

class Doors {
	private int ydoor;
	private int doorstype;
	
	public Doors(int ydoor, int doorstype) {
		this.ydoor = ydoor;
		this.doorstype = doorstype;
	}
	public void renderDoors(Graphics g) {
		if(doorstype == 0) {
			g.setColor(Color.blue); // black
			g.fillRect(Global.LeftDoorX[0], Global.LeftDoorY - ydoor, Global.WINDOW_WIDTH/2, 30);
			g.fillRect(Global.RightDoorX[0], Global.RightDoorY - ydoor, Global.WINDOW_WIDTH/2 -5, 30);
		}
		if(doorstype == 1) {
			g.setColor(Color.blue); //cyan
			g.fillRect(Global.LeftDoorX[1], Global.LeftDoorY - ydoor, Global.WINDOW_WIDTH/2, 30);
			g.fillRect(Global.RightDoorX[1], Global.RightDoorY - ydoor, Global.WINDOW_WIDTH/2 -5, 30);
		}
		if(doorstype == 2) {
			g.setColor(Color.blue); // blue
			g.fillRect(Global.LeftDoorX[2], Global.LeftDoorY - ydoor, Global.WINDOW_WIDTH/2, 30);
			g.fillRect(Global.RightDoorX[2], Global.RightDoorY - ydoor, Global.WINDOW_WIDTH/2 -5, 30);
		}
	}
}


class PaintPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	public static ArrayList<Doors> doors = new ArrayList<Doors>();
	private Graphics dbg;
	private Image dbImage;
	
	Random rend = new Random();
	
	
	public void renderBall(Graphics g) {
		g.setColor(Color.red);
		g.fillOval((Global.WINDOW_WIDTH/2 - 15), Global.yPos, 30, 30);
	}
	
	public void renderDoors(Graphics g) {
		if(Global.absolutePath == 150) {
			
			int doorstype = rend.nextInt(3);
			doors.add(new Doors(Global.RightDoorY, doorstype));
			Global.absolutePath = 0;
			
			//this used to count collisions
			
			if (Global.doorsNumberMonitor == 0) {
				
				Global.doorYmonitor[0] = 10;
				Global.doorYmonitor[1] = 10;
				
				Global.doorTypeMonitor[0] = doorstype;
				
			}
			if (Global.doorsNumberMonitor == 1) {
				
				Global.doorYmonitor[1] = 10;
				Global.doorYmonitor[2] = 10;
				
				Global.doorTypeMonitor[1] = doorstype;
				
			}
			if (Global.doorsNumberMonitor == 2) {
				
				Global.doorYmonitor[2] = 10;
				Global.doorYmonitor[3] = 10;
				
				Global.doorTypeMonitor[2] = doorstype;
				
			}
			if (Global.doorsNumberMonitor == 3) {
				
				Global.doorYmonitor[3] = 10;
				Global.doorYmonitor[0] = 10;
				
				Global.doorTypeMonitor[3] = doorstype;
				
				Global.doorsNumberMonitor = 0;
			}
			
			//System.out.println(Global.doorsNumberMonitor);
			Global.doorsNumber++;
			Global.doorsNumberMonitor++;
			
			
		}
		if(Global.absolutePath > 150 && Global.absolutePath <= 160) {
			int doorstype = rend.nextInt(3);
			doors.add(new Doors(Global.RightDoorY, doorstype));
			Global.absolutePath = 0;
			
//this used to count collisions
			
			if (Global.doorsNumberMonitor == 0) {
				
				Global.doorYmonitor[0] = 10;
				Global.doorYmonitor[1] = 10;
				
				Global.doorTypeMonitor[0] = doorstype;
				
			}
			if (Global.doorsNumberMonitor == 1) {
				
				Global.doorYmonitor[1] = 10;
				Global.doorYmonitor[2] = 10;
				
				Global.doorTypeMonitor[1] = doorstype;
				
			}
			if (Global.doorsNumberMonitor == 2) {
				
				Global.doorYmonitor[2] = 10;
				Global.doorYmonitor[3] = 10;
				
				Global.doorTypeMonitor[2] = doorstype;
				
			}
			if (Global.doorsNumberMonitor == 3) {
				
				Global.doorYmonitor[3] = 10;
				Global.doorYmonitor[0] = 10;
				
				Global.doorTypeMonitor[3] = doorstype;
				
				Global.doorsNumberMonitor = 0;
			}
			
			//System.out.println(Global.doorsNumberMonitor);
			Global.doorsNumber++;
			Global.doorsNumberMonitor++;
			
			
		}
		for(int i = 0; i < doors.size(); i++) {
			doors.get(i).renderDoors(g);
		}
		
	}
	
	public void renderScore(Graphics g) {
		int score;
		
		if(doors.size() < 2) {
			score = 0;
		} else {
			score = doors.size()-2;
		}
		
		g.setColor(Color.BLACK);
		g.drawString("Score: " + score, 10, 20);
	}
	
	public void paint(Graphics g) {
		
		dbImage = createImage(getWidth(), getHeight());
		dbg = dbImage.getGraphics();
		
		renderBall(dbg);
		renderDoors(dbg);
		renderScore(dbg);
		
		g.drawImage(dbImage, 0, 0, null);
		
		
		
		repaint();
		
	}
}

public class DashMain {
	public static void main(String[] args) {
		new Window();
	}
}
