/* 
* PROJECT TITLE: Battleships
* VERSION or DATE: Version 1.6, 21.05.24
* AUTHOR: Viraaj Ravji
* DETAILS:
    * Added KeyListener
    * Added rotation of ships (press r to rotate)
    * Error checking adding ship outside of bounds
    * Added new images for horizontal ships
    * Bug fixes
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
    private static JFrame f=new JFrame("Battleships"); //creates JFrame, the GUI window
    private static JButton[][] DGrid = new JButton[10][10];
    private static String[][] DGridData = new String[10][10]; 
    private static JButton[] ships = new JButton[5];
    private static int tileHeight = GUIHEIGHT/10; //tile spacing vertical
    private static int tileWidth = GUIWIDTH/20; //tile spacing horizontal
    private static int userSelection;
    private static int shipSelection;
    private static JButton rButton;
    private static JLabel shipLabel;
    private static String shipRotation = "vertical";

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


    public static void grid() { //creates the GUI and grid
        /*grid vairables*/
        String[] alphabetString = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
        JLabel[] labels = new JLabel[20];
       
        //adding buttons to GUI
        for (int y=0, yPos=0, xPos=GUITAB; y<10; y++) {
            for (int x=0; x<10; x++, xPos+=tileWidth)   {
                DGrid[x][y]=new JButton("-");
                //Colour and properties of button
                DGrid[x][y].setBackground(Color.white);
                DGrid[x][y].setOpaque(true);
                DGrid[x][y].setBorderPainted(false);
                DGrid[x][y].setBounds(xPos,yPos,tileWidth,tileHeight);
                f.add(DGrid[x][y]);   
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
                DGrid[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(MouseEvent evt) {
                        if (userSelection > 0) {
                            Color colour = Color.black;
                            if (shipRotation.equals("vertical")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newY+n > 9) {
                                        n = userSelection+1;
                                    } else {
                                        if (newY+userSelection > 9) {
                                            colour= Color.red;
                                        }
                                        DGrid[newX][newY+n].setBackground(colour);
                                    }   
                                }   
                            } 
                            if (shipRotation.equals("horizontal")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newX+n > 9) {
                                        n = userSelection+1;     
                                    } else {
                                        if (newX+userSelection > 9) {
                                            colour= Color.red;
                                        }
                                        DGrid[newX+n][newY].setBackground(colour);
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
                                        DGrid[newX][newY+n].setBackground(Color.white);
                                    }   
                                } 
                            }
                            if (shipRotation.equals("horizontal")) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newX+n > 9) {
                                        n = userSelection+1;   
                                    } else {
                                        DGrid[newX+n][newY].setBackground(Color.white);
                                    }   
                                } 
                            }
                        }  
                    }
                }); 
 
                DGrid[x][y].addActionListener(new ActionListener(){ 
                    public void actionPerformed(ActionEvent e){
                        if (userSelection > 0) {
                            if (e.getSource() == DGrid[newX][newY] && DGrid[newX][newY].getBackground() == Color.black) {
                                System.out.println("Button pressed at ("+(newX+1)+", "+(newY+1)+ ") with ship "+userSelection);
                                System.out.println(userSelection);
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
                                } else if (shipRotation.equals("horizontal")) {
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
                                        DGrid[newX][newY+n].setVisible(false);  
                                    }
                                    shipLabel.setBounds(DGrid[newX][newY].getX(), DGrid[newX][newY].getY(),tileWidth, tileHeight*(userSelection+1));
                                } else if (shipRotation.equals("horizontal")) {
                                    for (int n=0; n<userSelection+1; n++) {
                                        DGrid[newX+n][newY].setVisible(false);
                                    }
                                    shipLabel.setBounds(DGrid[newX][newY].getX(), DGrid[newX][newY].getY(),tileWidth*(userSelection+1), tileHeight);
                                }
                                
                                f.add(shipLabel);
                                ships[shipSelection].setVisible(false);
                                userSelection = 0;
                                resetButton();
                            }  else if (DGrid[newX][newY].getBackground() != Color.black) { // If ship is placed outside of bounds
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
        rButton = new JButton("Reset");
        rButton.setBounds(GUIWIDTH-tileWidth, GUIHEIGHT-tileWidth, tileWidth*2, tileHeight);
        rButton.setVisible(false);
        f.add(rButton);
        rButton.setVisible(true);
        rButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){ 
                for (int x=0; x<ships.length; x++) {
                    ships[x].setVisible(true);
                }              
                for (int y=0; y<10; y++) {
                    for (int x=0; x<10; x++) {
                        DGrid[x][y].setVisible(true);
                        DGrid[x][y].setBackground(Color.white);
                        DGrid[x][y].setText("-");
                        rButton.setVisible(false);
                    }
                }
                f.remove(shipLabel);
            }
        });
    }
    public Main() {
        //GUI window properties
        f.setSize(GUIWIDTH+GUITAB+(GUIWIDTH/20), GUIHEIGHT+GUITAB+GUITAB);  
        f.setLayout(null); 
        f.setVisible(true);
        f.setFocusTraversalKeysEnabled(false); 
        f.setFocusable(true);
        f.addKeyListener(this);
        for (int x=0; x<ships.length; x++) {
            ships[x].addKeyListener(this);
        }
        
    }
    //Implements of the KeyListener
    @Override
    public void keyTyped(KeyEvent e) { // Typed text
        //I dont need this method but I need to leave it for KeyListner anyways
    }

    @Override
    public void keyPressed(KeyEvent e) { // When key is pressed
        int keyCode = e.getKeyCode();
        if (keyCode == 82) { // Character 'r'
            System.out.println("r Key pressed");
            if (shipRotation.equals("vertical")) {
                shipRotation = "horizontal";
            } else {
                shipRotation = "vertical";
            } 
        }
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++) {
                DGrid[x][y].setBackground(Color.white); //reset grid 
            }
        }
        System.out.println(shipRotation);
    }

    @Override
    public void keyReleased(KeyEvent e) { // Typed text
        // I dont need this either
    } 

    public static void main(String[] args) {  //called when the program is run
        System.out.println("Window Width: "+GUIWIDTH);
        System.out.println("Window Height: "+GUIHEIGHT);
        grid(); //calls the grid method at the start of the program
        gridActivity();
        new Main();
    }
}  