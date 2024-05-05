/* 
* PROJECT TITLE: Battleships
* VERSION or DATE: Version 1, 05.05.24
* AUTHOR: Viraaj Ravji
* DETAILS:
    * This Version creates a simple GUI that can be scaled depending on the user's monitor size
    * Buttons are created but serve no function
    * The GUI is expanded so I can add a second grid without causing issues in the future
*/

/*LIBRARY*/
import javax.swing.*;    
import java.awt.*; 

public class Main { 
    //constants
    public static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int GUIWIDTH = (int)screenSize.getWidth() * 3 / 4; //width of the GUI
    public static final int GUIHEIGHT = (int)screenSize.getHeight() / 2; //height of the GUI
    public static final int GUITAB = 30; //this the tab where the title is written above the GUI screen

    public static void Grid() { //creates the GUI and grid
        /*grid vairables*/
        JFrame f=new JFrame("Battleships"); //creates JFrame, the GUI window
        JButton[][] DGrid = new JButton[10][10]; 
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
        //GUI window properties
        f.setSize(GUIWIDTH+GUITAB+(GUIWIDTH/20), GUIHEIGHT+GUITAB+GUITAB);  
        f.setLayout(null);  
        f.setVisible(true);
    }

    public static void main(String[] args) {  //called when the program is run
        Grid(); //calls the grid method at the start of the program
        System.out.println("Window Width: "+GUIWIDTH);
        System.out.println("Window Height: "+GUIHEIGHT);
    }  
}  