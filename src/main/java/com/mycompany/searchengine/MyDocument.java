//*****************************************************************************
//*****************************************************************************
/*
    *AUTHOR: Rene Reyes
    *DATE: 6/14/2016
*/
//*****************************************************************************
//*****************************************************************************
package com.mycompany.searchengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import javax.swing.JTextArea;

//*****************************************************************************
//*****************************************************************************
//GENERAL DOCUMENT CLASS:
    //CONTAINS:
        //1. File representing the document (doc)
        //2. int representing the identification of the document (id)
//*****************************************************************************
//*****************************************************************************
public class MyDocument implements Serializable{
    public URL url;//THIS URL CONTAINS THE FILE THAT CONTAINS ALL TEXT CONTENT.
    int id;//UNIQUE ID, THIS URL ON TO HAS THE ID IN IT's FILE NAME.
    public URL url_page;//THIS IS THE URL OF THE HTML FILE 
    
    
    MyDocument(File F, int ID) throws MalformedURLException{
        id = ID;
        url = F.toPath().toUri().toURL();
    }
    
    //*************************************************************
    //THIS IS THE CONTRUCTOR USED FOR PROJECT C
    //*************************************************************
    //*************************************************************
    MyDocument(File F, int ID, URL u) throws MalformedURLException{
        id = ID;
        url = F.toPath().toUri().toURL();
        url_page = u;
    }
    //*************************************************************
    //*************************************************************
    
    MyDocument(String U, int ID) throws MalformedURLException{
        id = ID;      
        url = new File(U).toPath().toUri().toURL();
    }
    
    //Return String, the name of the file
    public String DocumentName() throws FileNotFoundException{
        return url.getFile();
    }
    public double DocumentLength(){
        File file = new File(url.getPath());
        return (double)file.length();
    }
    //Return Document URL (NOT TESTED)!
    public URL DocumentURL() throws MalformedURLException{
        return url;
    }
    
    //Displays file contents on the terminal (some JTextArea)
    public void printToTerminal(JTextArea textarea) throws FileNotFoundException, IOException{
        BufferedReader reader = new BufferedReader(new FileReader(url.getPath()));
        String str;
        
        while(true){
            str = reader.readLine();
            
            if(str == null)
                break;
            
            textarea.append("       "+str + "\n");
        }
        textarea.append("\n \n");
    }
}
