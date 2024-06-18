/* 
* PROJECT TITLE: Battleships
* VERSION or DATE: Version 4.3, 19.06.24
* AUTHOR: Viraaj Ravji
* DETAILS:
    * Added to computer shooting algorithim
    * Still lots of errors when ships are placed on edges
*/

/*LIBRARY*/
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*; 
import java.awt.event.*;

public class Main implements KeyListener {    
     /*CLASS VARIABLES*/
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //Takes the height of the screen and calculates size of the GUI
    public static final int GUIHEIGHT = (int)screenSize.getHeight() / 2; 
    public static final int GUIWIDTH = GUIHEIGHT*5/2; //width of the GUI
    
    private static JFrame f=new JFrame("Battleships"); // Creates JFrame, the GUI window
    private static JPanel GUI = new JPanel(new GridLayout(1,2,20, 0)); // 1 row,2 columns,20px horizontal gap,0px vertical gap
    private static JPanel textPanel = new JPanel();

    private static int GUITAB = 30; //This will scale with the size of the GUI, default is 30
    private static JButton[][] uGrid = new JButton[10][10]; // User Grid
    private static JButton[][] cGrid = new JButton[10][10]; // Computer Grid
    private static int[][] uGridData = new int[10][10]; // 0-nothing, 1-5-ship, 6-hit ship, 7-miss
    private static int[][] cGridData = new int[10][10]; // 0-nothing, 1-5-ship, 6-hit ship, 7-miss
    private static int[] uShipHitPoints = {2, 3, 3, 4, 5}; 
    private static int[] cShipHitPoints = {2, 3, 3, 4, 5}; 
    private static JButton[] ships = new JButton[5];
    private static int tileHeight = GUIHEIGHT/10; // Tile spacing vertical
    private static int tileWidth = GUIWIDTH/20; // Tile spacing horizontal
    private static int userSelection;
    private static int shipSelection;
    private static final JButton rButton = new JButton("Reset");; //reset JButton
    private static String shipRotation = "vertical";
    private static final JButton startButton = new JButton("Press to Start"); // start JButton
    private static int turn = 0;
    private static  Boolean clickable = true; // Is the gui clickable
    
    private static int lastX = -1;
    private static int lastY = -1;
    private static String hitDirection = null;
    private static int originalX;
    private static int originalY;

    /*IMAGES*/
    private static ImageIcon shipImageH2 = new ImageIcon("assets/ship_texture_h2.jpg");
    private static Image scaleshipImageH2 = shipImageH2.getImage().getScaledInstance(tileWidth, tileHeight*2,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH3 = new ImageIcon("assets/ship_texture_h3.jpg");
    private static Image scaleshipImageH3 = shipImageH3.getImage().getScaledInstance(tileWidth, tileHeight*3,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH4 = new ImageIcon("assets/ship_texture_h4.jpg");
    private static Image scaleshipImageH4 = shipImageH4.getImage().getScaledInstance(tileWidth, tileHeight*4,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH5 = new ImageIcon("assets/ship_texture_h5.jpg");
    private static Image scaleshipImageH5 = shipImageH5.getImage().getScaledInstance(tileWidth, tileHeight*5,Image.SCALE_DEFAULT);

    private static ImageIcon shipImage = new ImageIcon("assets/ship_texture.jpg");
    private static ImageIcon shipImageHit = new ImageIcon("assets/ship_texture_hit.jpg");

    public Main() {
        //GUI window properties for JFrame
        f.setSize(GUIWIDTH+GUITAB*3, GUIHEIGHT+GUITAB*2);  
        f.setFocusTraversalKeysEnabled(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.setFocusable(true);
        f.addKeyListener(this);
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++) {
                if (x<5 && y==0) { //For all ship buttons add key listener
                    ships[x].addKeyListener(this);
                }
                uGrid[x][y].addKeyListener(this);
            }
        }   
        f.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { // Run when the GUI is resized
                for (int y=0; y<10; y++) {
                    for (int x=0; x<10;x++) {
                        if (uGridData[x][y] != 0 && uGridData[x][y] < 6) {
                            // Update icon of the button
                            uGrid[x][y].setIcon(new ImageIcon(shipImage.getImage().getScaledInstance(uGrid[x][y].getWidth(), uGrid[x][y].getHeight(),Image.SCALE_DEFAULT)));
                        } else if (uGridData[x][y] == 6) {
                            uGrid[x][y].setIcon(new ImageIcon(shipImageHit.getImage().getScaledInstance(uGrid[x][y].getWidth(), uGrid[x][y].getHeight(),Image.SCALE_DEFAULT)));
                        }
                    }
                }   
                GUITAB = f.getWidth()/75;  
                GUI.setLayout(new GridLayout(1,2,GUITAB, 0));
            }
        });
        refreshScreen();    
    }

    public static void refreshScreen() { // Refreshes screen when called
        // Used to fix bug where buttons called after f.setVisible are invisible
        f.setVisible(false);
        f.setVisible(true);
    }

    public static void grid() { //creates the GUI and grid
        /*grid vairables*/
        String[] alphabetString = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        JLabel[] labels = new JLabel[20];
        JPanel userPanel = new JPanel(new BorderLayout());
        JPanel userGrid = new JPanel(new GridLayout(10,10));
        JPanel labelsLeft = new JPanel(new GridLayout(10, 1));
        JPanel labelsBottom = new JPanel(new GridLayout(1, 10));
        labelsBottom.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        
        JPanel computerPanel = new JPanel(new BorderLayout());
        JPanel shipPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel();
       
        //adding buttons to GUI
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++)   {
                uGrid[x][y]=new JButton("-");
                //Colour and properties of button
                uGrid[x][y].setMargin(new Insets(0,0,0,0));
                uGrid[x][y].setBackground(Color.white);
                uGrid[x][y].setOpaque(true);
                uGrid[x][y].setBorderPainted(false);
                userGrid.add(uGrid[x][y]);   
            }
        }
        // Add sub-panels to user and computer panels
        userPanel.add(labelsLeft, BorderLayout.WEST);
        userPanel.add(userGrid, BorderLayout.CENTER);
        userPanel.add(labelsBottom, BorderLayout.SOUTH);
        computerPanel.add(shipPanel, BorderLayout.CENTER);
        computerPanel.add(buttonPanel, BorderLayout.SOUTH);
             
        //adding labels on GUI
        for (int x=0; x<11; x++) {
            if (x<10) {
                labels[x] = new JLabel(" "+alphabetString[x]+" "); // Spaces are just to help alignment
                labelsLeft.add(labels[x]);
            } else if (x==10) {
                for (int y=0; y<10; y++) {
                    labels[y+10] = new JLabel(String.valueOf(y+1), JLabel.CENTER); 
                    labelsBottom.add(labels[y+10]);
                }
            }
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 0;
        gbc.weighty = 0;
        // Adding ships to right of grid
        for (int x=0; x<5; x++) { 
            switch (x) {
                case 0:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH2));
                    break;     
                case 1:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH3));
                    break;
                case 2:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH3));
                    break;
                case 3:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH4));
                    break;
                case 4:
                    ships[x] = new JButton(new ImageIcon(scaleshipImageH5));
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
            ships[x].setBorder(new LineBorder(Color.BLACK));
            gbc.gridx = x; // Column 0
            gbc.gridy = 0; // Row 0
            shipPanel.add(ships[x], gbc);
        } 
        GUI.add(userPanel);
        GUI.add(computerPanel);
        f.setLayout(new BorderLayout());
        f.add(GUI, BorderLayout.CENTER);
        gridActivity(computerPanel, shipPanel, buttonPanel); // Grid activity is for user cursor hovering and clicks
    }

    public static void gridActivity(JPanel computerPanel, JPanel shiPanel, JPanel buttonPanel) { //grid inputs
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
                                            if (newY+userSelection > 9 || uGridData[newX][newY+userSelection-z] < 6 
                                            && uGridData[newX][newY+userSelection-z] != 0) { // Checking if it is placed over another ship
                                                colour=Color.red;
                                            }
                                        }
                                        if (uGrid[newX][newY+n].getBackground() != Color.DARK_GRAY) {
                                            uGrid[newX][newY+n].setBackground(colour);
                                        }
                                    }   
                                }   
                            } 
                            if (shipRotation.equals("horizontal")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newX+n > 9) {
                                        n = userSelection+1;     
                                    } else {
                                        for (int z=0; z<userSelection+1; z++) { 
                                            if (newX+userSelection > 9 || uGridData[newX+userSelection-z][newY] < 6 
                                                && uGridData[newX+userSelection-z][newY] != 0) { // Checking if it is placed over another ship
                                                colour= Color.red;
                                            }
                                        }
                                        if (uGrid[newX+n][newY].getBackground() != Color.DARK_GRAY) {
                                            uGrid[newX+n][newY].setBackground(colour);
                                        }
                                        
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
                                        if (uGrid[newX][newY+n].getBackground() != Color.DARK_GRAY) {
                                            uGrid[newX][newY+n].setBackground(Color.white);
                                        }
                                       
                                    }   
                                } 
                            }
                            if (shipRotation.equals("horizontal")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newX+n > 9) {
                                        n = userSelection+1;   
                                    } else {
                                        if (uGrid[newX+n][newY].getBackground() != Color.DARK_GRAY) {
                                            uGrid[newX+n][newY].setBackground(Color.white);
                                        }
                                    }   
                                } 
                            }
                        }  
                    }
                }); 
 
                uGrid[x][y].addActionListener(new ActionListener(){ 
                    public void actionPerformed(ActionEvent e){
                        if (clickable == true) {
                            if (userSelection > 0 && uGrid[newX][newY].getBackground() == Color.black) { // If ship selected and in valid placement                    
                                if (shipRotation.equals("vertical")) {
                                    for (int n=0; n<userSelection+1; n++) {
                                        uGrid[newX][newY+n].setText("");
                                        uGrid[newX][newY+n].setBackground(Color.DARK_GRAY);
                                        uGrid[newX][newY+n].setIcon(new ImageIcon(shipImage.getImage().getScaledInstance(uGrid[newX][newY+n].getWidth(), uGrid[newX][newY+n].getHeight(),Image.SCALE_DEFAULT))); 
                                        uGridData[newX][newY+n] = shipSelection+1;
                                    }
                                } else if (shipRotation.equals("horizontal")) {
                                    for (int n=0; n<userSelection+1; n++) {
                                        uGrid[newX+n][newY].setText("");
                                        uGrid[newX+n][newY].setBackground(Color.DARK_GRAY);
                                        uGrid[newX+n][newY].setIcon(new ImageIcon(shipImage.getImage().getScaledInstance(uGrid[newX+n][newY].getWidth(), uGrid[newX+n][newY].getHeight(),Image.SCALE_DEFAULT)));
                                        uGridData[newX+n][newY] = shipSelection+1;
                                    }
                                }
                                ships[shipSelection].setVisible(false);
                                userSelection = 0;
                                resetButton(buttonPanel);
                                startButton(computerPanel, shiPanel, buttonPanel);
                                shipRotation = "vertical";
                            } else if (uGrid[newX][newY].getBackground() != Color.black) { // If ship is placed outside of bounds
                                System.out.println("Error: Ship placement out of bounds");
                            } 
                        }                        
                    }  
                }); 
            }
        }
    }
    
    public static void resetButton(JPanel buttonPanel) {
        // Reset button is only visible after 1 or more ship(s) have been placed
        rButton.setVisible(false);
        buttonPanel.add(rButton);
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
                        uGrid[x][y].setIcon(null);
                        uGridData[x][y] = 0; // reseting data
                    }
                }
                rButton.setVisible(false); //Reset the button to be invisible
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
                if (uGrid[x][y].getBackground() != Color.DARK_GRAY) {
                    uGrid[x][y].setBackground(Color.white); // Reset grid 
                }   
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) { // Typed text
        // I dont need this either
    } 

    public static void startButton(JPanel computerPanel, JPanel shipPanel, JPanel buttonPanel) {
        startButton.setVisible(false); // When button is created it needs to be invisible
        buttonPanel.add(startButton); 
        if (ships[0].isVisible() == false && ships[1].isVisible() == false && ships[2].isVisible() == false
        && ships[3].isVisible() == false && ships[4].isVisible() == false) { // Condition for when all ships are placed
            startButton.setVisible(true);
            startButton.addActionListener(new ActionListener() { // ActionListener of start button
                public void actionPerformed(ActionEvent e){ 
                    startGame(computerPanel); // Calling startGame method 
                    computerPanel.remove(buttonPanel);
                    computerPanel.remove(shipPanel);
                    startButton.setVisible(false);
                    rButton.setVisible(false);
                }
            });
        }    
    }

    public static void startGame(JPanel computerPanel) {
        // Calls when the game has started
        System.out.println("Game Started");
        String[] alphabetString = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        JLabel[] labels = new JLabel[20];
        JLabel screenText = new JLabel("",JLabel.CENTER); // Centres the text inside the JLabel

        JPanel computerGrid = new JPanel(new GridLayout(10, 10));  
        JPanel labelsLeft = new JPanel(new GridLayout(10, 1));
        JPanel labelsBottom = new JPanel(new GridLayout(1, 10));
        labelsBottom.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        computerPanel.add(computerGrid, BorderLayout.CENTER); 
        computerPanel.add(labelsLeft, BorderLayout.EAST);     
        computerPanel.add(labelsBottom, BorderLayout.SOUTH); 
        // Adding buttons for computer grid
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++)   {
                int xx = x; // I need these because the program dosen't allow methods to send variables from for loops
                int yy=y;
                cGridData[x][y] = 0; // Sets the entire computer grid to notihing, look at top for number reference
                cGrid[x][y]=new JButton("-");
                //Colour and properties of button
                cGrid[x][y].setBackground(Color.white);
                cGrid[x][y].setOpaque(true);
                cGrid[x][y].setBorderPainted(false);
                
                cGrid[x][y].addActionListener(new ActionListener() { // Action listner for button presses
                    public void actionPerformed(ActionEvent e){
                        if (clickable == true) {
                            if (turn == 0) { // Add screen Text if it is the first turn
                                f.setSize(f.getWidth(), f.getHeight()+GUITAB);  // Increase size of GUI to allow for text below
                                f.add(textPanel, BorderLayout.SOUTH);
                                textPanel.add(screenText);
                                refreshScreen();
                            }
                            String text = userShot(xx, yy); // Calls for userShot method, which will return hit result
                            screenTimer(text, screenText, false);               
                        }   
                    }
                });
                computerGrid.add(cGrid[x][y]);   
            }
        }
        
        // Adding labels for compiter grid
        for (int x=0; x<11; x++) {
            if (x<10) {
                labels[x] = new JLabel(" "+alphabetString[x]+ " "); 
                labelsLeft.add(labels[x]);
            } else if (x==10) {
                for (int y=0; y<10; y++) { 
                    labels[y+10] = new JLabel(String.valueOf(y+1), JLabel.CENTER); 
                    labelsBottom.add(labels[y+10]);
                }
            }
        }
        refreshScreen(); // Refresh screen method
        computerShip(); // Places computer ships
    }

    public static void computerShip() { // Creates the position of the computer ship
        String[] rotation = {"vertical", "horizontal"};
        int[] length = {2, 3, 3, 4, 5};
        int xPos=0;
        int yPos=0;
        for (int n=0; n<length.length; n++) {
            String r = rotation[(int)Math.floor(Math.random()*2)]; // Either 0 or 1 cGri
            if (r.equals("vertical")) { // If rotation is vertical
                xPos = (int)Math.floor(Math.random()*10); // 0 to 9
                yPos = (int)Math.floor(Math.random()*(10-length[n])); // 0 to (10-length of ship)
                for (int z=0; z<length[n]; z++) {
                    if (cGridData[xPos][yPos+z] > 6 || cGridData[xPos][yPos+z] == 0) { // Ship is currently not placed
                        cGridData[xPos][yPos+z] = n+1;
                    } else { // Ship is currently placed in that location
                        System.out.println("Error overlap vertical: "+xPos+ ", "+(yPos+z)+", "+n+yPos);
                        for (int zz=0; zz<z; zz++) { // Remove currently placed tiles for this ship
                            cGridData[xPos][yPos+zz] = 0; // 0 is nothing
                        }
                        z=length[n]; //Exit z for loop
                        n-=1; //Re-place this ship
                    }
                }     
            } else { // If rotation is horizontal
                xPos = (int)Math.floor(Math.random()*(10-length[n])); // 0 to (10-length of ship)
                yPos = (int)Math.floor(Math.random()*10); // 0 to 9
                for (int z=0; z<length[n]; z++) {
                    if (cGridData[xPos+z][yPos] > 6 || cGridData[xPos+z][yPos] == 0) { // Ship is currently not placed
                        cGridData[xPos+z][yPos] = n+1;
                    } else { // Ship is currently placed in that location
                        System.out.println("Error overlap horizontal: "+(xPos+z)+ ", "+yPos+", "+n+xPos);
                        for (int zz=0; zz<z; zz++) { // Remove currently placed tiles for this ship
                            cGridData[xPos+zz][yPos] = 0; // 0 is nothing
                        }
                        z=length[n]; //Exit z loop
                        n-=1; //Re-place this ship
                    }
                }
            }      
        }
        for (int y=0; y<10; y++) { // FOR TESTING
            for (int x=0; x<10; x++) {
                if (cGridData[x][y] != 0) {
                    System.out.print(cGridData[x][y]+" ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println(" ");
        }
    }

    public static String userShot(int xPos, int yPos) {
        String text;
        if (cGridData[xPos][yPos] >= 6) { //Already shot this position, either hit or miss
            text = "Already shot here";
        } else if (cGridData[xPos][yPos] < 6 && cGridData[xPos][yPos] !=0) { // A ship is hit
            cShipHitPoints[cGridData[xPos][yPos]-1]--; // Lower hitpoint of the hit computer ship by 1
            cGridData[xPos][yPos] = 6;
            cGrid[xPos][yPos].setBackground(Color.RED);
            text = "You hit a Battleship!";
            turn++;
        } else { // Nothing is hit
            cGridData[xPos][yPos] = 7;
            cGrid[xPos][yPos].setBackground(Color.BLACK);
            text = "Miss! Nothing was hit";
            turn++;
        }
        return (text);
    }

    public static String computerShot() {
        String text;
        int xPos = (int)Math.floor(Math.random()*10); // 0 to 9
        int yPos = (int)Math.floor(Math.random()*10); // 0 to 9

        if (lastX != -1 && lastY != -1) { // If last shot was a hit
            int n;
            if (hitDirection == "vertical") {
                int[] list = {1, 2};
                n = list[(int)Math.floor(Math.random()*2)]; // 1 or 2
            } else if (hitDirection == "horizontal") {
                int[] list = {0, 3};
                n = list[(int)Math.floor(Math.random()*2)]; // 0 or 3
            } else { // Hit direction is null (unknown)
                if (lastX == 0) {
                    n = (int)Math.floor(Math.random()*3); // 0 - 2 
                } else if (lastX == 9) {
                    n = (int)Math.floor(Math.random()*3)+1; // 1 - 3 
                } else if (lastY == 0) {
                    int[] list = {0, 1, 3};
                    n = list[(int)Math.floor(Math.random()*3)]; // 0, 1, or 3 
                } else if (lastY == 9) {
                    int[] list = {0, 2, 3};
                    n = list[(int)Math.floor(Math.random()*3)]; // 0, 2, or 3 
                } else if (lastX == 0 && lastY == 0) {
                    n = (int)Math.floor(Math.random()*2); // 0 - 1 
                } else if (lastX == 0 && lastY == 9) {
                    int[] list = {0, 2};
                    n = list[(int)Math.floor(Math.random()*2)]; // 0 or 2
                } else if (lastX == 9 && lastY == 0) {
                    int[] list = {1, 3};
                    n = list[(int)Math.floor(Math.random()*2)]; // 1 or 3
                } else if (lastX == 9 && lastY == 9) {
                    n = (int)Math.floor(Math.random()*2)+2; // 2 or 3
                } else { // Between (1-8, 1-8)
                    n = (int)Math.floor(Math.random()*4); // 0 - 3
                }
            }
       
            switch (n) {
                case 0:
                    xPos = lastX + 1;
                    yPos = lastY; 
                    break;
                case 1:
                    xPos = lastX;
                    yPos = lastY + 1;  
                    break;
                case 2:
                    xPos = lastX;
                    yPos = lastY - 1;
                    break; 
                case 3:
                    xPos = lastX - 1;
                    yPos = lastY;
                    break;
                default:
                    System.out.println("Error: "+n);
            }
            System.out.println(xPos);
            System.out.println(yPos);
        } 
        
        if (uGridData[xPos][yPos] >= 6) { //Already shot this position, either hit or miss
            text = "Error: Already shot here";
        } else if (uGridData[xPos][yPos] < 6 && uGridData[xPos][yPos] != 0) { // Hit
            uGrid[xPos][yPos].setIcon(new ImageIcon(shipImageHit.getImage().getScaledInstance(uGrid[xPos][yPos].getWidth(), uGrid[xPos][yPos].getHeight(),Image.SCALE_DEFAULT)));
            text = "Opponent has hit a Battleship!";
            uShipHitPoints[(uGridData[xPos][yPos]-1)]--; // Lower hitpoint of the hit user ship by 1
            if (lastX > -1 || lastY > -1) {
                if (hitDirection == null) {
                    if (lastX < xPos || lastX > xPos) {
                        hitDirection = "horizontal";                 
                    } else if (lastY < yPos || lastY > yPos) {
                        hitDirection = "vertical"; 
                    }
                    System.out.println(hitDirection);
                }   
            }
            lastX = xPos;
            lastY = yPos;
            if (uShipHitPoints[uGridData[xPos][yPos]-1] == 0) { // Reset last variables if the ship is destroyed
                lastX = -1;
                lastY = -1;
                hitDirection = null;
            }
            if (hitDirection == null) {
                originalX = xPos;
                originalY = yPos;
            }
            uGridData[xPos][yPos] = 6;
            turn++;
        } else { // Nothing is hit
            uGrid[xPos][yPos].setBackground(Color.black);
            uGridData[xPos][yPos] = 7;
            text = "Opponent missed! Nothing was hit";
            if (lastX > -1 || lastY > -1) {
                if (hitDirection == null) {
                    if (xPos-2 >= 0 && xPos+2 <= 10 && yPos-2 >= 0 && yPos+2 <= 10) {
                        if (lastX < xPos && uGridData[xPos-2][yPos] == 7|| lastX > xPos && uGridData[xPos+2][yPos] == 7) {
                            hitDirection = "vertical";
                        } else if (lastY < yPos && uGridData[xPos][yPos-2] == 7 || lastY > yPos && uGridData[xPos][yPos+2] == 7) {
                            hitDirection = "horizontal";
                        }
                        System.out.println(hitDirection);
                    } else {
                        System.out.println("ERROR");
                    }
                } else {
                    lastX = originalX;
                    lastY = originalY;   
                }      
            }
            turn++;
        }
        return (text);
    }

    public static void winChecker(JLabel screenText) {
        if (uShipHitPoints[0] == 0 && uShipHitPoints[1] == 0 && uShipHitPoints[2] == 0 &&
        uShipHitPoints[3] == 0 && uShipHitPoints[4] == 0) {
            System.out.println("Game over, computer wins");
            screenTimer("Game over, computer wins", screenText, true);
        } else if (cShipHitPoints[0] == 0 && cShipHitPoints[1] == 0 && cShipHitPoints[2] == 0 &&
        cShipHitPoints[3] == 0 && cShipHitPoints[4] == 0) {
            System.out.println("Game over, user wins");
            screenTimer("Game over, user wins", screenText, true);

        }   
    }

    public static void screenTimer(String text, JLabel screenText, boolean gameOver) {
        Timer timer = new Timer(30, new ActionListener() { //Timer runs every 30ms
            int index = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < text.length()) {
                    clickable = false;
                    // Reveal one more character
                    screenText.setText(text.substring(0, index + 1)); // Starts from 0 and adds each character up to the int of index
                    index++;
                } else {
                    // Stop the timer once all characters are revealed
                    ((Timer)e.getSource()).stop();
                    if (turn%2 == 1) { //If it is end of user's turn
                        String ntext = null;
                        while (turn%2 == 1 && gameOver==false) { //
                            ntext = computerShot(); // Calls for userShot method, which will return hit result
                        }
                        try {
                            Thread.sleep(500); // Pause for 0.5s
                            screenTimer(ntext, screenText, false);    
                        } catch (InterruptedException ie) {                          
                            Thread.currentThread().interrupt(); 
                        } 
                    } else if (gameOver == true) {
                        clickable = false;
                    } else {
                        clickable = true;
                        winChecker(screenText);
                    }  
                }
            }
        });
        timer.start();
    }

    public static void main(String[] args) {  // Called when the program is run
        grid(); // Calls the grid method at the start of the program
        new Main(); // Constructor for Jframe(GUI) and ships
    }
}  