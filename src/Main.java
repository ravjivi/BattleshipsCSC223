/* 
* PROJECT TITLE: Battleships
* VERSION or DATE: Version 5.5, 23.07.24
* AUTHOR: Viraaj Ravji
* DETAILS:
 * More comments and small changes to computer AI
*/

/*LIBRARY*/
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

public class Main implements KeyListener {
    /* CLASS VARIABLES */
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    // Takes the height of the screen and calculates size of the GUI
    private static final int GUIHEIGHT = (int) screenSize.getHeight() / 2;
    private static final int GUIWIDTH = GUIHEIGHT * 5 / 2; // Width of the GUI proportional to the width of the scrreen

    private static JFrame f = new JFrame("Battleships"); // Creates JFrame, the GUI window
    private static JPanel guiPanel = new JPanel(new GridLayout(1, 2, 20, 0)); // 1 row,2 columns,20px horizontal gap,0px
                                                                              // vertical gap
    private static JPanel textPanel = new JPanel();

    private static int GUITAB = 30; // This will scale with the size of the GUI, default is 30
    private static JButton[][] uGrid = new JButton[10][10]; // User Grid
    private static JButton[][] cGrid = new JButton[10][10]; // Computer Grid
    private static int[][] uGridData = new int[10][10]; // 0-nothing, 1-5-ship, 6-hit ship, 7-miss
    private static int[][] cGridData = new int[10][10];
    private static int[] uShipHitPoints = { 2, 3, 3, 4, 5 }; // How many hits left on each user's battleship
    private static int[] cShipHitPoints = { 2, 3, 3, 4, 5 };
    private static JButton[] ships = new JButton[5];
    private static int tileHeight = GUIHEIGHT / 10; // Tile spacing vertical
    private static int tileWidth = GUIWIDTH / 20; // Tile spacing horizontal
    private static int userSelection;
    private static int shipSelection;
    private static JButton currentHoveredButton = null;
    private static final JButton rButton = new JButton("Reset");; // reset JButton
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

    /* IMAGES */
    private static ImageIcon shipImageH2 = new ImageIcon("assets/ship_texture_h2.jpg");
    private static Image scaleshipImageH2 = shipImageH2.getImage().getScaledInstance(tileWidth, tileHeight * 2,
            Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH3 = new ImageIcon("assets/ship_texture_h3.jpg");
    private static Image scaleshipImageH3 = shipImageH3.getImage().getScaledInstance(tileWidth, tileHeight * 3,
            Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH4 = new ImageIcon("assets/ship_texture_h4.jpg");
    private static Image scaleshipImageH4 = shipImageH4.getImage().getScaledInstance(tileWidth, tileHeight * 4,
            Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH5 = new ImageIcon("assets/ship_texture_h5.jpg");
    private static Image scaleshipImageH5 = shipImageH5.getImage().getScaledInstance(tileWidth, tileHeight * 5,
            Image.SCALE_DEFAULT);

    private static ImageIcon shipImage = new ImageIcon("assets/ship_texture.jpg");
    private static ImageIcon shipImageHit = new ImageIcon("assets/ship_texture_hit.jpg");

    /*
     * This is my JFrame constructor
     * It is used to setup all the properies for my GUI
     * I also use this to add keyListeners to grid buttons and ship buttons. When
     * these are selsected you can press keys like 'r' because it is a non static
     * method
     * It also runs a method when the GUI is resized. It updates the size of the
     * images and GUITAB, which is the space between the JPanels inside the GUI
     * layout
     */

    public Main() {
        // GUI window properties for JFrame
        System.out.println(GUIHEIGHT);
        System.out.println(GUIWIDTH);
        f.setSize(GUIWIDTH + GUITAB * 3, GUIHEIGHT + GUITAB * 3);
        f.setFocusTraversalKeysEnabled(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setFocusable(true);
        f.addKeyListener(this);
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (x < 5 && y == 0) { // For all ship buttons add key listener
                    ships[x].addKeyListener(this);
                }
                uGrid[x][y].addKeyListener(this);
            }
        }
        f.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) { // Run when the GUI is resized
                for (int y = 0; y < 10; y++) {
                    for (int x = 0; x < 10; x++) {
                        if (uGridData[x][y] != 0 && uGridData[x][y] < 6) {
                            // Update icon of the button
                            uGrid[x][y].setIcon(new ImageIcon(shipImage.getImage().getScaledInstance(
                                    uGrid[x][y].getWidth(), uGrid[x][y].getHeight(), Image.SCALE_DEFAULT)));
                        } else if (uGridData[x][y] == 6) {
                            uGrid[x][y].setIcon(new ImageIcon(shipImageHit.getImage().getScaledInstance(
                                    uGrid[x][y].getWidth(), uGrid[x][y].getHeight(), Image.SCALE_DEFAULT)));
                        }
                    }
                }
                GUITAB = f.getWidth() / 75;
                guiPanel.setLayout(new GridLayout(1, 2, GUITAB, 0)); // Changes the gap between the left grid and right
                                                                     // grid
            }
        });
        refreshScreen();
    }

    /*
     * Callled when new objects are added to the GUI like a JLabel
     * Used to fix bug where buttons called after f.setVisible are invisible
     */
    public static void refreshScreen() {
        f.setVisible(false);
        f.setVisible(true);
    }

    /*
     * Creates the starting GUI for when the game is run
     * Creates all the JPanels visible before the game is started
     * JPanels allow the GUI to scale nicely i.e JButtons are smaller when the gui
     * is downscaled
     * User's grid are 100 buttons alligned in a 10x10 format
     * JLabels are added to the left and bottom of the grid to show the coordinates
     * of the grid
     * JPanel on the right are the ships placed with a gridBagLayout
     * Finally the panel on the bottom is for the text
     */
    public static void startGUI() {
        // Initial GUI variables
        String[] alphabetString = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
        JLabel[] labels = new JLabel[20];
        JPanel userPanel = new JPanel(new BorderLayout());
        JPanel userGrid = new JPanel(new GridLayout(10, 10));
        JPanel labelsLeft = new JPanel(new GridLayout(10, 1));
        JPanel labelsBottom = new JPanel(new GridLayout(1, 10));
        labelsBottom.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        // Creating panels for user grid, labels, computer panel, ships, and buttons
        JPanel computerPanel = new JPanel(new BorderLayout());
        JPanel shipPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel();

        // Adding buttons to GUI
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                uGrid[x][y] = new JButton("-");
                // Colour and properties of button
                uGrid[x][y].setMargin(new Insets(0, 0, 0, 0));
                uGrid[x][y].setBackground(Color.white);
                uGrid[x][y].setOpaque(true);
                uGrid[x][y].setBorderPainted(false);
                userGrid.add(uGrid[x][y]);
            }
        }
        // adding labels on GUI
        for (int x = 0; x < 11; x++) {
            if (x < 10) {
                labels[x] = new JLabel(" " + alphabetString[x] + " "); // Spaces are just to help alignment
                labelsLeft.add(labels[x]);
            } else if (x == 10) {
                for (int y = 0; y < 10; y++) {
                    labels[y + 10] = new JLabel(String.valueOf(y + 1), JLabel.CENTER);
                    labelsBottom.add(labels[y + 10]);
                }
            }
        }
        // Set constraints for ship buttons
        GridBagConstraints shipsGBC = new GridBagConstraints();
        shipsGBC.fill = GridBagConstraints.NONE; //
        shipsGBC.insets = new Insets(5, 5, 5, 5); // Padding around JButtons so they aren't touching each other
        // Adding ships to right of grid
        for (int x = 0; x < 5; x++) {
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
                    System.out.println("Error: h");
                    break;
            }
            /*
             * ActionListner for when the ships are clicked
             * Identifies which button is pressed
             * Assigns userSelection the length of the ship
             * Assigns shipSelection to which ship is clicked
             */
            ships[x].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Object src = e.getSource();
                    for (int x = 0; x < 5; x++) {
                        if (src.equals(ships[x])) {
                            System.out.println("User has selected Ship " + (x + 1));
                            ships[shipSelection].setBorder(new LineBorder(Color.BLACK, 1)); // Makes previosly selected
                                                                                            // ship default outline
                            ships[x].setBorder(new LineBorder(Color.BLUE, 2)); // Makes new selection outlined in blue
                            userSelection = x + 1;
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
        JLabel screenText = new JLabel("Place all 5 ships to start", JLabel.CENTER); // Centres the text inside the
                                                                                     // JLabel
        screenText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        textPanel.add(screenText);
        f.add(textPanel, BorderLayout.SOUTH);
        // Instruction button
        JButton instructionButton = new JButton("Instructions");
        buttonPanel.add(instructionButton);
        instrcutions(instructionButton, computerPanel, shipPanel); // Calls the method to tell the button what to do
        gridActivity(computerPanel, shipPanel, buttonPanel, screenText); // Grid activity is for user cursor hovering
                                                                         // and clicks
    }

    /*
     * Adds a mouseListener to the instructions button for when hovered
     * Uses html text layout to add break between lines
     * When the mouse hovers the button the ships need to hide, vice versa
     */
    public static void instrcutions(JButton instructionButton, JPanel computerPanel, JPanel shipPanel) {
        instructionButton.addMouseListener(new java.awt.event.MouseAdapter() {
            JLabel instrunctionLabel = new JLabel("<html>Battleships!<br>" // Text on JLabel
                    + "- In this game you play against the computer<br>" // br is a line break
                    + "- The objective is to sink all the computer's ships before it sinks all of yours<br>"
                    + "- Start by placing all your ships. Click a ship on the right to select it, and click again anywhere on the grid to place it. Press R on your keyboard to rotate the ship<br>"
                    + "- Once all ships are placed you can start the game<br>"
                    + "- Start by clicking somewhere on the computer's(left) grid to fire a shot<br>"
                    + "- The tile will change colour if it is a hit or miss, aswell as saying below the grid<br>"
                    + "- The computer will return a shot<br>"
                    + "- The game will end when either player destroys all 5 of the opponents battleships</html>",
                    JLabel.CENTER);

            public void mouseEntered(MouseEvent evt) {
                shipPanel.setVisible(false);
                computerPanel.add(instrunctionLabel);
            }

            public void mouseExited(MouseEvent evt) {
                computerPanel.remove(instrunctionLabel);
                shipPanel.setVisible(true);
            }
        });
    }

    /*
     * Handles the inputs on the grid including mouse events and button actions
     * This method sets up the interactions for placing ships on the grid.
     * Requires 4 parameters, local variables declared in the startGUI() method
     */
    public static void gridActivity(JPanel computerPanel, JPanel shiPanel, JPanel buttonPanel, JLabel screenText) {
        // Call for every button in the 2d array
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
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
                                for (int n = 0; n < userSelection + 1; n++) {
                                    if (newY + n > 9) {
                                        n = userSelection + 1;
                                    } else {
                                        for (int z = 0; z < userSelection + 1; z++) {
                                            if (newY + userSelection > 9
                                                    || uGridData[newX][newY + userSelection - z] < 6
                                                            && uGridData[newX][newY + userSelection - z] != 0) { // Checking
                                                                                                                 // if
                                                                                                                 // it
                                                                                                                 // is
                                                                                                                 // placed
                                                                                                                 // over
                                                                                                                 // another
                                                                                                                 // ship
                                                colour = Color.red; // The preview will highlight red
                                            }
                                        }
                                        if (uGrid[newX][newY + n].getBackground() != Color.DARK_GRAY) { // If no ship is
                                                                                                        // placed in the
                                                                                                        // way
                                            uGrid[newX][newY + n].setBackground(colour);
                                        }
                                    }
                                }
                            }
                            // If the ship preview is horizontal
                            if (shipRotation.equals("horizontal")) {
                                for (int n = 0; n < userSelection + 1; n++) {
                                    if (newX + n > 9) {
                                        n = userSelection + 1;
                                    } else {
                                        for (int z = 0; z < userSelection + 1; z++) {
                                            if (newX + userSelection > 9
                                                    || uGridData[newX + userSelection - z][newY] < 6
                                                            && uGridData[newX + userSelection - z][newY] != 0) { // Checking
                                                                                                                 // if
                                                                                                                 // it
                                                                                                                 // is
                                                                                                                 // placed
                                                                                                                 // over
                                                                                                                 // another
                                                                                                                 // ship
                                                colour = Color.red;
                                            }
                                        }
                                        if (uGrid[newX + n][newY].getBackground() != Color.DARK_GRAY) { // If no ship is
                                                                                                        // placed in the
                                                                                                        // way
                                            uGrid[newX + n][newY].setBackground(colour);
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
                                for (int n = 0; n < userSelection + 1; n++) {
                                    if (newY + n > 9) {
                                        n = userSelection + 1;
                                    } else {
                                        if (uGrid[newX][newY + n].getBackground() != Color.DARK_GRAY) { // Checking a
                                                                                                        // ship isnt
                                                                                                        // placed
                                            uGrid[newX][newY + n].setBackground(Color.white);
                                        }

                                    }
                                }
                            }
                            if (shipRotation.equals("horizontal")) {
                                for (int n = 0; n < userSelection + 1; n++) {
                                    if (newX + n > 9) {
                                        n = userSelection + 1;
                                    } else {
                                        if (uGrid[newX + n][newY].getBackground() != Color.DARK_GRAY) { // Checking a
                                                                                                        // ship isnt
                                                                                                        // placed
                                            uGrid[newX + n][newY].setBackground(Color.white);
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
                uGrid[x][y].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (clickable == true) {
                            // Check if a ship is selected and the placement is valid
                            if (userSelection > 0 && uGrid[newX][newY].getBackground() == Color.black) { // If
                                                                                                         // background
                                                                                                         // isn't black
                                                                                                         // it is out of
                                                                                                         // bounds or
                                                                                                         // over another
                                                                                                         // ship
                                // Place ship vertically
                                if (shipRotation.equals("vertical")) {
                                    for (int n = 0; n < userSelection + 1; n++) {
                                        uGrid[newX][newY + n].setText(""); // If there is text it messes with the image
                                                                           // postions
                                        uGrid[newX][newY + n].setBackground(Color.DARK_GRAY);
                                        uGrid[newX][newY + n].setIcon(new ImageIcon(
                                                shipImage.getImage().getScaledInstance(uGrid[newX][newY + n].getWidth(),
                                                        uGrid[newX][newY + n].getHeight(), Image.SCALE_DEFAULT)));
                                        uGridData[newX][newY + n] = shipSelection + 1; // Updates the state of this
                                                                                       // postion in the data variable
                                    }
                                }
                                // Place ship horizontally
                                else if (shipRotation.equals("horizontal")) {
                                    for (int n = 0; n < userSelection + 1; n++) {
                                        // Does the same as before but along X-axis
                                        uGrid[newX + n][newY].setText("");
                                        uGrid[newX + n][newY].setBackground(Color.DARK_GRAY);
                                        uGrid[newX + n][newY].setIcon(new ImageIcon(
                                                shipImage.getImage().getScaledInstance(uGrid[newX + n][newY].getWidth(),
                                                        uGrid[newX + n][newY].getHeight(), Image.SCALE_DEFAULT)));
                                        uGridData[newX + n][newY] = shipSelection + 1;
                                    }
                                }
                                ships[shipSelection].setVisible(false); // Removes the ship from the right
                                // Reset selection and rotation
                                userSelection = 0;
                                shipRotation = "vertical";
                                // Calls methods for buttons
                                resetButton(buttonPanel);
                                startButton(computerPanel, shiPanel, buttonPanel, screenText);
                            } else if (uGrid[newX][newY].getBackground() != Color.black) { // If ship is placed outside
                                                                                           // of bounds
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
            public void actionPerformed(ActionEvent e) {
                for (int x = 0; x < ships.length; x++) {
                    // Add all ships back on the right
                    ships[x].setVisible(true);
                    ships[x].setBorder(new LineBorder(Color.BLACK, 1)); // No ship is visibly selected
                }
                for (int y = 0; y < 10; y++) {
                    for (int x = 0; x < 10; x++) {
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

    /*
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
            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 10; x++) {
                    if (uGrid[x][y].getBackground() != Color.DARK_GRAY) { // If no ship is placed on this tile
                        uGrid[x][y].setBackground(Color.white); // Reset placement previews on the grid
                    }
                }
            }
            // Re-trigger the mouseEntered event mannually if a button is currently hovered
            if (currentHoveredButton != null) {
                MouseEvent enterEvent = new MouseEvent(currentHoveredButton, MouseEvent.MOUSE_ENTERED, // Which button
                                                                                                       // and what event
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
     * Sets up and displays the start button, making it visible only when all ships
     * are placed
     * When the start button is clicked, it initiates the game method and updates
     * the UI accordingly
     */
    public static void startButton(JPanel computerPanel, JPanel shipPanel, JPanel buttonPanel, JLabel screenText) {
        startButton.setVisible(false); // When button is created it needs to be invisible
        buttonPanel.add(startButton);
        if (ships[0].isVisible() == false && ships[1].isVisible() == false && ships[2].isVisible() == false
                && ships[3].isVisible() == false && ships[4].isVisible() == false) { // Condition for when all ships are
                                                                                     // placed
            startButton.setVisible(true); // Make start button visible
            // ActionListener of start button
            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
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
     * This method also sets up action listeners for the computer grid buttons to
     * handle user interactions
     */
    public static void startGame(JPanel computerPanel) {
        System.out.println("Game Started");
        String[] alphabetString = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J" };
        JLabel[] labels = new JLabel[20]; // Labels for right and bottom of the computer grid
        JLabel screenText = new JLabel("", JLabel.CENTER); // Text is currently empty

        /* Creates and installs JPanel for the right side of the screen */
        JPanel computerGrid = new JPanel(new GridLayout(10, 10));
        JPanel labelsLeft = new JPanel(new GridLayout(10, 1));
        JPanel labelsBottom = new JPanel(new GridLayout(1, 10));
        labelsBottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        computerPanel.add(computerGrid, BorderLayout.CENTER);
        computerPanel.add(labelsLeft, BorderLayout.EAST);
        computerPanel.add(labelsBottom, BorderLayout.SOUTH);

        /* Adding buttons for computer grid */
        for (int y = 0; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                int xx = x; // I need these because the program dosen't allow methods to send variables from
                int yy = y;
                cGridData[x][y] = 0; // Sets the entire computer grid to notihing, look at top for number reference
                cGrid[x][y] = new JButton("-");
                /* Colour and properties of button */
                cGrid[x][y].setMargin(new Insets(0, 0, 0, 0));
                cGrid[x][y].setBackground(Color.white);
                cGrid[x][y].setOpaque(true);
                cGrid[x][y].setBorderPainted(false);
                // Action listener for grid button presses
                cGrid[x][y].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        if (clickable == true) {
                            if (turn == 0) { // If first turn
                                // Add text to the text panel at the bottom of the screen
                                textPanel.add(screenText);
                                refreshScreen();
                            }
                            String text = userShot(xx, yy); // Calls for userShot method, which will return hit result
                            // Sends the result of the usershot to a timer method, the timer method creates
                            // the text on the bottom on the screen
                            screenTimer(text, screenText);

                        }
                    }
                });
                computerGrid.add(cGrid[x][y]); // Adds button to the grid panel
            }
        }

        // Adding labels for computer grid, same code as the user's grid labels
        for (int x = 0; x < 11; x++) {
            if (x < 10) {
                labels[x] = new JLabel(" " + alphabetString[x] + " ");
                labelsLeft.add(labels[x]);
            } else if (x == 10) {
                for (int y = 0; y < 10; y++) {
                    labels[y + 10] = new JLabel(String.valueOf(y + 1), JLabel.CENTER);
                    labelsBottom.add(labels[y + 10]);
                }
            }
        }
        refreshScreen(); // Refresh screen method
        computerShip(); // Places computer ships
    }

    public static void computerShip() { // Creates the position of the computer ship
        String[] rotation = { "vertical", "horizontal" };
        int[] length = { 2, 3, 3, 4, 5 };
        int xPos = 0;
        int yPos = 0;
        for (int n = 0; n < length.length; n++) {
            String r = rotation[(int) Math.floor(Math.random() * 2)]; // Either 0 or 1 cGri
            if (r.equals("vertical")) { // If rotation is vertical
                xPos = (int) Math.floor(Math.random() * 10); // 0 to 9
                yPos = (int) Math.floor(Math.random() * (10 - length[n])); // 0 to (10-length of ship)
                for (int z = 0; z < length[n]; z++) {
                    if (cGridData[xPos][yPos + z] > 6 || cGridData[xPos][yPos + z] == 0) { // Ship is currently not
                                                                                           // placed
                        cGridData[xPos][yPos + z] = n + 1;
                    } else { // Ship is currently placed in that location
                        System.out.println("Error overlap vertical: " + xPos + ", " + (yPos + z) + ", " + n + yPos);
                        for (int zz = 0; zz < z; zz++) { // Remove currently placed tiles for this ship
                            cGridData[xPos][yPos + zz] = 0; // 0 is nothing
                        }
                        z = length[n]; // Exit z for loop
                        n -= 1; // Re-place this ship
                    }
                }
            } else { // If rotation is horizontal
                xPos = (int) Math.floor(Math.random() * (10 - length[n])); // 0 to (10-length of ship)
                yPos = (int) Math.floor(Math.random() * 10); // 0 to 9
                for (int z = 0; z < length[n]; z++) {
                    if (cGridData[xPos + z][yPos] > 6 || cGridData[xPos + z][yPos] == 0) { // Ship is currently not
                                                                                           // placed
                        cGridData[xPos + z][yPos] = n + 1;
                    } else { // Ship is currently placed in that location
                        System.out.println("Error overlap horizontal: " + (xPos + z) + ", " + yPos + ", " + n + xPos);
                        for (int zz = 0; zz < z; zz++) { // Remove currently placed tiles for this ship
                            cGridData[xPos + zz][yPos] = 0; // 0 is nothing
                        }
                        z = length[n]; // Exit z loop
                        n -= 1; // Re-place this ship
                    }
                }
            }
        }
        for (int y = 0; y < 10; y++) { // FOR TESTING
            for (int x = 0; x < 10; x++) {
                if (cGridData[x][y] != 0) {
                    System.out.print(cGridData[x][y] + " ");
                } else {
                    System.out.print(". ");
                }
            }
            System.out.println(" ");
        }
    }

    public static String userShot(int xPos, int yPos) {
        String text;
        if (cGridData[xPos][yPos] >= 6) { // Already shot this position, either hit or miss
            text = "Already shot here";
        } else if (cGridData[xPos][yPos] < 6 && cGridData[xPos][yPos] != 0) { // A ship is hit
            cShipHitPoints[cGridData[xPos][yPos] - 1]--; // Lower hitpoint of the hit computer ship by 1
            if (cShipHitPoints[cGridData[xPos][yPos] - 1] == 0) {
                switch (cGridData[xPos][yPos] - 1) {
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
                boldText = true;
            } else {
                text = "You hit a Battleship";
                boldText = false;
            }
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

    public static String computerShot() {
        String text;
        int xPos = (int) Math.floor(Math.random() * 10); // 0 to 9
        int yPos = (int) Math.floor(Math.random() * 10); // 0 to 9

        if (lastX != -1 && lastY != -1) { // If last shot was a hit
            int n;
            if (hitDirection == "vertical") {
                if (lastY == 0) { // Top edge
                    n = 1;
                } else if (lastY == 9) { // Bottom edge
                    n = 2;
                } else { // Y is between 1-8
                    int[] list = { 1, 2 };
                    n = list[(int) Math.floor(Math.random() * 2)]; // 1 or 2
                }
            } else if (hitDirection == "horizontal") {
                if (lastX == 0) { // Left edge
                    n = 0;
                } else if (lastX == 9) { // Right edge
                    n = 3;
                } else { // X is between 1-8
                    int[] list = { 0, 3 };
                    n = list[(int) Math.floor(Math.random() * 2)]; // 0 or 3
                }
            } else { // Hit direction is null (unknown)
                if (lastX == 0 && lastY == 0) {
                    n = (int) Math.floor(Math.random() * 2); // 0 - 1
                } else if (lastX == 0 && lastY == 9) {
                    int[] list = { 0, 2 };
                    n = list[(int) Math.floor(Math.random() * 2)]; // 0 or 2
                } else if (lastX == 9 && lastY == 0) {
                    int[] list = { 1, 3 };
                    n = list[(int) Math.floor(Math.random() * 2)]; // 1 or 3
                } else if (lastX == 9 && lastY == 9) {
                    n = (int) Math.floor(Math.random() * 2) + 2; // 2 or 3
                } else if (lastX == 0) {
                    n = (int) Math.floor(Math.random() * 3); // 0 - 2
                } else if (lastX == 9) {
                    n = (int) Math.floor(Math.random() * 3) + 1; // 1 - 3
                } else if (lastY == 0) {
                    int[] list = { 0, 1, 3 };
                    n = list[(int) Math.floor(Math.random() * 3)]; // 0, 1, or 3
                } else if (lastY == 9) {
                    int[] list = { 0, 2, 3 };
                    n = list[(int) Math.floor(Math.random() * 3)]; // 0, 2, or 3
                } else { // Between (1-8, 1-8)
                    n = (int) Math.floor(Math.random() * 4); // 0 - 3
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
                    System.out.println("Error: " + n);
            }
            System.out.println(n);
            System.out.println(xPos);
            System.out.println(yPos);
        }

        if (uGridData[xPos][yPos] >= 6) { // Already shot this position, either hit or miss
            text = "Error: Already shot here";
            if (hitDirection == "vertical") {
                if (lastY - 1 >= 0) {
                    if (uGridData[lastX][lastY - 1] >= 6) {
                        if (lastY + 1 <= 9) {
                            if (uGridData[lastX][lastY + 1] >= 6) {
                                if (lastY == originalY) {
                                    System.out.println("Error loop");
                                    if (lastX - 1 >= 0) {
                                        if (uGridData[lastX - 1][lastY] <= 5) {
                                            hitDirection = "horizontal";
                                        } else {
                                            hitDirection = null;
                                            lastX = -1;
                                            lastY = -1;
                                        }
                                    } else if (lastX + 1 <= 9) {
                                        if (uGridData[lastX + 1][lastY] <= 5) {
                                            hitDirection = "horizontal";
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
                                    lastY = originalY;
                                }
                            }
                        }
                    }
                }
            } else if (hitDirection == "horizontal") {
                if (lastX - 1 >= 0) {
                    if (uGridData[lastX - 1][lastY] >= 6) {
                        if (lastX + 1 <= 9) {
                            if (uGridData[lastX + 1][lastY] >= 6) {
                                if (lastX == originalX) {
                                    if (lastY - 1 >= 0) {
                                        if (uGridData[lastX][lastY - 1] <= 5) {
                                            hitDirection = "vertical";
                                        } else {
                                            hitDirection = null;
                                            lastX = -1;
                                            lastY = -1;
                                        }
                                    } else if (lastY + 1 <= 9) {
                                        if (uGridData[lastX][lastY + 1] <= 5) {
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
                        }
                    }
                }
            }

        } else if (uGridData[xPos][yPos] < 6 && uGridData[xPos][yPos] != 0) { // Hit
            uGrid[xPos][yPos]
                    .setIcon(new ImageIcon(shipImageHit.getImage().getScaledInstance(uGrid[xPos][yPos].getWidth(),
                            uGrid[xPos][yPos].getHeight(), Image.SCALE_DEFAULT)));
            uShipHitPoints[(uGridData[xPos][yPos] - 1)]--; // Lower hitpoint of the hit user ship by 1
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
            if (hitDirection == null) {
                // Original postion of shot
                originalX = xPos;
                originalY = yPos;
            }

            // Update last shots
            lastX = xPos;
            lastY = yPos;
            if (uShipHitPoints[uGridData[xPos][yPos] - 1] == 0) {
                boldText = true;
                switch (uGridData[xPos][yPos] - 1) {
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
            uGrid[xPos][yPos].setBackground(Color.black);
            uGridData[xPos][yPos] = 7;
            text = "Opponent missed! Nothing was hit";
            if (lastX > -1 || lastY > -1) {
                if (hitDirection == null) {
                    if (lastX > 0 && lastX < 9) {
                        if (lastX < xPos && uGridData[lastX - 1][lastY] == 6
                                || lastX > xPos && uGridData[lastX + 1][lastY] == 6) {
                            hitDirection = "vertical";
                        }
                    } else if (lastX == 0 && lastY == 0) {
                        if (lastX < xPos) {
                            hitDirection = "vertical";
                        }
                    } else if (lastX == 0 && lastY == 9) {
                        if (lastX < xPos) {
                            hitDirection = "vertical";
                        }
                    } else if (lastX == 9 && lastY == 0) {
                        if (lastX > xPos) {
                            hitDirection = "vertical";
                        }
                    } else if (lastX == 9 && lastY == 9) {
                        if (lastX > xPos) {
                            hitDirection = "vertical";
                        }
                    } else if (lastX == 0) {
                        if (lastX < xPos) {
                            hitDirection = "vertical";
                        }

                    } else if (lastX == 9) {
                        if (lastX > xPos)
                            hitDirection = "vertical";
                    }

                    if (lastY > 0 && lastY < 9) {
                        if (lastY < yPos && uGridData[lastX][lastY - 1] == 6
                                || lastY > yPos && uGridData[lastX][lastY + 1] == 6) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 0 && lastX == 0) {
                        if (lastY < yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 0 && lastX == 9) {
                        if (lastY < yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 9 && lastX == 0) {
                        if (lastY > yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 9 && lastX == 9) {
                        if (lastY > yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 0) {
                        if (lastY < yPos) {
                            hitDirection = "horizontal";
                        }
                    } else if (lastY == 9) {
                        if (lastY > yPos) {
                            hitDirection = "horizontal";
                        }
                    }
                    System.out.println(hitDirection);
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
            gameOver = true;
            screenTimer("Game over, computer wins", screenText);
        } else if (cShipHitPoints[0] == 0 && cShipHitPoints[1] == 0 && cShipHitPoints[2] == 0 &&
                cShipHitPoints[3] == 0 && cShipHitPoints[4] == 0) {
            System.out.println("Game over, user wins");
            gameOver = true;
            screenTimer("Game over, user wins", screenText);

        }
    }

    public static void screenTimer(String text, JLabel screenText) {
        if (boldText) {
            // Make text bold
            screenText.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        } else {
            // Default
            screenText.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        }
        Timer timer = new Timer(30, new ActionListener() { // Timer runs every 30ms
            int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < text.length()) {
                    clickable = false;
                    // Reveal one more character
                    screenText.setText(text.substring(0, index + 1)); // Starts from 0(first character) and adds each
                                                                      // character up to the int of index
                    index++;
                } else {
                    // Stop the timer once all characters are revealed
                    ((Timer) e.getSource()).stop();
                    if (!gameOver) { // Game over is false
                        winChecker(screenText);
                    }
                    if (turn % 2 == 1 && !gameOver) { // If it is end of user's turn
                        String ntext = null;
                        if (gameOver == false) {
                            while (turn % 2 == 1) { //
                                ntext = computerShot(); // Calls for userShot method, which will return hit result
                            }
                            try {
                                Thread.sleep(500); // Pause for 0.5s
                                screenTimer(ntext, screenText);
                            } catch (InterruptedException ie) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    } else if (gameOver) {
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

    public static void main(String[] args) { // Called when the program is run
        startGUI(); // Calls the grid method at the start of the program
        new Main(); // Constructor for Jframe(GUI) and ships
    }
}