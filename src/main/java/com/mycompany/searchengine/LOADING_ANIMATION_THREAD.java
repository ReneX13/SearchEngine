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
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
//*****************************************************************************
//*****************************************************************************
//This class is used to show if some process is still in progress. For this 
//is used to show that we are loading data. It displays a name of what is being 
//processed and a LOADING label with minor animations. Two construcotrs, a 
//default one and the second takes a name and a color.

//Once the second constructor is called the thread runs while LOADING is true.
    //1. isLoading() can be used to check if LOADING is still true.
    //2. Once the doneLoading is called, LOADING is set to false.

//HOW TO USE:
    //Assume some random function,
/*
        public void someFunction(){
            LOADING_ANIMATION_THREAD tmp = 
                new LOADING_ANIMATION_THREAD("some string", some color);

                [WHATEVER CODE]

            tmp.isLoading()<--- this is boolean function that returns LOADING  

            tmp.doneLoading()<---- once you get to the end of the function
                                   set LOADING to false.
        }
*/
//*****************************************************************************
//*****************************************************************************
public class LOADING_ANIMATION_THREAD extends Rectangle implements Runnable, Serializable{
   // private Panel panel;
    private int LOADING_COUNTER;
    private boolean LOADING;
    private String name;
    private Color name_Color;
    public Thread starter;
    public String Message;
    LOADING_ANIMATION_THREAD(){
        name = "none";
        name_Color = Color.BLACK;
        LOADING_COUNTER = 0;
        LOADING = false;
        Message = " ";
        starter = new Thread(this, "Loading_Thread");
        starter.start();
    }
    
    LOADING_ANIMATION_THREAD(String n, Color c){
        //panel = p;
        name = n;
        name_Color = c;
        LOADING_COUNTER = 0;
        LOADING = true;
        Message = " ";
        starter = new Thread(this, "Loading_Thread");
        starter.start();
    }

    
    private void drawString(Graphics g, String text, int x, int y) {
        for (String line : Message.split("\n"))
                 g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
   
    public void draw(Graphics g){
            g.setColor(name_Color);
            g.setFont(g.getFont().deriveFont(0, 20.0f));
            g.drawString(name , 1000,680);
            drawString(g, Message, 200, 200);
            
            
            g.setColor(Color.black);
            g.setFont(g.getFont().deriveFont(0, 50.0f));
            
            
            
            switch (LOADING_COUNTER){
                case 1:
                g.drawString("LOADING." , 1000,730);
                break;
                case 2:
                g.drawString("LOADING.." , 1000,730);    
                break;
                case 3:
                g.drawString("LOADING..." , 1000,730);
                break;
                default:
                g.drawString("LOADING" , 1000,730);
            }
            
            
            
    }
    public void update(){
        if(LOADING_COUNTER >3)
                LOADING_COUNTER = 0;
        
        LOADING_COUNTER++;
    }
    public boolean isLoading(){
        return LOADING;
    }
    public void doneLoading(){
        LOADING = false;
    }
    
    @Override
    public void run() {
     
        while(LOADING){
          try {
                Thread.sleep(800);
                
            } catch (InterruptedException ex) {
                Logger.getLogger(Panel.class.getName()).log(Level.SEVERE, null, ex);
            }
          
            update();
               // panel.repaint();
        }
        
    }     
}
