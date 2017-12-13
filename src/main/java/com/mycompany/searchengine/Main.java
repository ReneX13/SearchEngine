package com.mycompany.searchengine;
import java.awt.Color;
import java.awt.Dimension;
import static spark.Spark.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;


import spark.*;
//import spark.template.freemarker.FreeMarkerEngine;
import spark.Request;
import spark.Response;
import spark.Route;



public class Main{
    public static MyParser parser;
    public static Panel panel;
    public static JFrame frame;
    public static LOADING_ANIMATION_THREAD loading;
    public static void SETUP_LOADING() throws IOException, FileNotFoundException, InterruptedException{
        int w = 1280;
        int h = 768;
        frame = new JFrame();
        panel = new Panel(loading);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        
        panel.setPreferredSize(new Dimension(w,h));
        panel.setup(parser);
        frame.getContentPane().add(panel);
        frame.pack();
        
        frame.setVisible(true);
        frame.getContentPane().revalidate();
    }
    
    public static void main(String[] args) throws IOException, InterruptedException, FileNotFoundException, MalformedURLException, ClassNotFoundException {
        parser = new MyParser();
        SETUP_LOADING();
        Directory dic = new Directory();
        
        File parserFile = new File(dic.DOCUMENTS_DIRECTORY+File.separator+"Indexing_Saved_Data.txt");
        
        //Delete File if you wish to let the programming set it up again
        if(parserFile.exists()){
            loading = new LOADING_ANIMATION_THREAD("Loading From File", Color.GREEN);
            panel.setloading(loading, false, false);
           // loading.Message = "Loading Saved File!!!";
            FileInputStream inputFile = new FileInputStream(parserFile);
            ObjectInputStream inputObject = new ObjectInputStream(inputFile);
        
            parser.indexAndtables = (DocumentIndexTables) inputObject.readObject();   
            loading.doneLoading();
        }
        
        else{
            System.out.println("Setting Up Parser!!!");
            File testFile = new File("C:/RHF/index.html");
            
            
            loading = new LOADING_ANIMATION_THREAD("Building Document for Indexing", Color.RED);
            panel.setloading(loading, true, false);
            parser.documents.RHFSetup();
            loading.doneLoading();
                    
            loading = new LOADING_ANIMATION_THREAD("Indexing Documents", Color.YELLOW);
            panel.setloading(loading, false, true);
            parser.indexAndtables.processDocuments(parser.documents.DocumentList);
            loading.doneLoading();
            
            loading = new LOADING_ANIMATION_THREAD("Saving Index", Color.GREEN);
            panel.setloading(loading, false, false);
                FileOutputStream outputFile = new FileOutputStream(parserFile);
                ObjectOutputStream outputObject = new ObjectOutputStream(outputFile);

                outputObject.writeObject(parser.indexAndtables);
                outputObject.flush();
                outputObject.close();
                outputFile.close();
            loading.doneLoading();
            WebEngine eng = new WebEngine();
            eng.setParser(parser);
            eng.renderUrlList();
        }
        frame.dispose();
       
       String SESSION_NAME = "QUERY";
       int SESSION_NUMBER = 0;
       get("/", (Request request, Response response) -> {
                String query = request.session().attribute(SESSION_NAME);
                
                if (query == null) {
                    return "<html><body>Enter Query: <form action=\"/entry\" method=\"POST\"><input type=\"text\" name=\"query\"/><input type=\"submit\" value=\"go\"/></form></body></html>";
                } else {
                    if(!query.equals("./more")){
                        parser.process(query, null);
                        
                        //parser.Displaying_Results = true;
                    }
                    //Enter Query: <form action=\"/entry\" method=\"POST\"><input type=\"text\" name=\"query\"/><input type=\"submit\" value=\"go\"/></form> %s 
                    //, parser.Displaying()
                    //System.out.println(parser.Displaying());
                    return String.format("<html><body>   Enter Query: <form action=\"/entry\" method=\"POST\"><input type=\"text\" name=\"query\"/><input type=\"submit\" value=\"go\"/></form> %s </body></html>", parser.Displaying());
                }
            });

        post("/entry", (request, response) -> {
            String query = request.queryParams("query");
            if (query != null) {
                request.session().attribute(SESSION_NAME, query);
            }
            response.redirect("/");
            return null;
        }); 
        
        //parser.process("abandoned", null);
        //System.out.println(parser.Displaying());
        
        System.out.println("HERE IS THE END OF THE PROGRAM!!!!!");
   
    
    }
 
}