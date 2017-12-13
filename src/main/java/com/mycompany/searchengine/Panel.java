//*****************************************************************************
//*****************************************************************************
/*
    *AUTHOR: Rene Reyes
    *DATE: 6/14/2016
*/
//*****************************************************************************
//*****************************************************************************
package com.mycompany.searchengine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import static jdk.nashorn.internal.runtime.PropertyDescriptor.SET;


public class Panel extends JPanel implements Runnable, ActionListener, Serializable{
    //Creates List of Documents for a text file. Used to manage Documents
    private DocumentsBuilder documents; 
        //Calling defaultSetup uses, TomSawyer.txt, and stores everything in
        // "Documents" folder.
    
    //Parser: 
    public MyParser parser; 
        //1. Constructor takes a textArea, and query to work with. 
        //2. FUNCTION processDocument, takes a list of documents and stores 
            //data in tables;
        //3. Using TextField and TextArea given to the parser, it process the 
            //query entered in the TextField and outputs results to the textArea.
    
    
    
    //*************************************
    //COMPONENTS USED 
    private Thread starter;
    private Image background_1;
    public JTextField query;
    public JTextArea textArea;
    public GridBagConstraints gbc;
    public GridBagLayout gbl;
    public JScrollPane scroll;
    public boolean flag;
    public Directory dic;
    
    public boolean flag1;
    public boolean flag2;
    public LOADING_ANIMATION_THREAD loading;
   // public TerminalPanel terminal;
    //*************************************
        
    Panel(LOADING_ANIMATION_THREAD l) throws FileNotFoundException, IOException, InterruptedException{
        loading = l;
        dic = new Directory();
        
        flag1 = flag2 = false;
        //Setup all Components
        setupComponents();    

        //Create thread
        starter = new Thread(this, "Panel_Thread");
        //run this panel
        starter.start();   
        
    }
    public void setloading(LOADING_ANIMATION_THREAD l, boolean f1, boolean f2)
    {
        flag1 = f1;
        flag2 = f2;
        loading = l;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(background_1, 0, 0, null);
        
        //*************************************
        //Display if programming to LOADING data
        //*************************************
        if(loading != null){
            if(loading.isLoading()){
                if(flag1){
                    loading.Message = parser.documents.getMessage();
                      
                }
                if(flag2){
                    loading.Message = parser.indexAndtables.getMessage();
                }
            }
            loading.draw(g);
        }
        //*************************************
    }
 
    public void update(){
        
    }
    
    
    @Override
    public void run() {
         
        while(true){
            
            update();
            this.repaint();
        }
        
    }     
    
    //**************************************************************************
    //**************************************************************************
    //SETUP FUNCTIONS
    //**************************************************************************
    //
    //MAKES SURE TO WAIT UNTIL IT FINISHES LOADING ALL DOCUMENTS!
    public void setup(MyParser p){
            parser = p;
    }
    
    //**************************************************************************
    //GRAPHICS SETUP
    //**************************************************************************
    private void setupComponents(){
        background_1 = new ImageIcon(dic.PROJECT_DIRECTORY+File.separator+"Images"
                +File.separator+"BACKGROUND_1.JPG").getImage();
     
    }    

    @Override
    public void actionPerformed(ActionEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

