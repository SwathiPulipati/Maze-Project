import javax.swing.*; // printing visuals
import java.util.*;
import java.awt.*; // colors and graphical stuff
import java.awt.event.*;
import java.io.*;

public class MazeProject extends JPanel implements KeyListener{

	JFrame frame;
	String[][] maze;
	boolean[][] visited;
	Hero hero;
	int startR, startC, endR, endC;
	char endDir;
	boolean in2D = true, isRunning = true;
	boolean showMiniMap = false;
	ArrayList<Wall> walls;

	public MazeProject(){
			frame = new JFrame();
					frame.add(this);

			frame.setSize(1000, 600);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			//stuff happens here ie making the maze
			setMaze();
			frame.addKeyListener(this);

			frame.setVisible(true); // always last

	}

	public void paintComponent(Graphics g){
		super.paintComponent(g); 	//functions like a giant eraser
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.PINK);
		g2.setColor(new Color(0,0,0));

		if(isRunning){
			if(in2D){
				frame.setSize(1000,600);
				g2.fillRect(0,0,1000,600);
				int scale = 15;
				g2.setColor(Color.BLUE);
				for(int r=0; r<maze.length; r++){
					for(int c=0; c<maze[r].length; c++){
						if(maze[r][c].equals("*"))
							g2.fillRect(c*scale+20,r*scale+20,scale,scale);
					}
				}

				g2.setColor(Color.WHITE);
				g2.fillOval(hero.getC()*scale+19, hero.getR()*scale+19,scale,scale);
				g2.setFont(new Font("Purisa", Font.PLAIN, 20));
				g2.drawString("Steps: " +Integer.toString(hero.getSteps()), 30, 420);
			}
			else{
				frame.setSize(615,638);
				g2.fillRect(0,0,600,600);
				for(Wall wall: walls){ 
					g2.setPaint(wall.getPaint()); 
					g2.fillPolygon(wall.getPoly());
					g2.setColor(Color.BLACK);
					g2.drawPolygon(wall.getPoly());
				}
				if (showMiniMap){
					g2.setColor(Color.GRAY);
					g2.fillRect(500,500,100,100);
					g2.setColor(Color.BLACK);
					g2.fillRect(505,505, 90, 90);
					g2.setColor(Color.LIGHT_GRAY);
					int scale = 10;

					int rPos = hero.getR();
					int cPos = hero.getC();
					int rCount = 0;
					int cCount = 0;
					for(int r=rPos-4; r<=rPos+4; r++){
						for(int c=cPos-4; c<=cPos+4; c++){
							try{
								if(maze[r][c].equals("*")){
									g2.fillRect(505+(cCount*scale), 505+(rCount*scale), scale, scale);
								}
							}catch(ArrayIndexOutOfBoundsException e){
							}
							cCount++;
						}
						cCount = 0;
						rCount++;
					}
					g2.setColor(Color.WHITE);
					g2.fillOval(545, 545,scale,scale);
				}
				g2.setColor(Color.BLACK);
				g2.setFont(new Font("Purisa", Font.PLAIN, 20));
				g2.drawString("Steps: " +Integer.toString(hero.getSteps()), 35, 25);

			//draw compass
				g2.setColor(Color.BLACK);
				g2.fillOval(518, 28, 65, 65);
				g2.setColor(new Color(80,80,80));
				g2.drawPolygon(new int[]{550,553,550,547}, new int[]{37,47,57,47}, 4); // north
				g2.drawPolygon(new int[]{550,553,550,547}, new int[]{63,73,83,73}, 4); //south
				g2.drawPolygon(new int[]{527,537,547,537}, new int[]{60,57,60,63}, 4); //west
				g2.drawPolygon(new int[]{553,563,573,563}, new int[]{60,57,60,63}, 4); //east

				g2.setColor(Color.RED);
				switch(hero.getDir()){
					case 'E': g2.fillPolygon(new int[]{553,563,573,563}, new int[]{60,57,60,63}, 4); 
						break;
					case 'N': g2.fillPolygon(new int[]{550,553,550,547}, new int[]{37,47,57,47}, 4);
						break;
					case 'W': g2.fillPolygon(new int[]{527,537,547,537}, new int[]{60,57,60,63}, 4);
						break;
					case 'S': g2.fillPolygon(new int[]{550,553,550,547}, new int[]{63,73,83,73}, 4);
						break;
				}
			}
		}else{
			frame.setSize(1000,600);
			g2.fillRect(0,0,1000,600);
			g2.setColor(Color.BLUE);
			g2.fillRect(300,200,400,200);
			g2.setColor(Color.WHITE);
			g2.setFont(new Font("Purisa", Font.PLAIN, 20));
			g2.drawString("Great Job! You made it out of the maze!", 325,280);
			g2.drawString("It took you " +Integer.toString(hero.getSteps())+ " steps to make it out.", 325, 320);
		}
    }
    

	public void setMaze(){
		File file = new File("C:\\Users\\Swathi Pulipati\\OneDrive\\Documents\\maze1.txt");
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String text;
			maze = new String[25][59];
			visited = new boolean[25][59];
			int r=0;
			boolean first = true;
			while((text = br.readLine()) != null){

				if(first){
					String[] pieces = text.split(" ");
					startR = Integer.parseInt(pieces[0]);
					startC = Integer.parseInt(pieces[1]);
					hero = new Hero(startR, startC, pieces[2].charAt(0));
					endR = Integer.parseInt(pieces[3]);
					endC = Integer.parseInt(pieces[4]);
					endDir = pieces[2].charAt(0);
					first = false;
				}else{
					String[] pieces = text.split("");
					maze[r] = pieces;
					r++;
				}
			}
			br.close();
		}catch(Exception e){e.printStackTrace();}


	}

//three methods for the KeyListener interface
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == 32){
			in2D = !in2D;
		}
		hero.move(e.getKeyCode());
		if(!in2D)
			set3D();

		if(e.getKeyCode() == 75 && !in2D){
			showMiniMap = !showMiniMap;
		}
		frame.repaint();
	}

	public void keyReleased(KeyEvent e){}

	public void keyTyped(KeyEvent e){}

	public void set3D(){
		walls = new ArrayList<Wall>();
		int visDis = 5;
		int wallSize = 50;

//check for front wall
		for(int i=1; i<visDis; i++){
			int row = hero.getR();
			int col = hero.getC();
			switch(hero.getDir()){
				case 'E':
					try{
						if(maze[row][col+i].equals("*")){
							frontWall(i-1, wallSize);
							visDis = i;
						}else if((row == endR && col+i == endC) || (row == startR && col+i == startC)){
							visDis = i;
						}
					}catch(ArrayIndexOutOfBoundsException e){}
					break;
				case 'N':
					try{
						if(maze[row-i][col].equals("*")){
							frontWall(i-1, wallSize);
							visDis = i;
						}else if((row-i == endR && col == endC) || (row-i == startR && col == startC)){
							visDis = i;
						}
					}catch(ArrayIndexOutOfBoundsException e){}
					break;
				case 'W':
					try{
						if(maze[row][col-i].equals("*")){
							frontWall(i-1, wallSize);
							visDis = i;
						}else if((row == endR && col-i == endC) || (row == startR && col-i == startC)){
							visDis = i;
						}
					}catch(ArrayIndexOutOfBoundsException e){}
					break;
				case 'S':
					try{
						if(maze[row+i][col].equals("*")){
							frontWall(i-1, wallSize);
							visDis = i;
						}else if((row+i == endR && col == endC) || (row+i == startR && col == startC)){
							visDis = i;
						}
					}catch(ArrayIndexOutOfBoundsException e){}
					break;
			}

		}

//draw in left rectangles
		for(int i=0; i<visDis; i++){
			leftWall(i, wallSize);
			int[] x = {wallSize*i,50+wallSize*i,50+wallSize*i,wallSize*i};
			int[] y = {50+wallSize*i,50+wallSize*i,550-wallSize*i,550-wallSize*i};
			walls.add(new Wall(x,y,"left rect",i));
		}

//draw in right rectangles
		for(int i=visDis-1; i>=0; i--){
			rightWall(i, wallSize);
			int[] x = {550-wallSize*i,600-wallSize*i,600-wallSize*i,550-wallSize*i};
			int[] y = {50+wallSize*i,50+wallSize*i,550-wallSize*i,550-wallSize*i};
			walls.add(new Wall(x,y,"right rect", i));
		}

//draw in left trapezoids
		for(int i=visDis-1; i>=0; i--){
			int row = hero.getR();
			int col = hero.getC();
			int dir = hero.getDir();
				switch(dir){
					case 'E':
						try{
							if(maze[row-1][col+i].equals("*"))
								leftWall(i, wallSize);
							if(maze[row+1][col+i].equals("*"))
								rightWall(i, wallSize);
						}catch(ArrayIndexOutOfBoundsException e){}
						break;
					case 'N':
						try{
							if(maze[row-i][col-1].equals("*"))
								leftWall(i, wallSize);
							if(maze[row-i][col+1].equals("*"))
								rightWall(i, wallSize);
						}catch(ArrayIndexOutOfBoundsException e){}
						break;
					case 'W':
						try{
							if(maze[row+1][col-i].equals("*"))
								leftWall(i, wallSize);
							if(maze[row-1][col-i].equals("*"))
								rightWall(i, wallSize);
						}catch(ArrayIndexOutOfBoundsException e){}
						break;
					case 'S':
						try{
							if(maze[row+i][col+1].equals("*"))
								leftWall(i, wallSize);
							if(maze[row+i][col-1].equals("*"))
								rightWall(i, wallSize);
						}catch(ArrayIndexOutOfBoundsException e){}
						break;
				}
		}


// draw ceiling
		for(int i=visDis-1; i>=0; i--){
			int[] x = {wallSize*i,600-wallSize*i,550-wallSize*i,50+wallSize*i};
			int[] y = {wallSize*i,wallSize*i,50+wallSize*i,50+wallSize*i};
			walls.add(new Wall(x,y,"ceiling",i));
		}

//draw floor
		for(int i=visDis-1; i>=0; i--){
			int[] x = {wallSize*i,600-wallSize*i,550-wallSize*i,50+wallSize*i};
			int[] y = {600-wallSize*i,600-wallSize*i,550-wallSize*i,550-wallSize*i};

			boolean added = false;
			try{
				switch(hero.getDir()){
					case 'E': 
						if (visited[hero.getR()][hero.getC()+i]){
							walls.add(new Wall(x,y,"visited",i));
							added = true;
						}
						break;
					case 'N': 
						if (visited[hero.getR()-i][hero.getC()]){
							walls.add(new Wall(x,y,"visited",i));
							added = true;
						}
						break;
					case 'W': 
						if (visited[hero.getR()][hero.getC()-i]){
							walls.add(new Wall(x,y,"visited",i));
							added = true;
						}
						break;
					case 'S': 
						if (visited[hero.getR()+i][hero.getC()]){
							walls.add(new Wall(x,y,"visited",i));
							added = true;
						}
						break;
				}
			}catch(Exception e){}
			if(!added)
				walls.add(new Wall(x,y,"floor",i));
		}

	}


	public void leftWall(int i, int wallSize){
		int[] x = {wallSize*i,50+wallSize*i,50+wallSize*i,wallSize*i};
		int[] y = {wallSize*i,50+wallSize*i,550-wallSize*i,600-wallSize*i};
		walls.add(new Wall(x,y, "left", i));
	}

	public void rightWall(int i, int wallSize){
		int[] x = {600-wallSize*i,550-wallSize*i,550-wallSize*i,600-wallSize*i};
		int[] y = {wallSize*i,50+wallSize*i,550-wallSize*i,600-wallSize*i};
		walls.add(new Wall(x,y, "right", i));
	}

	public void frontWall(int i, int wallSize){
		int[] x = {50+wallSize*i, 550-wallSize*i, 550-wallSize*i, 50+wallSize*i};
		int[] y = {50+wallSize*i, 50+wallSize*i, 550-wallSize*i, 550-wallSize*i};
		walls.add(new Wall(x,y, "front", i));
	}


	public class Wall{
		int[] x, y;
        int i;
        String type;
		public Wall(int[] x, int[] y, String type, int i){
			this.x = x;
			this.y = y;
            this.type = type;
            this.i = i;
		}

		public Polygon getPoly(){
			return new Polygon(x, y, 4);
		}

		public GradientPaint getPaint(){
			int shrink = 50;
			switch(type){
				case "left":
				case "right": return new GradientPaint(x[0],y[0], new Color(255-shrink*i, 255-shrink*i, 255-shrink*i), x[1], y[0], new Color(255-shrink*(i+1), 255-shrink*(i+1), 255-shrink*(i+1)));
				case "visited":  return new GradientPaint(x[0], y[0], new Color(255-shrink*i, 255-shrink*i, 0), x[0], y[3], new Color(255-shrink*(i+1), 255-shrink*(i+1), 0));
				default: return new GradientPaint(x[0], y[0], new Color(255-shrink*i, 255-shrink*i, 255-shrink*i), x[0], y[3], new Color(255-shrink*(i+1), 255-shrink*(i+1), 255-shrink*(i+1)));
			}
		}
	}

	public static void main(String[] args){
			MazeProject mp = new MazeProject();
	}

	public class Hero{
		private char dir;
		private int r, c;
		private int steps = 0;
		

		public Hero(int r, int c, char dir){
			this.r = r;
			this.c = c;
			this.dir = dir;
		}

		public char getDir(){
			return dir;
		}

		public int getR(){
			return r;
		}

		public int getC(){
			return c;
		}

		public int getSteps(){
			return steps;
		}

		public void move(int key){
			switch(key){
				//turn left
				case 37: switch(dir){
							case 'E': dir = 'N';
								break;
							case 'N': dir = 'W';
								break;
							case 'W': dir = 'S';
								break;
							case 'S': dir = 'E';
					     }
					break;
				//forward
				case 38: switch(dir){
							case 'E':
									try{
										if(!maze[r][c+1].equals("*")){
											c++;
											steps++;
										}
									}
									catch(ArrayIndexOutOfBoundsException e){}
								break;
							case 'N':
									try{
										if(!maze[r-1][c].equals("*")){
											r--;
											steps++;
										}
									}
									catch(ArrayIndexOutOfBoundsException e){}
							break;
							case 'W':
									try{
										if(!maze[r][c-1].equals("*")){
											c--;
											steps++;
										}
									}
									catch(ArrayIndexOutOfBoundsException e){}
								break;
							case 'S':
									try{
										if(!maze[r+1][c].equals("*")){
											r++;
											steps++;
										}
									}
									catch(ArrayIndexOutOfBoundsException e){}
					     }
					break;
				//turn right
				case 39: switch(dir){
							case 'E': dir = 'S';
								break;
							case 'N': dir = 'E';
								break;
							case 'W': dir = 'N';
								break;
							case 'S': dir = 'W';
					     }
					break;
			}
			try{
				visited[r][c] = true;
			}catch(Exception e){}
			if(r == endR && c == endC)
				isRunning = false;
		}

	}

}