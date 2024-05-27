/* 
* PROJECT TITLE: Battleships
* VERSION or DATE: Version 2.3, 28.05.24
* AUTHOR: Viraaj Ravji
* DETAILS:
    * Added button register for computer grid
    * If the tile pressed has a ship, return hit
    * if not return miss
    * I am adding return if user has already shot in a postion. Not working yet
*/

/*LIBRARY*/
import javax.swing.*;    
import java.awt.*; 
import java.awt.event.*;

public class Main implements KeyListener { 
    /*Constants*/
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //Takes the height of the screen and calculates size of the GUI
    public static final int GUIHEIGHT = (int)screenSize.getHeight() / 2; 
    public static final int GUIWIDTH = GUIHEIGHT*5/2; //width of the GUI
    private static final int GUITAB = 30; //this the tab where the title is written above the GUI screen
    /*CLASS VARIABLES*/
    private static JFrame f=new JFrame("Battleships"); // Creates JFrame, the GUI window
    private static JButton[][] uGrid = new JButton[10][10]; // User Grid
    private static JButton[][] cGrid = new JButton[10][10]; // Computer Grid
    private static String[][] uGridData = new String[10][10]; 
    private static String[][] cGridData = new String[10][10]; 
    //private static int[] shipHitPoints = {2, 3, 3, 4, 5}; For later
    private static JButton[] ships = new JButton[5];
    private static int tileHeight = GUIHEIGHT/10; // Tile spacing vertical
    private static int tileWidth = GUIWIDTH/20; // Tile spacing horizontal
    private static int userSelection;
    private static int shipSelection;
    private static final JButton rButton = new JButton("Reset");; //reset JButton
    private static JLabel shipLabel;
    private static String shipRotation = "vertical";
    private static final JButton startButton = new JButton("Press to Start"); // start JButton

    /*IMAGES*/
    private static ImageIcon shipImageH2 = new ImageIcon("assets/ship_texture_h2.jpg");
    private static Image scaleshipImageH2 = shipImageH2.getImage().getScaledInstance(tileWidth, tileHeight*2,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageRH2 = new ImageIcon("assets/ship_texture_rh2.jpg");
    private static Image scaleshipImageRH2 = shipImageRH2.getImage().getScaledInstance(tileWidth*2, tileHeight,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH3 = new ImageIcon("assets/ship_texture_h3.jpg");
    private static Image scaleshipImageH3 = shipImageH3.getImage().getScaledInstance(tileWidth, tileHeight*3,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageRH3 = new ImageIcon("assets/ship_texture_rh3.jpg");
    private static Image scaleshipImageRH3 = shipImageRH3.getImage().getScaledInstance(tileWidth*3, tileHeight,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH4 = new ImageIcon("assets/ship_texture_h4.jpg");
    private static Image scaleshipImageH4 = shipImageH4.getImage().getScaledInstance(tileWidth, tileHeight*4,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageRH4 = new ImageIcon("assets/ship_texture_rh4.jpg");
    private static Image scaleshipImageRH4 = shipImageRH4.getImage().getScaledInstance(tileWidth*4, tileHeight,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH5 = new ImageIcon("assets/ship_texture_h5.jpg");
    private static Image scaleshipImageH5 = shipImageH5.getImage().getScaledInstance(tileWidth, tileHeight*5,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageRH5 = new ImageIcon("assets/ship_texture_rh5.jpg");
    private static Image scaleshipImageRH5 = shipImageRH5.getImage().getScaledInstance(tileWidth*5, tileHeight,Image.SCALE_DEFAULT);

    public Main() {
        //GUI window properties
        f.setSize(GUIWIDTH+GUITAB+(GUIWIDTH/20), GUIHEIGHT+GUITAB+GUITAB);  
        f.setLayout(null); // Not using layouts because I have 2 grids that are seperated
        f.setFocusTraversalKeysEnabled(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.setFocusable(true);
        f.addKeyListener(this);
        for (int x=0; x<ships.length; x++) {
            ships[x].addKeyListener(this);
        }
        refreshScreen();
    }

    public static void grid() { //creates the GUI and grid
        /*grid vairables*/
        String[] alphabetString = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        JLabel[] labels = new JLabel[20];
       
        //adding buttons to GUI
        for (int y=0, yPos=0, xPos=GUITAB; y<10; y++) {
            for (int x=0; x<10; x++, xPos+=tileWidth)   {
                uGrid[x][y]=new JButton("-");
                //Colour and properties of button
                uGrid[x][y].setBackground(Color.white);
                uGrid[x][y].setOpaque(true);
                uGrid[x][y].setBorderPainted(false);
                uGrid[x][y].setBounds(xPos,yPos,tileWidth,tileHeight);
                f.add(uGrid[x][y]);   
            }
            yPos+=tileHeight; 
            xPos=GUITAB;
        }

        //adding labels on GUI
        for (int x=0, yPos=0; x<11; x++, yPos+=tileHeight) {
            if (x<10) {
                labels[x] = new JLabel(alphabetString[x]); 
                labels[x].setBounds(GUITAB/3,yPos,tileWidth,tileHeight);
                f.add(labels[x]);
            } else if (x==10) {
                for (int y=0, xPos=GUITAB+(tileWidth*7/16); y<10; y++, xPos+=tileWidth) {
                    labels[y+10] = new JLabel(String.valueOf(y+1)); 
                    labels[y+10].setBounds(xPos,tileHeight*9+GUITAB+tileHeight/5,tileWidth,tileHeight);
                    f.add(labels[y+10]);
                }
            }
        }
        for (int x=0; x<5; x++) {
            switch (x) {
                case 0:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH2));
                    ships[x].setBounds(tileWidth*12,tileHeight,tileWidth,tileHeight*2);
                    break;     
                case 1:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH3));
                    ships[x].setBounds(tileWidth*15,tileHeight,tileWidth,tileHeight*3);
                    break;
                case 2:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH3));
                    ships[x].setBounds(tileWidth*18,tileHeight,tileWidth,tileHeight*3);
                    break;
                case 3:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH4));
                    ships[x].setBounds(tileWidth*27/2,tileHeight*5,tileWidth,tileHeight*4);
                    break;
                case 4:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH5));
                    ships[x].setBounds(tileWidth*33/2,tileHeight*5,tileWidth,tileHeight*5);
                    break;
                default:
                    System.out.println ("Error: h"); break;
            }
            ships[x].addActionListener(new ActionListener(){  
                public void actionPerformed(ActionEvent e){  
                    Object src = e.getSource(); 
                    for (int x=0; x<5; x++) {
                        if (src.equals(ships[x])) {
                            System.out.println("User has selected Ship "+(x+1));
                            userSelection = x+1;
                            shipSelection = x;
                            if (userSelection > 2) {
                                userSelection = x;
                            }
                        } 
                    }       
                }  
            }); 
            f.add(ships[x]); 
        }
    }

    public static void gridActivity() { //grid inputs
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++) {
            int newX = x; //Cant use the x from the for loop inside a local method
            int newY = y;
                uGrid[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(MouseEvent evt) {
                        if (userSelection > 0) {
                            Color colour = Color.black;
                            if (shipRotation.equals("vertical")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newY+n > 9) {
                                        n = userSelection+1;
                                    } else {
                                        for (int z=0; z<userSelection+1; z++) {
                                            if (newY+userSelection > 9 || uGridData[newX][newY+userSelection-z] == "ship") { // Checking if it is placed over another ship
                                                colour=Color.red;
                                            }
                                        }
                                       
                                        uGrid[newX][newY+n].setBackground(colour);
                                    }   
                                }   
                            } 
                            if (shipRotation.equals("horizontal")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newX+n > 9) {
                                        n = userSelection+1;     
                                    } else {
                                        for (int z=0; z<userSelection+1; z++) { 
                                            if (newX+userSelection > 9 || uGridData[newX+userSelection-z][newY] == "ship") { // Checking if it is placed over another ship
                                                colour= Color.red;
                                            }
                                        }
                                        
                                        uGrid[newX+n][newY].setBackground(colour);
                                    }   
                                }   
                            } 
                        }     
                    }
                    public void mouseExited(MouseEvent evt) {
                        if (userSelection > 0) {
                            if (shipRotation.equals("vertical")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newY+n > 9) {
                                        n = userSelection+1;   
                                    } else {
                                        uGrid[newX][newY+n].setBackground(Color.white);
                                    }   
                                } 
                            }
                            if (shipRotation.equals("horizontal")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newX+n > 9) {
                                        n = userSelection+1;   
                                    } else {
                                        uGrid[newX+n][newY].setBackground(Color.white);
                                    }   
                                } 
                            }
                        }  
                    }
                }); 
 
                uGrid[x][y].addActionListener(new ActionListener(){ 
                    public void actionPerformed(ActionEvent e){
                        if (userSelection > 0) {
                            if (e.getSource() == uGrid[newX][newY] && uGrid[newX][newY].getBackground() == Color.black) {
                                Image icon = scaleshipImageH2;
                                if (shipRotation.equals("vertical")) {
                                    switch (userSelection) {
                                        case 1: icon = scaleshipImageH2; break;
                                        case 2: icon = scaleshipImageH3; break;
                                        case 3: icon = scaleshipImageH4; break;
                                        case 4: icon = scaleshipImageH5; break;
                                        default:
                                            System.out.println("Error: userSelection");
                                            break;
                                    } 
                                } else if (shipRotation.equals("horizontal")) { // Changing image if the ship needs to be horizontal
                                    switch (userSelection) {
                                        case 1: icon = scaleshipImageRH2; break;
                                        case 2: icon = scaleshipImageRH3; break;
                                        case 3: icon = scaleshipImageRH4; break;
                                        case 4: icon = scaleshipImageRH5; break;
                                        default:
                                            System.out.println("Error: userSelection");
                                            break;
                                    } 
                                }
                                
                                shipLabel = new JLabel();
                                shipLabel = new JLabel(new ImageIcon(icon));    
                             
                                if (shipRotation.equals("vertical")) {
                                    for (int n=0; n<userSelection+1; n++) {
                                        uGrid[newX][newY+n].setVisible(false);  
                                        uGridData[newX][newY+n] = "ship";
                                    }
                                    shipLabel.setBounds(uGrid[newX][newY].getX(), uGrid[newX][newY].getY(),tileWidth, tileHeight*(userSelection+1));
                                } else if (shipRotation.equals("horizontal")) {
                                    for (int n=0; n<userSelection+1; n++) {
                                        uGrid[newX+n][newY].setVisible(false);
                                        uGridData[newX+n][newY] = "ship";
                                    }
                                    shipLabel.setBounds(uGrid[newX][newY].getX(), uGrid[newX][newY].getY(),tileWidth*(userSelection+1), tileHeight);
                                }
                                
                                f.add(shipLabel);
                                ships[shipSelection].setVisible(false);
                                userSelection = 0;
                                resetButton();
                                startButton();
                            }   else if (uGrid[newX][newY].getBackground() != Color.black) { // If ship is placed outside of bounds
                                    System.out.println("Error: Ship placement out of bounds");
                                }
                        } 
                    }  
                }); 
            }
        }
    }
    
    public static void resetButton() {
        // Reset button is only visible after 1 or more ship(s) have been placed
        rButton.setBounds(GUIWIDTH-tileWidth, GUIHEIGHT-tileWidth, tileWidth*2, tileHeight);
        rButton.setVisible(false);
        f.add(rButton);
        rButton.setVisible(true);
        rButton.addActionListener(new ActionListener() { // Reset button actionListener
            public void actionPerformed(ActionEvent e){ 
                for (int x=0; x<ships.length; x++) {
                    ships[x].setVisible(true);
                }              
                for (int y=0; y<10; y++) {
                    for (int x=0; x<10; x++) {
                        uGrid[x][y].setVisible(true);
                        uGrid[x][y].setBackground(Color.white);
                        uGrid[x][y].setText("-");
                        uGridData[x][y] = null; // reseting data
                    }
                }
                rButton.setVisible(false); //Reset the button to be invisible
                f.remove(shipLabel);   
                startButton.setVisible(false);
            }
        });
    }

    //Implements of the KeyListener
    @Override
    public void keyTyped(KeyEvent e) { // Typed text
        // I dont need this method but I need to leave it for KeyListner anyways
    }
    @Override
    public void keyPressed(KeyEvent e) { // When key is pressed
        int keyCode = e.getKeyCode();
        if (keyCode == 82) { // Character 'r'
            System.out.println("r Key pressed");
            if (shipRotation.equals("vertical")) { // Switch between vertical and horizontal rotation
                shipRotation = "horizontal";
            } else {
                shipRotation = "vertical";
            } 
        }
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++) {
                uGrid[x][y].setBackground(Color.white); // Reset grid 
            }
        }
        System.out.println(shipRotation);
    }
    @Override
    public void keyReleased(KeyEvent e) { // Typed text
        // I dont need this either
    } 

    public static void startButton() {
        startButton.setBounds(GUIWIDTH*3/4-tileWidth*2, GUIHEIGHT/2-tileHeight*3, tileWidth*6,tileHeight*6);
        startButton.setFont(new Font("Arial", Font.PLAIN, 25)); // Using it to make font size larger
        startButton.setVisible(false); // When button is created it needs to be invisible
        f.add(startButton); 
        if (ships[0].isVisible() == false && ships[1].isVisible() == false && ships[2].isVisible() == false
        && ships[3].isVisible() == false && ships[4].isVisible() == false) { // Condition for when all ships are placed
            startButton.setVisible(true);
            startButton.addActionListener(new ActionListener() { // ActionListener of start button
                public void actionPerformed(ActionEvent e){ 
                    startGame(); // Calling startGame method 
                    startButton.setVisible(false);
                    rButton.setVisible(false);
                }
            });
        }    
    }

    public static void startGame() {
        // Calls when the game has started
        System.out.println("Game Started");
        String[] alphabetString = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        JLabel[] labels = new JLabel[20];
        // Adding buttons for computer grid
        for (int y=0, yPos=0, xPos=GUIWIDTH-tileWidth*9; y<10; y++) {
            for (int x=0; x<10; x++, xPos+=tileWidth)   {
                int xx = x; // I need these because the program dosen't allow methods to send variables from for loops
                int yy=y;
                cGrid[x][y]=new JButton("-");
                //Colour and properties of button
                cGrid[x][y].setBackground(Color.white);
                cGrid[x][y].setOpaque(true);
                cGrid[x][y].setBorderPainted(false);
                cGrid[x][y].setBounds(xPos,yPos,tileWidth,tileHeight);
                cGrid[x][y].addActionListener(new ActionListener() { // Action listner for button presses
                    public void actionPerformed(ActionEvent e){
                        System.out.println(userShot(xx, yy));
                    }
                });
                f.add(cGrid[x][y]);   
            }
            yPos+=tileHeight; 
            xPos=GUIWIDTH-tileWidth*9;
        }
        
        // Adding labels for compiter grid
        for (int x=0, yPos=0; x<11; x++, yPos+=tileHeight) {
            if (x<10) {
                labels[x] = new JLabel(alphabetString[x]); 
                labels[x].setBounds(GUIWIDTH+tileWidth,yPos,tileWidth,tileHeight);
                f.add(labels[x]);
            } else if (x==10) {
                for (int y=0, xPos=GUIWIDTH-tileWidth*43/5; y<10; y++, xPos+=tileWidth) { // 43/5 is my 'golden ratio'
                    labels[y+10] = new JLabel(String.valueOf(y+1)); 
                    labels[y+10].setBounds(xPos,tileHeight*9+GUITAB+tileHeight/5,tileWidth,tileHeight);
                    f.add(labels[y+10]);
                }
            }
        }
        refreshScreen(); // Refresh screen method
    }

    public static void refreshScreen() { // Refreshes screen when called
        // Used to fix bug where buttons called after f.setVisible are invisible
        f.setVisible(false);
        f.setVisible(true);
    }
    public static void computerShip() { // Creates the position of the computer ship
        String[] rotation = {"vertical", "horizontal"};
        int[] length = {2, 3, 3, 4, 5};
        int xPos=0;
        int yPos=0;
        for (int n=0; n<length.length; n++) {
            String r = rotation[(int)Math.floor(Math.random()*2)]; // 0-1
            if (r.equals("vertical")) { // If rotation is vertical
                xPos = (int)Math.floor(Math.random()*10); // 0-9
                yPos = (int)Math.floor(Math.random()*(10-length[n])); // 0-(10-length of ship)
                for (int z=0; z<length[n]; z++) {
                    if (cGridData[xPos][yPos+z] != "ship") { // Ship is currently not placed
                        cGridData[xPos][yPos+z] = "ship";
                    } else { // Ship is currently placed in that location
                        System.out.println("Error overlap vertical: "+xPos+ ", "+(yPos+z)+", "+n+yPos);
                        for (int zz=0; zz<z; zz++) { // Remove currently placed tiles for this ship
                            cGridData[xPos][yPos+zz] = null;
                        }
                        cGridData[xPos][yPos+z] = "ship"; // Original position
                        z=length[n]; //Exit z loop
                        n-=1; //Re-place this ship
                    }
                }
               
            } else { // If rotation is horizontal
                xPos = (int)Math.floor(Math.random()*(10-length[n])); // 0-(10-length of ship)
                yPos = (int)Math.floor(Math.random()*10); // 0-9
                for (int z=0; z<length[n]; z++) {
                    if (cGridData[xPos+z][yPos] != "ship") { // Ship is currently not placed
                        cGridData[xPos+z][yPos] = "ship";
                    } else { // Ship is currently placed in that location
                        System.out.println("Error overlap horizontal: "+(xPos+z)+ ", "+yPos+", "+n+xPos);
                        for (int zz=0; zz<z; zz++) { // Remove currently placed tiles for this ship
                            cGridData[xPos+zz][yPos] = null;
                        }
                        cGridData[xPos+z][yPos] = "ship"; // Original position
                        z=length[n]; //Exit z loop
                        n-=1; //Re-place this ship
                    }
                }
            }      
        }
        for (int y=0; y<10; y++) { // For Testing
            for (int x=0; x<10; x++) {
                if (cGridData[x][y] == "ship") {
                    System.out.print("X ");
                } else if (cGridData[x][y] == "overlap") {
                    System.out.print("O ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println(" ");
        }
    }

    public static String userShot(int xPos, int yPos) {
        //System.out.println("Computer grid pressed at: "+xPos+" "+yPos);
        if (cGridData[xPos][yPos] != "ship" && cGridData[xPos][yPos] != null) {
            return("repeat");
        } else if (cGridData[xPos][yPos] != "ship") {
            return ("miss");
        } else {
            return("hit");
        }
    }
    public static void main(String[] args) {  // Called when the program is run
        System.out.println("Window Width: "+GUIWIDTH);
        System.out.println("Window Height: "+GUIHEIGHT);
        grid(); // Calls the grid method at the start of the program
        gridActivity(); // Grid activity is for user cursor hovering and clicks
        new Main(); // Constructor for Jframe(GUI) and ships
        computerShip();
    }
}  