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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
//import javax.lang.model.util.Elements;
import javax.swing.JTextArea;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.get;


public class DocumentsBuilder extends Directory implements Serializable{
    public LinkedList<MyDocument> DocumentList;
    
    public LinkedList<String> List_ofURLstrings;
    public HashSet<String>List_ofVistedURLs;
    public int fileNumber;
    public String Message;
    DocumentsBuilder() throws IOException{
        Message = " ";
        DocumentList = new LinkedList<>();
        List_ofURLstrings = new LinkedList<>();
        List_ofVistedURLs = new HashSet<>();
        File FOLDER = new File(RHF_DIRECTORY);
        File[] files = FOLDER.listFiles();
        fileNumber = 1;
        //loading = new LOADING_ANIMATION_THREAD();
    }
    //*************************************************************************
    //*************************************************************************
    
    
public void displayVisitedURLS(){
    for(String s: List_ofVistedURLs)
        System.out.println(s);
    System.out.println(List_ofURLstrings.size());
}

public boolean checkIfSaved_RHF(File f) throws FileNotFoundException, MalformedURLException, IOException, ClassNotFoundException{
        if(!f.exists())
            return false;
        
        //loading.Message = "Loading from Saved Data";
        FileInputStream inputFile = new FileInputStream(f);
        ObjectInputStream inputObject = new ObjectInputStream(inputFile);
        
        DocumentList = (LinkedList<MyDocument>) inputObject.readObject();                  
         return true;
    }


public void RHFSetup() throws IOException, FileNotFoundException, MalformedURLException, ClassNotFoundException{
    
    File tempFile;
    //PrintWriter out;
    
    ObjectOutputStream outputObject;
    FileOutputStream outputFile;
    
    //CHECKS IF DATA IS SAVED THEN LOAD IT AND RETURN THE FUNCTION
    if(checkIfSaved_RHF(new File(RHF_TMP + "saved.txt"))){
            System.out.println("LEFT!  "+DocumentList.getFirst().id + 
                    "   /n "+DocumentList.getLast().url_page);
            return;
    }
    
    
    //THIS IS THE FUNCTION THAT STARTS THE CRAWLING YOU NEED TO GIVE IT THE INDEX FILE
    RHFSetup(new File(RHF_DIRECTORY+"/"+"index.html"));
    
    
    
    //SAVE THE DATA 
   /* tempFile = new File(RHF_TMP
                    + "saved.txt");
    
    outputFile = new FileOutputStream(tempFile);
    outputObject = new ObjectOutputStream(outputFile);
    outputObject.writeObject(DocumentList);
    outputObject.flush();
    outputObject.close();
    outputFile.close();
    
    out = new PrintWriter(tempFile);
    for(MyDocument doc: DocumentList){
        out.println(doc.url.toString());
        out.println(doc.url_page.toString());
        out.println(doc.id);
    }
    out.println("#");


    for(String s: List_ofURLstrings){
        out.println(s);
    }
    out.close();*/
}
public String getMessage() {
    return Message;
}
private void RHFSetup(File CURRENT_FILE) throws IOException{     
    
        //STEP 1: ADD THE CURRENT_FILE's PATH TO THE LIST HOLDIND THE VISITED URLS
        List_ofVistedURLs.add(CURRENT_FILE.getAbsolutePath());
        
        //THIS LIST HOLDS ALL THE URL's FOUND IN THE CURREN_FILE(HTML FILE)
        HashSet<String> tmpURLList = new HashSet<>();
        
        //THIS SHOWS THAT THE PROGRAMING IS RUNNING AND ID OF THE FILE BEING PROCCESSED
                //FILENUMBER STARTS AT 1
                
        //loading.Message = "Crawling \n";
        
        Message = " Crawling...\n PROCESSING FILE_NUMBER:" + fileNumber;
                    //System.out.println(CURRENT_FILE.getParent()+File.separator+CURRENT_FILE.getName());
        
        //loading.Message += "\n PROCESS... FILE_NUMBER:" + fileNumber;
        //THIS TEMP FILE IS USED TO HOLD ALL TEXT CONTENT OF THE CURRENT_FILE
        File tempFile;
        
        //THIS DISPLAYS THE TEXT TO TEMPFILE
        PrintWriter out;
       
        //IF THE TEMPFILE EXISTS THEn DELETE IT AND RE-CREATE IT
        tempFile = new File(RHF_TMP + File.separator+ "Document_" + fileNumber + ".txt");
        //System.out.println("HERE HERE: "+tempFile.getAbsolutePath());
        if(tempFile.exists())
            tempFile.delete();
        tempFile.createNewFile();
        
        //GIVE THE TEMPFILE TO OUT
        out = new PrintWriter(tempFile);
        
        //THIS IS JSOUP WHICH IS WHAT WE USED TO PARSE THE CURRENT_FILE
        Document doc = Jsoup.parse(CURRENT_FILE, null);
        
        //THIS COLLECTS ALL URL HYPERLINKS
        Elements links = doc.select("a[href]");
               
        //THIS ADDS HYPERLINKS TO TMPURLLIST
            //FOR EACH ELEMENT IN LINKS ADD THE HYPERLINK TO TMPURLLIST
        for (Element link : links) 
            tmpURLList.add(link.attr("href"));
        
        //OUTPUT EVERYTHING TO THE TMPFILE
        out.append(doc.text());
        out.close();
        
        //THIS ADDS THE DATA COLLECTED ABOUT THE CURRENT_FLE IN DOCUMENT_LIST
        //USING MYDOCUMENT AS THE NODE. //LOOK AT MYDOCUMENT.JAVA
        DocumentList.add(new MyDocument(tempFile, fileNumber, CURRENT_FILE.toPath().toUri().toURL()));
        
        //INCREASE FILE NUMBER BY 1 FOR THE NEXT FILE TO BE PROCCESSED
        fileNumber++;
        
        //THES VARIABLES ARE USED IS URLS CONTAINS ../
        File TFILE;
        Pattern pattern = Pattern.compile("(\\.)(\\.)(\\/)");
        Matcher m;  
        String str;
        
        //FOR ALL URLS(S2) IN TMPURLIST
        for(String s2:tmpURLList){
            //GIVE THE URL TO PATTERN MATCHER
            m = pattern.matcher(s2); 
            
            //TFILE EQUALS CURRENT_FILE
            TFILE = CURRENT_FILE;
            
            //FOR ALL PATTERN(../) FOUND IN THE URL
            while(m.find()){
                //CHECK IF THE TFILE's PARENT DIRECTORY IS NULL, IF NOT THEN...
                if(TFILE.getParent() == null)
                        break;
                //TFILE EQUALS IT'S PARENT DIRECTORY
                TFILE = new File(TFILE.getParent());
            }
            
            //GET THE NEW PROPER DIRECTORY; TFILE IS NOW THE PROPER DIRECTORY FOR THE URL(S2)
            TFILE = new File(TFILE.getParent());
            
            //REMOVE ALL ../ FROM THE URL(S2)
            str=s2.replaceAll("(\\.)(\\.)(\\/)", "");
            
            //GIVE TFILE THE REST OF THE PATH TO THE NEXT HTML FILE
            TFILE= new File(TFILE.getPath()+File.separator+str);

            //PROCCESS THE HTML GIVEN TO TFILE  IF, TFILE EXISTS AND
            //IF IT DOES NOT BELONG TO THE LIST OF VISTED URLS
            if((TFILE.exists()) && !List_ofVistedURLs.contains(TFILE.getAbsolutePath()))              
                RHFSetup(TFILE);

        }
    }



}
