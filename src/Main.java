/** 
* PROJECT TITLE: Battleships
* VERSION or DATE: Version 6, 24.07.24
* AUTHOR: Viraaj Ravji
* DETAILS:
    * Finished Game
*/

/*LIBRARY*/
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*; 
import java.awt.event.*;

public class Main implements KeyListener {    
     /*CLASS VARIABLES*/
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // Takes the height of the screen and calculates size of the GUI
    private static final int GUIHEIGHT = (int)screenSize.getHeight() / 2; 
    private static final int GUIWIDTH = GUIHEIGHT*5/2; // Width of the GUI proportional to the width of the scrreen
    
    private static JFrame f=new JFrame("Battleships"); // Creates JFrame, the GUI window
    private static JPanel guiPanel = new JPanel(new GridLayout(1,2,20, 0)); // 1 row,2 columns,20px horizontal gap,0px vertical gap
    private static JPanel textPanel = new JPanel();

    private static int GUITAB = 30; //This will scale with the size of the GUI, default is 30
    private static JButton[][] uGrid = new JButton[10][10]; // User Grid
    private static JButton[][] cGrid = new JButton[10][10]; // Computer Grid
    private static int[][] uGridData = new int[10][10]; // 0-nothing, 1-5-ship, 6-hit ship, 7-miss
    private static int[][] cGridData = new int[10][10]; 
    private static int[] uShipHitPoints = {2, 3, 3, 4, 5}; // How many hits left on each user's battleship
    private static int[] cShipHitPoints = {2, 3, 3, 4, 5}; 
    private static JButton[] ships = new JButton[5];
    private static int tileHeight = GUIHEIGHT/10; // Tile spacing vertical
    private static int tileWidth = GUIWIDTH/20; // Tile spacing horizontal
    private static int userSelection;
    private static int shipSelection;
    private static JButton currentHoveredButton = null; 
    private static final JButton rButton = new JButton("Reset");; //reset JButton
    private static String shipRotation = "vertical";
    private static final JButton startButton = new JButton("Press to Start"); // start JButton
    private static int turn = 0;
    private static Boolean clickable = true; // Is the gui clickable
    private static Boolean gameOver = false; // Is the game over
    private static Boolean boldText = false;
    
    // Computer algorithm
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

    /**
     * This is my JFrame constructor
     * It is used to setup all the properies for my GUI
     * I also use this to add keyListeners to grid buttons and ship buttons. When these are selsected you can press keys like 'r' because it is a non static method
     * It also runs a method when the GUI is resized. It updates the size of the images and GUITAB, which is the space between the JPanels inside the GUI layout
     */
    public Main() {
        // GUI window properties for JFrame
        f.setSize(GUIWIDTH+GUITAB*3, GUIHEIGHT+GUITAB*2);  
        f.setFocusTraversalKeysEnabled(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
        f.setFocusable(true);
        f.addKeyListener(this);
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++) {
                if (x<5 && y==0) { // For all ship buttons add key listener
                    ships[x].addKeyListener(this);
                }
                uGrid[x][y].addKeyListener(this);
            }
        }

        f.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { // Run when the GUI is manually resized
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
                guiPanel.setLayout(new GridLayout(1,2,GUITAB, 0));
            }
        });
        refreshScreen();    
    }

    /**
     * Callled when new objects are added to the GUI like a JLabel
     * Used to fix bug where buttons called after f.setVisible are invisible
     */
    public static void refreshScreen() { 
        f.setVisible(false);
        f.setVisible(true);
    }

    /**
     * Creates the starting GUI for when the game is run
     * Creates all the JPanels visible before the game is started
     * JPanels allow the GUI to scale nicely i.e JButtons are smaller when the gui is downscaled
     * User's grid are 100 buttons alligned in a 10x10 format
     * JLabels are added to the left and bottom of the grid to show the coordinates of the grid
     * JPanel on the right are the ships placed with a gridBagLayout
     * Finally the panel on the bottom is for the text
     */
    public static void startGUI() { 
        // Initial GUI variables
        String[] alphabetString = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        JLabel[] labels = new JLabel[20];
        JPanel userPanel = new JPanel(new BorderLayout());
        JPanel userGrid = new JPanel(new GridLayout(10,10));
        JPanel labelsLeft = new JPanel(new GridLayout(10, 1));
        JPanel labelsBottom = new JPanel(new GridLayout(1, 10));
        labelsBottom.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        // Creating panels for user grid, labels, computer panel, ships, and buttons
        JPanel computerPanel = new JPanel(new BorderLayout());
        JPanel shipPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel();
             
        // Adding buttons to GUI
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
        // Adding labels on GUI
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
        // Set constraints for ship buttons
        GridBagConstraints shipsGBC = new GridBagConstraints();
        shipsGBC.fill = GridBagConstraints.NONE; // 
        shipsGBC.insets = new Insets(5, 5, 5, 5); // Padding around JButtons so they aren't touching each other
        // Adding ships to right of grid
        for (int x=0; x<5; x++) { 
            switch (x) { // Each value of x needs a different imageIcon
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
                    // Handles unexpected values of x
                    System.out.println ("Error: h"); 
                    break;
            }

            /*
             * ActionListner for when the ships are clicked
             * Identifies which button is pressed
             * Assigns userSelection the length of the ship
             * Assigns shipSelection to which ship is clicked
             */
            ships[x].addActionListener(new ActionListener(){  
                public void actionPerformed(ActionEvent e){ 
                    Object src = e.getSource(); 
                    for (int x=0; x<5; x++) {
                        if (src.equals(ships[x])) {
                            ships[shipSelection].setBorder(new LineBorder(Color.BLACK, 1)); // Makes previosly selected ship default outline
                            ships[x].setBorder(new LineBorder(Color.BLUE, 2)); // Makes new selection outlined in blue
                            userSelection = x+1;
                            shipSelection = x;
                            if (userSelection > 2) {
                                userSelection = x;
                            }
                        } 
                    }       
                }  
            });  
            ships[x].setBorder(new LineBorder(Color.BLACK)); // Adds a border around the button for aesthetic
            shipsGBC.gridx = x; // Column 0-4
            shipsGBC.gridy = 0; // Row 0
            shipPanel.add(ships[x], shipsGBC);
        } 
        // Add panels the GUI, some are sub-panels
        userPanel.add(labelsLeft, BorderLayout.WEST);
        userPanel.add(userGrid, BorderLayout.CENTER);
        userPanel.add(labelsBottom, BorderLayout.SOUTH);
        computerPanel.add(shipPanel, BorderLayout.CENTER);
        computerPanel.add(buttonPanel, BorderLayout.SOUTH);
        guiPanel.add(userPanel);
        guiPanel.add(computerPanel);
        f.setLayout(new BorderLayout());
        f.add(guiPanel, BorderLayout.CENTER);

        // Adding text label at the bottom of the GUI
        JLabel screenText = new JLabel("Place all 5 ships to start",JLabel.CENTER); // Centres the text inside the JLabel
        screenText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        textPanel.add(screenText);
        f.add(textPanel, BorderLayout.SOUTH);
        // Instruction button
        JButton instructionButton = new JButton("Instructions");
        buttonPanel.add(instructionButton); 
        instrcutions(instructionButton, computerPanel, shipPanel); // Calls the method to tell the button what to do
        gridActivity(computerPanel, shipPanel, buttonPanel, screenText); // Grid activity is for user cursor hovering and clicks
    }

    /**
     * Adds a mouseListener to the instructions button for when hovered
     * Uses html text layout to add break between lines
     * When the mouse hovers the button the ships need to hide, vice versa
     */
    public static void instrcutions(JButton instructionButton, JPanel computerPanel, JPanel shipPanel) {
        instructionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            JLabel instructionLabel = new JLabel("<html>Battleships!<br>" // Text on JLabel
            +"- In this game you play against the computer<br>" // br is a line break
            +"- The objective is to sink all the computer's ships before it sinks all of yours<br>"
            +"- Start by placing all your ships. Click a ship on the right to select it, and click again anywhere on the grid to place it. Press R on your keyboard to rotate the ship<br>"
            +"- Once all ships are placed you can start the game<br>"
            +"- Start by clicking somewhere on the computer's(left) grid to fire a shot<br>"
            +"- The tile will change colour if it is a hit or miss, aswell as saying below the grid<br>"
            +"- The computer will return a shot<br>"
            +"- The game will end when either player destroys all 5 of the opponents battleships</html>", JLabel.CENTER);
            public void mouseEntered(MouseEvent evt) {
                shipPanel.setVisible(false);
                computerPanel.add(instructionLabel);
            }
            public void mouseExited(MouseEvent evt) {
                shipPanel.setVisible(true);
                computerPanel.remove(instructionLabel);

            }
        });
    }

    /**
     * Handles the inputs on the grid including mouse events and button actions
     * This method sets up the interactions for placing ships on the grid.
     * Requires 4 parameters, local variables declared in the startGUI() method
     */
    public static void gridActivity(JPanel computerPanel, JPanel shiPanel, JPanel buttonPanel, JLabel screenText) { 
        // Call for every button in the 2d array
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++) {
            int newX = x; // Cant use the x from the for loop inside a local method
            int newY = y;
                // Add mouse listener to each grid button
                uGrid[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
                    // If the user's cursor hovers over the button
                    public void mouseEntered(MouseEvent evt) {
                        currentHoveredButton = (JButton) evt.getSource(); // Track the current hovered button
                        if (userSelection > 0) {
                            Color colour = Color.black;
                            // If the ship preview is vertical
                            if (shipRotation.equals("vertical")) { 
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newY+n > 9) {
                                        n = userSelection+1;
                                    } else {
                                        for (int z=0; z<userSelection+1; z++) {
                                            if (newY+userSelection > 9 || uGridData[newX][newY+userSelection-z] < 6 
                                            && uGridData[newX][newY+userSelection-z] != 0) { // Checking if it is placed over another ship
                                                colour=Color.red; // The preview will highlight red
                                            }
                                        }
                                        if (uGrid[newX][newY+n].getBackground() != Color.DARK_GRAY) { // If no ship is placed in the way
                                            uGrid[newX][newY+n].setBackground(colour);
                                        }
                                    }   
                                }   
                            } 
                            // If the ship preview is horizontal
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
                                        if (uGrid[newX+n][newY].getBackground() != Color.DARK_GRAY) { // If no ship is placed in the way
                                            uGrid[newX+n][newY].setBackground(colour);
                                        }
                                        
                                    }   
                                }   
                            } 
                        }     
                    }
                    // If the user's cursors exits hover over the button
                    public void mouseExited(MouseEvent evt) {
                        currentHoveredButton = null;
                        if (userSelection > 0) {
                            if (shipRotation.equals("vertical")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newY+n > 9) {
                                        n = userSelection+1;   
                                    } else {
                                        if (uGrid[newX][newY+n].getBackground() != Color.DARK_GRAY) { //Checking a ship isnt placed 
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
                                        if (uGrid[newX+n][newY].getBackground() != Color.DARK_GRAY) { //Checking a ship isnt placed 
                                            uGrid[newX+n][newY].setBackground(Color.white);
                                        }
                                    }   
                                } 
                            }
                        }  
                    }
                }); 

                /*
                 * When the button is clicked this method is called
                 * Needs to check it isn't going to overlap another ship
                 * Needs to check it isn't placing out of bounds
                 */
                uGrid[x][y].addActionListener(new ActionListener(){ 
                    public void actionPerformed(ActionEvent e){
                        if (clickable == true) {
                             // Check if a ship is selected and the placement is valid
                            if (userSelection > 0 && uGrid[newX][newY].getBackground() == Color.black) { // If background isn't black it is out of bounds or over another ship  
                                // Place ship vertically                
                                if (shipRotation.equals("vertical")) {
                                    for (int n=0; n<userSelection+1; n++) {
                                        uGrid[newX][newY+n].setText(""); // If there is text it messes with the image postions 
                                        uGrid[newX][newY+n].setBackground(Color.DARK_GRAY);
                                        uGrid[newX][newY+n].setIcon(new ImageIcon(shipImage.getImage().getScaledInstance(uGrid[newX][newY+n].getWidth(), uGrid[newX][newY+n].getHeight(),Image.SCALE_DEFAULT))); 
                                        uGridData[newX][newY+n] = shipSelection+1; // Updates the state of this postion in the data variable
                                    }
                                } 
                                // Place ship horizontally
                                else if (shipRotation.equals("horizontal")) {
                                    for (int n=0; n<userSelection+1; n++) {
                                        // Does the same as before but along X-axis
                                        uGrid[newX+n][newY].setText("");
                                        uGrid[newX+n][newY].setBackground(Color.DARK_GRAY);
                                        uGrid[newX+n][newY].setIcon(new ImageIcon(shipImage.getImage().getScaledInstance(uGrid[newX+n][newY].getWidth(), uGrid[newX+n][newY].getHeight(),Image.SCALE_DEFAULT)));
                                        uGridData[newX+n][newY] = shipSelection+1;
                                    }
                                }
                                ships[shipSelection].setVisible(false); // Removes the ship from the right
                                // Reset selection and rotation
                                userSelection = 0; 
                                shipRotation = "vertical";
                                // Calls methods for buttons
                                resetButton(buttonPanel);
                                startButton(computerPanel, shiPanel, buttonPanel, screenText);
                            } else if (uGrid[newX][newY].getBackground() != Color.black) { // If ship is placed outside of bounds
                                System.out.println("Error: Ship placement out of bounds");
                            } 
                        }                        
                    }  
                }); 
            }
        }
    }

    /**
     * Resets the grid and ship buttons to their initial state
     * This method makes the reset button visible 
     * Uses actionListener to reset the state of the grid and the ships when clicked
     * Takes the buttonPanel variable to add resetButton later
     * Reset button is only visible after 1 or more ship(s) have been placed
     */
    public static void resetButton(JPanel buttonPanel) {
        // Initially add the reset button and set to visible
        buttonPanel.add(rButton);
        rButton.setVisible(true);
        
        rButton.addActionListener(new ActionListener() { // Reset button actionListener
            // When clicked
            public void actionPerformed(ActionEvent e){ 
                for (int x=0; x<ships.length; x++) {
                    // Add all ships back on the right
                    ships[x].setVisible(true); 
                    ships[x].setBorder(new LineBorder(Color.BLACK, 1)); // No ship is visibly selected
                }              
                for (int y=0; y<10; y++) {
                    for (int x=0; x<10; x++) {
                        uGrid[x][y].setVisible(true);
                        uGrid[x][y].setBackground(Color.white);
                        uGrid[x][y].setText("-");
                        uGrid[x][y].setIcon(null); // Remove images
                        uGridData[x][y] = 0; // Reseting data
                    }
                }
                // Reset the buttons to be invisible
                rButton.setVisible(false); 
                startButton.setVisible(false); 
            }
        });
    } 

    /**
     * Implements of the KeyListener
     * I only need the key pressed lisntener
     * Other 2 are required for the KeyListener implement
     */
    @Override
    public void keyTyped(KeyEvent e) { // Typed text
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
            for (int y=0; y<10; y++) {
                for (int x=0; x<10; x++) {
                    if (uGrid[x][y].getBackground() != Color.DARK_GRAY) { // If no ship is placed on this tile
                        uGrid[x][y].setBackground(Color.white); // Reset placement previews on the grid 
                    }                       
                }
            }
            // Re-trigger the mouseEntered event mannually if a button is currently hovered
            if (currentHoveredButton != null) {
                MouseEvent enterEvent = new MouseEvent(currentHoveredButton, MouseEvent.MOUSE_ENTERED, // Which button and what event
                System.currentTimeMillis(), // Gives the time the event occured
                0, 0, 0, 0, false); // 0 modifiers. Neccecry for calling manual event
                for (MouseListener listener : currentHoveredButton.getMouseListeners()) {
                    listener.mouseEntered(enterEvent); // Manually call the mouse entered event
                }
            }
        }
    }
    @Override
    public void keyReleased(KeyEvent e) { // Key released
    } 

    /**
     * Sets up and displays the start button, making it visible only when all ships are placed
     * When the start button is clicked, it initiates the game method and updates the UI accordingly
     */
    public static void startButton(JPanel computerPanel, JPanel shipPanel, JPanel buttonPanel, JLabel screenText) {
        startButton.setVisible(false); // When button is created it needs to be invisible
        buttonPanel.add(startButton); 
        if (ships[0].isVisible() == false && ships[1].isVisible() == false && ships[2].isVisible() == false
        && ships[3].isVisible() == false && ships[4].isVisible() == false) { // Condition for when all ships are placed
            startButton.setVisible(true); // Make start button visible
            // ActionListener of start button
            startButton.addActionListener(new ActionListener() { 
                public void actionPerformed(ActionEvent e){ 
                    startGame(computerPanel); // Calling startGame method 
                    // Removes panels from the right and bottom of the screen
                    textPanel.remove(screenText);
                    computerPanel.remove(buttonPanel);
                    computerPanel.remove(shipPanel);
                    startButton.setVisible(false);
                    buttonPanel.remove(rButton);
                }
            });
        }    
    }
    
    /**
     * Initializes and starts the game by setting up the computer's grid and labels
     * This method also sets up action listeners for the computer grid buttons to handle user interactions
     */
    public static void startGame(JPanel computerPanel) {
        System.out.println("Game Started");
        String[] alphabetString = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        JLabel[] labels = new JLabel[20]; // Labels for right and bottom of the computer grid
        JLabel screenText = new JLabel("",JLabel.CENTER); // Text is currently empty

        /* Creates and installs JPanel for the right side of the screen */
        JPanel computerGrid = new JPanel(new GridLayout(10, 10));  
        JPanel labelsLeft = new JPanel(new GridLayout(10, 1));
        JPanel labelsBottom = new JPanel(new GridLayout(1, 10));
        labelsBottom.setBorder(BorderFactory.createEmptyBorder(0,0,0,10));
        computerPanel.add(computerGrid, BorderLayout.CENTER); 
        computerPanel.add(labelsLeft, BorderLayout.EAST);     
        computerPanel.add(labelsBottom, BorderLayout.SOUTH); 
        
        /* Adding buttons for computer grid */
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++)   {
                int xx = x; // I need these because the program dosen't allow methods to send variables from 
                int yy=y;
                cGridData[x][y] = 0; // Sets the entire computer grid to notihing, look at top for number reference
                cGrid[x][y]=new JButton("-");
                /* Colour and properties of button */
                cGrid[x][y].setMargin(new Insets(0,0,0,0));
                cGrid[x][y].setBackground(Color.white);
                cGrid[x][y].setOpaque(true);
                cGrid[x][y].setBorderPainted(false);
                // Action listener for grid button presses
                cGrid[x][y].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e){
                        if (clickable == true) {
                            if (turn == 0) { // If first turn
                                // Add text to the text panel at the bottom of the screen
                                textPanel.add(screenText);
                                refreshScreen();
                            }
                            String text = userShot(xx, yy); // Calls for userShot method, which will return hit result
                            // Sends the result of the usershot to a timer method, the timer method creates the text on the bottom on the screen
                            screenTimer(text, screenText);

                        }   
                    }
                });
                computerGrid.add(cGrid[x][y]); // Adds button to the grid panel
            }
        }
        
        // Adding labels for computer grid, same code as the user's grid labels
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

    /**
     * This method creates the computer ships randomly
     * The location of the ship is stored in a 2d array
     * The ships cannot overlap or be placed out of bounds
     * The rotation, xpostion, and ypostion are all random
     */
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
                            cGridData[xPos][yPos+zz] = 0; // Undo ship in that postion
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
    }

    /**
     * This method is responsible for returing the result of the user's shot
     * The method inputs the x and y postion of the shot
     * It returns a string of the result of the shot
     * Inside the method it updates all the variables related to the result of the shot
     */
    public static String userShot(int xPos, int yPos) {
        String text;
        if (cGridData[xPos][yPos] >= 6) { //Already shot this position, either hit or miss
            text = "Already shot here";
        } else if (cGridData[xPos][yPos] < 6 && cGridData[xPos][yPos] !=0) { // A ship is hit
            cShipHitPoints[cGridData[xPos][yPos]-1]--; // Lower hitpoint of the hit computer ship by 1
            if (cShipHitPoints[cGridData[xPos][yPos]-1] == 0) {
                switch (cGridData[xPos][yPos]-1) { // Which ship was destroyed
                    case 0:
                        text = "You have destroyed the Destroyer!"; 
                        break;
                    case 1:
                        text = "You have destroyed the Submarine!"; 
                        break;
                    case 2:
                        text = "You have destroyed the Cruiser!";
                        break;
                    case 3:
                        text = "You have destroyed the Battleship!";
                        break;
                    case 4:
                        text = "You have destroyed the Carrier!";
                        break;
                    default:
                        text = "Error: value cGridData[xPos][yPos]-1";
                        break;
                }
                boldText = true; // Make text bold becase a ship was hit
            } else { // A ship was not destroyed
                text = "You hit a Battleship";
                boldText = false;
            }
            // Update the grid upon the shot
            cGridData[xPos][yPos] = 6;
            cGrid[xPos][yPos].setBackground(Color.RED);
            turn++;
        } else { // Nothing is hit
            cGridData[xPos][yPos] = 7;
            cGrid[xPos][yPos].setBackground(Color.BLACK);
            text = "Miss! Nothing was hit";
            boldText = false;
            turn++;
        }
        return (text);
    }

    /**
     * This method returns the result of the computer shot
     * It includes the computer algorithm, that simulates playing against a real person
     * Like the userShot it returns a string
     * The bottom of the method uses the x and y pos to check if hit/miss
     * If it shoots at a already shot at postion, it recalls the method later on because the turn doesn't progress
     */
    public static String computerShot() {
        /* Local variables */
        String text;
        int xPos = (int)Math.floor(Math.random()*10); // 0 to 9
        int yPos = (int)Math.floor(Math.random()*10); // 0 to 9

        if (lastX != -1 && lastY != -1) { // If last shot was a hit
            /* Determines the next shot to hit around where the last shot was */
            int n;
            if (hitDirection == "vertical") {
                if (lastY == 0) { // Top edge
                    n = 1;
                } else if (lastY == 9) { // Bottom edge
                    n = 2;
                } else { // Y is between 1-8
                    int[] list = {1, 2};
                    n = list[(int)Math.floor(Math.random()*2)]; // 1 or 2
                }   
            } else if (hitDirection == "horizontal") {
                if (lastX == 0) { // Left edge
                    n = 0;
                } else if (lastX == 9) { // Right edge
                    n = 3;
                } else { // X is between 1-8
                    int[] list = {0, 3};
                    n = list[(int)Math.floor(Math.random()*2)]; // 0 or 3
                }       
            } else { // Hit direction is null (unknown)
                if (lastX == 0 && lastY == 0) { // Top left corner
                    n = (int)Math.floor(Math.random()*2); // 0 - 1 
                } else if (lastX == 0 && lastY == 9) { // Bottom left corner
                    int[] list = {0, 2};
                    n = list[(int)Math.floor(Math.random()*2)]; // 0 or 2
                } else if (lastX == 9 && lastY == 0) { // Top right corner
                    int[] list = {1, 3};
                    n = list[(int)Math.floor(Math.random()*2)]; // 1 or 3
                } else if (lastX == 9 && lastY == 9) { // Bottom right corner
                    n = (int)Math.floor(Math.random()*2)+2; // 2 or 3
                } else if (lastX == 0) { // Left edge
                    n = (int)Math.floor(Math.random()*3); // 0 - 2 
                } else if (lastX == 9) { // Right edge
                    n = (int)Math.floor(Math.random()*3)+1; // 1 - 3 
                } else if (lastY == 0) { // Top edge
                    int[] list = {0, 1, 3};
                    n = list[(int)Math.floor(Math.random()*3)]; // 0, 1, or 3 
                } else if (lastY == 9) { // Bottom edge
                    int[] list = {0, 2, 3};
                    n = list[(int)Math.floor(Math.random()*3)]; // 0, 2, or 3 
                }else { // Between (1-8, 1-8)
                    n = (int)Math.floor(Math.random()*4); // 0 - 3
                }
            }
       
            switch (n) { // Using n to find the next shot
                case 0: // One tile to right
                    xPos = lastX + 1;
                    yPos = lastY; 
                    break;
                case 1: // One tile down
                    xPos = lastX;
                    yPos = lastY + 1;  
                    break;
                case 2: // One tile up
                    xPos = lastX;
                    yPos = lastY - 1;
                    break; 
                case 3: // One tile left
                    xPos = lastX - 1;
                    yPos = lastY;
                    break;
                default:
                    System.out.println("Error: "+n);
                    break;
            }
        } 
        
        if (uGridData[xPos][yPos] >= 6) { // Already shot this position, either hit or miss
            text = "Error: Already shot here";
             /*
              * This is my fail safe
              * It checks if tiles around tile has already been hit
              * If there are no tiles around to hit it will reset shooting and fire randomly
              * If there are tiles around then it will fire at them
              * If I don't add this then the program will essentially crash
              */
            if (hitDirection == "vertical") {
                if (lastY-1 >= 0) { // Is one tile up is inside bounds
                    if (uGridData[lastX][lastY-1] >= 6) { // Has that tile already been shot at
                        if (lastY+1 <= 9) { // Is one tile down inside bounds
                            if (uGridData[lastX][lastY+1] >= 6) { 
                                if (lastY == originalY) { // Has the postion already been reset (ie last turn)
                                    // Program is stuck in a loop
                                    System.out.println("Error loop"); 
                                    if (lastX-1 >= 0) { // Is one tile left out of bounds
                                        if (uGridData[lastX-1][lastY] <= 5) { // is this tile not been shot at
                                            hitDirection = "horizontal"; // Change the hitDirection because there is a tile it can hit
                                        } else {
                                            // Reset shooting, fire randomly
                                            hitDirection = null;
                                            lastX = -1;
                                            lastY = -1;
                                        }
                                    } else if (lastX+1 <= 9) { // Is one tile left out of bounds
                                        // Rest is same as above 
                                        if (uGridData[lastX+1][lastY] <= 5) {
                                            hitDirection = "horizontal";
                                        } else {
                                            hitDirection = null;
                                            lastX = -1;
                                            lastY = -1;
                                        }
                                    } else { // Cannot find a shootable tile around this tile
                                        // Reset shooting, fire randomly
                                        hitDirection = null;
                                        lastX = -1;
                                        lastY = -1;
                                    }
                                   
                                } else {
                                    lastY = originalY; // Reset the shooting postion to original location
                                    // Next time it will fire in a new direction
                                }
                            }
                        } else if (lastY == originalY) {
                            System.out.println("Error loop"); 
                            hitDirection = null;
                            lastX = -1;
                            lastY = -1;
                        } else {
                            lastY = originalY; // Reset the shooting postion to original location
                            // Next time it will fire in a new direction
                        }
                    }
                } else if (uGridData[lastX][lastY+1] >= 6) {
                    if (lastY == originalY) {
                        System.out.println("Error loop"); 
                        hitDirection = null;
                        lastX = -1;
                        lastY = -1;
                    } else {
                        lastY = originalY; // Reset the shooting postion to original location
                        // Next time it will fire in a new direction
                    }
                   
                }
            } else if (hitDirection == "horizontal") { // This is same as above but for horizontal hit direction and in reverse 
                // Not adding comments because they are the same as above but for x instead of y
                if (lastX-1 >= 0) {
                    if (uGridData[lastX-1][lastY] >= 6) {
                        if (lastX+1 <= 9) {
                            if (uGridData[lastX+1][lastY] >= 6) {
                                if (lastX == originalX) {
                                    if (lastY-1 >= 0) {
                                        if (uGridData[lastX][lastY-1] <= 5) {
                                            hitDirection = "vertical";
                                        } else {
                                            hitDirection = null;
                                            lastX = -1;
                                            lastY = -1;
                                        }
                                    } else if (lastY+1 <= 9) {
                                        if (uGridData[lastX][lastY+1] <= 5) {
                                            hitDirection = "vertical";
                                        } else {
                                            hitDirection = null;
                                            lastX = -1;
                                            lastY = -1;
                                        }
                                    } else {
                                        hitDirection = null;
                                        lastX = -1;
                                        lastY = -1;
                                    }
                                } else {
                                    lastX = originalX;
                                }  
                            }
                        } else if (lastX == originalX) {
                            System.out.println("Error loop"); 
                            hitDirection = null;
                            lastX = -1;
                            lastY = -1;
                        } else {
                            lastX = originalX; 
                        }
                    }
                } else if (uGridData[lastX+1][lastY] >= 6) {
                    if (lastX == originalX) {
                        System.out.println("Error loop"); 
                        hitDirection = null;
                        lastX = -1;
                        lastY = -1;
                    } else {
                        lastX = originalX;
                    }  
                }
            }
            // If nothing happens then there is another possible shot so it will try again from the start
                   
        } else if (uGridData[xPos][yPos] < 6 && uGridData[xPos][yPos] != 0) { // Hit
            uGrid[xPos][yPos].setIcon(new ImageIcon(shipImageHit.getImage().getScaledInstance(uGrid[xPos][yPos].getWidth(), uGrid[xPos][yPos].getHeight(),Image.SCALE_DEFAULT))); // Change the image to the tile
            uShipHitPoints[(uGridData[xPos][yPos]-1)]--; // Lower hitpoint of the hit user ship by 1
            if (lastX > -1 || lastY > -1) { // If last shot was a hit (and this shot)
                if (hitDirection == null) { // No direction is currently assigned
                    if (lastX < xPos || lastX > xPos) {
                        hitDirection = "horizontal";                 
                    } else if (lastY < yPos || lastY > yPos) {
                        hitDirection = "vertical"; 
                    }
                    System.out.println("Hit Direction: "+hitDirection); 
                }   
            }
            if (hitDirection == null) {
                // Original postion of shot
                // Used later in code if needed
                originalX = xPos;
                originalY = yPos;
            }
            
            // Update last shots
            lastX = xPos; 
            lastY = yPos;
            if (uShipHitPoints[uGridData[xPos][yPos]-1] == 0) { // If the entire shis is destroyed
                boldText = true; // Text will be bold
                switch (uGridData[xPos][yPos]-1) { // Which ship was destroyed (name)
                    case 0:
                        text = "Opponent has destroyed your Destroyer!"; 
                        break;
                    case 1:
                        text = "Opponent has destroyed your Submarine!"; 
                        break;
                    case 2:
                        text = "Opponent has destroyed your Cruiser!";
                        break;
                    case 3:
                        text = "Opponent has destroyed your Battleship!";
                        break;
                    case 4:
                        text = "Opponent has destroyed your Carrier!";
                        break;
                    default:
                        text = "Error: value uGridData[xPos][yPos]-1";
                        break;
                }
                // Reset last variables and hit direction if the ship is destroyed
                lastX = -1; // Next shot will be random 
                lastY = -1;
                hitDirection = null;
            } else {
                text = "Opponent has hit a Battleship!";
                boldText = false;
            }
            uGridData[xPos][yPos] = 6; // Tile is now miss
            turn++;
        } else { // Nothing is hit
            boldText = false; 
            uGrid[xPos][yPos].setBackground(Color.black); // Show that the tile missed
            uGridData[xPos][yPos] = 7;
            text = "Opponent missed! Nothing was hit";
            if (lastX > -1 || lastY > -1) { // Last shot was a hit
                if (hitDirection == null) {
                    if (lastX > 0 && lastX < 9) {
                        if (lastX < xPos && uGridData[lastX-1][lastY] == 6 || lastX > xPos && uGridData[lastX+1][lastY] == 6) { // Left and right tiles are both misses
                            hitDirection = "vertical"; // Change hit direction to vertical (was horizontal)
                        }
                    } else if (lastX == 0 && lastY == 0) { // Top left
                        if (lastX < xPos) { // Shot was to the right of previous shot
                            hitDirection =  "vertical";
                        }
                    } else if (lastX == 0 && lastY == 9) { // Bottom left
                        if (lastX < xPos) {
                            hitDirection =  "vertical";
                        }
                    } else if (lastX == 9 && lastY == 0) { // Top right
                        if (lastX > xPos) { // Shot was to the left of previous shot
                            hitDirection = "vertical"; 
                        }
                    } else if (lastX == 9 && lastY == 9) { // Bottom right
                        if (lastX > xPos) {
                            hitDirection = "vertical"; 
                        }
                    } else if (lastX == 0) { // Left edge
                        if (lastX < xPos) {
                            hitDirection = "vertical"; 
                        }
                        
                    } else if (lastX == 9) { // Right edge
                        if (lastX > xPos)
                        hitDirection = "vertical"; 
                    }
                    
                    if (lastY > 0 && lastY < 9) {
                        if (lastY < yPos && uGridData[lastX][lastY-1] == 6 || lastY > yPos && uGridData[lastX][lastY+1] == 6) {
                            hitDirection = "horizontal";
                        }
                    }else if (lastY == 0 && lastX == 0) { // Top left
                        if (lastY < yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 0 && lastX == 9) { // Top right
                        if (lastY < yPos) {
                            hitDirection = "horizontal";
                        }  
                    } else if (lastY == 9 && lastX == 0) { // Bottom left
                        if (lastY > yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 9 && lastX == 9) { // Bottom right
                        if (lastY > yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 0) { // Top edge
                        if (lastY < yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 9) { // Bottom edge
                        if (lastY > yPos) {
                            hitDirection = "horizontal";
                        }
                    }
                    System.out.println("Hit Direction: "+hitDirection);
                } else {
                    // Go back to the first shot because it has reached the end of the ship
                    lastX = originalX;
                    lastY = originalY;
                }            
            }
            turn++;
        }
        return (text);
    }

    /**
     * This method is called to check if the game is over. 
     * It checks if the computer has destroyed all the users ships, or if the user has destroyed all the computers ships
     * If one of the players win, then it calls the screenTimer method that displays the text at the bottom of this screen
     * The boolean gameOver is a global variable to prevent any inputs after the game is over
     * This method is called once every turn
     * The method parameter includes the JLabel at the bottom of the screen
     */
    public static void winChecker(JLabel screenText) {
        if (uShipHitPoints[0] == 0 && uShipHitPoints[1] == 0 && uShipHitPoints[2] == 0 &&
        uShipHitPoints[3] == 0 && uShipHitPoints[4] == 0) { // Are all ships destroyed (ie hitpoints are 0)
            System.out.println("Game over, computer wins");
            gameOver = true;
            screenTimer("Game over, computer wins", screenText); 
        } else if (cShipHitPoints[0] == 0 && cShipHitPoints[1] == 0 && cShipHitPoints[2] == 0 &&
        cShipHitPoints[3] == 0 && cShipHitPoints[4] == 0) { // Checks the same but for the the computer's ships
            System.out.println("Game over, user wins");
            gameOver = true;
            screenTimer("Game over, user wins", screenText);
        }   
    }

    /**
     * This method uses a timer to create the text shown at the bottom of the screen
     * The first parameter is the text to display(string), the second is the JLabel that the text will be written on
     * The timer has a constant 30ms delay, each character is revealed after 30ms
     * This method also calls for the computerShot method after it has completed the user's shot timer
     * It also calls the win checker method, that will break the timer loop if the game is over
     */
    public static void screenTimer(String text, JLabel screenText) {
        if (boldText) {
            // Make text bold
            screenText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        } else {
            // Default
            screenText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        }
        Timer timer = new Timer(30, new ActionListener() { //Timer runs every 30ms
            int i = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                if (i < text.length()) {
                    clickable = false;
                    // Reveal another character
                    screenText.setText(text.substring(0, i + 1)); // Starts from 0(first character) and adds each character up to the int of i
                    i++;
                } else { // All characters are visible
                    ((Timer)e.getSource()).stop(); // Stop the timer
                    if (!gameOver) { 
                        winChecker(screenText); // Check if the user has won in the last turn
                    }
                    if (turn%2 == 1 && !gameOver) { //If it is end of user's turn and user didn't win
                        String newText = null;
                        if (gameOver==false) {
                            while (turn%2 == 1) { // Continue calling until computer has hit a valid tile
                                newText = computerShot(); 
                            }
                            try {
                                Thread.sleep(500); // Pause the GUI for 0.5s
                                screenTimer(newText, screenText); // Recall this method to show the result of computer's shot
                            } catch (InterruptedException ie) { //Error catch             
                                Thread.currentThread().interrupt(); 
                            } 
                        }      
                    } else if (gameOver) { 
                        // If game is over make the grid not clickable so you cannot start a new turn
                        clickable = false;
                    } else { // It is the users turn
                        clickable = true;
                        winChecker(screenText);
                    }  
                }
            }
        });
        timer.start();
    }

    /**
     * Method used to run the code
     */
    public static void main(String[] args) { 
        startGUI(); // Calls the grid method at the start of the program
        new Main(); // Constructor for Jframe(GUI) and ships
    }
}  