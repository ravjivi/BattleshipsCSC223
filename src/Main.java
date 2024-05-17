/* 
* PROJECT TITLE: Battleships
* VERSION or DATE: Version 1.2, 17.05.24
* AUTHOR: Viraaj Ravji
* DETAILS:
    * Changes to images and scaling
    * Buttons register clicks but don't function
    * Converted battleships to a object
*/

/*LIBRARY*/
import javax.swing.*;    
import java.awt.*; 
import java.awt.event.*;

public class Main implements ActionListener { 
    //constants
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    //Takes the height of the screen and calculates size of the GUI
    private static final int GUIHEIGHT = (int)screenSize.getHeight() / 2; 
    private static final int GUIWIDTH = GUIHEIGHT*5/2; //width of the GUI
    private static final int GUITAB = 30; //this the tab where the title is written above the GUI screen

    private static JButton[][] DGrid = new JButton[10][10]; 
    private static JButton[] ships = new JButton[5];
    Object userSelection;

    public static void Grid() { //creates the GUI and grid
        /*grid vairables*/
        JFrame f=new JFrame("Battleships"); //creates JFrame, the GUI window
        int tileHeight = GUIHEIGHT/10; //tile spacing vertical
        int tileWidth = GUIWIDTH/20; //tile spacing horizontal
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
                DGrid[x][y].addActionListener(new ActionListener(){  
                    public void actionPerformed(ActionEvent e){  
                        actionPerformed(e);
                    }  
                }); 

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
        Main ship0 = new Main(0, tileWidth, tileHeight, ships);
        Main ship1 = new Main(1, tileWidth, tileHeight, ships);
        Main ship2 = new Main(2, tileWidth, tileHeight, ships);
        Main ship3 = new Main(3, tileWidth, tileHeight, ships);
        Main ship4 = new Main(4, tileWidth, tileHeight, ships);

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

    public Main(int n, int tileWidth, int tileHeight, JButton ships[]) {
        switch (n) {
            case 0:
                ImageIcon shipImageH2 = new ImageIcon("assets/ship_texture_h2.jpg");
                Image scaleshipImageH2 = shipImageH2.getImage().getScaledInstance(tileWidth, tileHeight*2,Image.SCALE_DEFAULT);
                ships[n] = new JButton(new ImageIcon(scaleshipImageH2));
                ships[n].setBounds(tileWidth*12,tileHeight,tileWidth,tileHeight*2);
                break;
            
            case 1:
                ImageIcon shipImageH3 = new ImageIcon("assets/ship_texture_h3.jpg");
                Image scaleshipImageH3 = shipImageH3.getImage().getScaledInstance(tileWidth, tileHeight*3,Image.SCALE_DEFAULT);
                ships[n] = new JButton(new ImageIcon(scaleshipImageH3));
                ships[n].setBounds(tileWidth*15,tileHeight,tileWidth,tileHeight*3);
                break;

            case 2:
                shipImageH3 = new ImageIcon("assets/ship_texture_h3.jpg");
                scaleshipImageH3 = shipImageH3.getImage().getScaledInstance(tileWidth, tileHeight*3,Image.SCALE_DEFAULT);
                ships[n] = new JButton(new ImageIcon(scaleshipImageH3));
                ships[n].setBounds(tileWidth*18,tileHeight,tileWidth,tileHeight*3);
                break;
            case 3:
                ImageIcon shipImageH4 = new ImageIcon("assets/ship_texture_h4.jpg");
                Image scaleshipImageH4 = shipImageH4.getImage().getScaledInstance(tileWidth, tileHeight*4,Image.SCALE_DEFAULT);
                ships[n] = new JButton(new ImageIcon(scaleshipImageH4));
                ships[n].setBounds(tileWidth*27/2,tileHeight*5,tileWidth,tileHeight*4);
                break;
            case 4:
                ImageIcon shipImageH5 = new ImageIcon("assets/ship_texture_h5.jpg");
                Image scaleshipImageH5 = shipImageH5.getImage().getScaledInstance(tileWidth, tileHeight*5,Image.SCALE_DEFAULT);
                ships[n] = new JButton(new ImageIcon(scaleshipImageH5));
                ships[n].setBounds(tileWidth*33/2,tileHeight*5,tileWidth,tileHeight*5);
                break;
            
            default:
                System.out.println ("Error: h");
                break;
        }
        ships[n].addActionListener(this);
    }

    public void actionPerformed(ActionEvent e){
        Object src = e.getSource(); 
        userSelection = src;
        for (int x=0; x<5; x++) {
            if (userSelection == ships[x]) {
                System.out.println("User has selected Ship "+(x+1));
            }
        }
        
    }  

    public static void main(String[] args) {  //called when the program is run
        Grid(); //calls the grid method at the start of the program
        System.out.println("Window Width: "+GUIWIDTH);
        System.out.println("Window Height: "+GUIHEIGHT);
    }  
}  