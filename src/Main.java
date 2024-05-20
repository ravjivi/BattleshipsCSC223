/* 
* PROJECT TITLE: Battleships
* VERSION or DATE: Version 1.4, 20.05.24
* AUTHOR: Viraaj Ravji
* DETAILS:
    * You can click on the grid to place ships while ship is selected
    * After placing atleast 1 ship you can reset them with the reset button
    * You cannot rotate ships yet
    * Placing ships with remove the button on the left, and place it on the grid
*/

/*LIBRARY*/
import javax.swing.*;    
import java.awt.*; 
import java.awt.event.*;

public class Main implements ActionListener { 
    /*Constants*/
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //Takes the height of the screen and calculates size of the GUI
    private static final int GUIHEIGHT = (int)screenSize.getHeight() / 2; 
    private static final int GUIWIDTH = GUIHEIGHT*5/2; //width of the GUI
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

    /*IMAGES*/
    private static ImageIcon shipImageH2 = new ImageIcon("assets/ship_texture_h2.jpg");
    private static Image scaleshipImageH2 = shipImageH2.getImage().getScaledInstance(tileWidth, tileHeight*2,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH3 = new ImageIcon("assets/ship_texture_h3.jpg");
    private static Image scaleshipImageH3 = shipImageH3.getImage().getScaledInstance(tileWidth, tileHeight*3,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH4 = new ImageIcon("assets/ship_texture_h4.jpg");
    private static Image scaleshipImageH4 = shipImageH4.getImage().getScaledInstance(tileWidth, tileHeight*4,Image.SCALE_DEFAULT);
    private static ImageIcon shipImageH5 = new ImageIcon("assets/ship_texture_h5.jpg");
    private static Image scaleshipImageH5 = shipImageH5.getImage().getScaledInstance(tileWidth, tileHeight*5,Image.SCALE_DEFAULT);


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
        Main ship0 = new Main(0, tileWidth, tileHeight);
        Main ship1 = new Main(1, tileWidth, tileHeight);
        Main ship2 = new Main(2, tileWidth, tileHeight);
        Main ship3 = new Main(3, tileWidth, tileHeight);
        Main ship4 = new Main(4, tileWidth, tileHeight);
        f.add(ships[0]);
        f.add(ships[1]);
        f.add(ships[2]);
        f.add(ships[3]);
        f.add(ships[4]);

        //GUI window properties
        f.setSize(GUIWIDTH+GUITAB+(GUIWIDTH/20), GUIHEIGHT+GUITAB+GUITAB);  
        f.setLayout(null); 
        f.setVisible(true);
    }

    public Main(int n, int tileWidth, int tileHeight) {
        switch (n) {
            case 0:
                ships[n] = new JButton(new ImageIcon(scaleshipImageH2));
                ships[n].setBounds(tileWidth*12,tileHeight,tileWidth,tileHeight*2);
                break;
            
            case 1:
                ships[n] = new JButton(new ImageIcon(scaleshipImageH3));
                ships[n].setBounds(tileWidth*15,tileHeight,tileWidth,tileHeight*3);
                break;

            case 2:
                ships[n] = new JButton(new ImageIcon(scaleshipImageH3));
                ships[n].setBounds(tileWidth*18,tileHeight,tileWidth,tileHeight*3);
                break;
            case 3:
                ships[n] = new JButton(new ImageIcon(scaleshipImageH4));
                ships[n].setBounds(tileWidth*27/2,tileHeight*5,tileWidth,tileHeight*4);
                break;
            case 4:
                ships[n] = new JButton(new ImageIcon(scaleshipImageH5));
                ships[n].setBounds(tileWidth*33/2,tileHeight*5,tileWidth,tileHeight*5);
                break;
            default:
                System.out.println ("Error: h");
                break;
        }
        ships[n].addActionListener(this); //adds click detection
    }

    public static void gridActivity() { //grid inputs
        for (int y=0; y<10; y++) {
            for (int x=0; x<10; x++) {
                int newX = x; //Cant use the x from the for loop inside a local method
                int newY = y;
                for (int z=0; z<5; z++) {
                    DGrid[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseEntered(MouseEvent evt) {
                            if (userSelection > 0) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newY+n > 9) {
                                        n = userSelection+1;     
                                    } else {
                                        DGrid[newX][newY+n].setBackground(Color.black);
                                    }   
                                }  
                            }     
                        }
                        public void mouseExited(MouseEvent evt) {
                            if (userSelection > 0) {
                                for (int n=0; n<userSelection+1; n++) {
                                    if (newY+n > 9) {
                                        n = userSelection+1;   
                                    } else {
                                        DGrid[newX][newY+n].setBackground(Color.white);
                                    }   
                                }    
                            }  
                        }
                    });
                } 
                DGrid[x][y].addActionListener(new ActionListener(){ 

                    public void actionPerformed(ActionEvent e){
                        if (userSelection > 0) {
                            if (e.getSource() == DGrid[newX][newY]) {
                                System.out.println("Button pressed at ("+(newX+1)+", "+(newY+1)+ ") with ship "+userSelection);
                                System.out.println(userSelection);
                                for (int n=0; n<userSelection+1; n++) {
                                    DGrid[newX][newY+n].setVisible(false);
                                }
                                Image icon = scaleshipImageH2;
                                if (userSelection == 1) {
                                    icon = scaleshipImageH2;
                                } else if (userSelection == 2) {
                                    icon = scaleshipImageH3;
                                } else if (userSelection == 3) {
                                    icon = scaleshipImageH4;
                                } else if (userSelection == 4) {
                                    icon = scaleshipImageH5;
                                } 
                                
                                shipLabel = new JLabel();
                                shipLabel = new JLabel(new ImageIcon(icon));
                                shipLabel.setBounds(DGrid[newX][newY].getX(), DGrid[newX][newY].getY(),tileWidth, tileHeight*(userSelection+1));
                                f.add(shipLabel);
                                ships[shipSelection].setVisible(false);
                                userSelection = 0;
                                resetButton();
                            }  
                        } 
                    }  
                }); 
            }
        }
    }
    
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
    
    public static void resetButton() {
        rButton = new JButton("Reset");
        rButton.setBounds(GUIWIDTH-tileWidth, GUIHEIGHT-tileWidth, tileWidth*2, tileHeight);
        rButton.setVisible(false);
        f.add(rButton);
        rButton.setVisible(true);
        rButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){ 
                ships[0].setVisible(true);
                ships[1].setVisible(true);
                ships[2].setVisible(true);
                ships[3].setVisible(true);
                ships[4].setVisible(true);
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

    public static void main(String[] args) {  //called when the program is run
        System.out.println("Window Width: "+GUIWIDTH);
        System.out.println("Window Height: "+GUIHEIGHT);
        grid(); //calls the grid method at the start of the program
        gridActivity();
    }  
}  