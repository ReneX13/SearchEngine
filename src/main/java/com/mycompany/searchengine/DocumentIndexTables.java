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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JTextArea;
import jdk.nashorn.internal.runtime.regexp.joni.Regex;
import spark.Request;
import spark.Response;
import spark.Route;
import static spark.Spark.get;

public class DocumentIndexTables implements Serializable{   
    
    protected class wordTableNode implements Serializable{
        public HashMap<String, documentNode> documentHashMap;
        
        wordTableNode(documentNode n){
            documentHashMap = new HashMap<>();
            documentHashMap.put(n.url.getPath(), n);
        }
        
        public int documentFrequency(){
            return documentHashMap.size();
        }
        public double Inverse_documentFrequency(){
            return Math.log((double)N_Documents()/((double)documentFrequency()))/Math.log(2);
        }
        
        public double Weight(){
            return ((double)documentFrequency())*Inverse_documentFrequency();
        }
    }    
    
    protected class documentTableNode implements Serializable{
        public int max_frequency;
        public MyDocument doc;
        
        documentTableNode(int m, MyDocument u) {
            max_frequency = m; 
            doc = u;
        }   
    }
    
    private class tmpTable_Node implements Serializable{
        public int frequency;
        public LinkedList<Integer> position;
        tmpTable_Node(int x) {
            frequency = 1;
            position = new LinkedList<>();
            position.add(x);
        }   
    }
    
    public Set<String> sortedWordTable_KEYS;
    public HashMap<String, wordTableNode> wordTable;
    public HashMap<String, documentTableNode> documentTable;
    public HashMap<Integer, URL> pageTable;
    
    public HashSet<String> StopWords;
    
    public String Message;
//*****************************************************************************
//*****************************************************************************    
//*****************************************************************************
    DocumentIndexTables(){
        wordTable = new HashMap<>();
        documentTable = new HashMap<>();
        pageTable = new HashMap<>();
        
        sortedWordTable_KEYS = new TreeSet<>();
        Message = " ";
    }
    
    public int N_Documents(){
        return documentTable.size();
    }
    
    public String printDocumentTableContents() throws FileNotFoundException{
        String str = "Document Table: \n"
                + "SIZE: "+documentTable.size()+"\n"
                + "*********************************************************** \n"
                + "*********************************************************** \n";
        for(String key: documentTable.keySet()){
            str += documentTable.get(key).doc.DocumentName()+"\n";
            str += "PAGE URL:  " +documentTable.get(key).doc.url_page.getPath()+"\n";
            str += "          ID:"+ documentTable.get(key).doc.id+"   Max_Frequency"
            + documentTable.get(key).max_frequency
                    +"   Length: "
            + documentTable.get(key).doc.DocumentLength()
                    +"\n\n";
            
        }
        return str;
    }
    public String printWordTableContents() throws FileNotFoundException{

        String str = "Word Table: \n"
                + "SIZE: "+wordTable.size()+"\n"
                + "*********************************************************** \n"
                + "*********************************************************** \n";
        for(String key: sortedWordTable_KEYS){
            str += key+": \n"
                    +"      df = "+wordTable.get(key).documentFrequency()
                    +"  idf = "+wordTable.get(key).Inverse_documentFrequency()
                    +"  weight = "+wordTable.get(key).Weight() + "\n";
        }
        return str;
    }
    public void setStopWords() throws FileNotFoundException, IOException{
        BufferedReader reader;
        Directory dic = new Directory();
        FileReader file =new FileReader( new File(dic.DOCUMENTS_DIRECTORY+File.separator+"Stop_Words.txt"));
        reader = new BufferedReader(file);
        String str;
        StopWords = new HashSet<>();
        while(true){
            str = reader.readLine();
            if(str == null)
                break;
            StopWords.add(str);
        }
    }
    
    public String getMessage(){
        return Message;
    }
    public void processDocuments(LinkedList<MyDocument> documentList) throws FileNotFoundException, IOException, InterruptedException, ClassNotFoundException{
        
        BufferedReader reader; 
        String str= " ";
        String [] tmpstr;
        
        boolean keyFound = false;
        Hashtable<String, tmpTable_Node> tmpTable;
        Set<String> keys;
        
        int max;
        
        int position = 0;
        for(MyDocument d: documentList){
            if(d.id%1000 == 0)
                    Message = "Indexing start... \n INDEXING FILE_NUMBER:" + d.id;
                    
            //System.out.println( "HERE 1"+ d.url.getFile());
            reader = new BufferedReader(new FileReader(d.url.getFile()));
            str = reader.readLine();
            tmpTable = new Hashtable<>();
            max = 0;
            
            //Process MyDocument
            position = 0;
            while(str != null){
                
                tmpstr = str.replaceAll("[\\W]", " ").trim().split("\\s+");
                
                for(String s: tmpstr){
                    if(tmpTable.containsKey(s)){
                        tmpTable.get(s).frequency++;
                        tmpTable.get(s).position.add(position);
                    }
                    else
                        tmpTable.put(s, new tmpTable_Node(position));
                    
                    position++;
                }
                str = reader.readLine();
                
            }
            
            //Add To Table
            setStopWords();
            keys = tmpTable.keySet();
            for(String k: keys){
                if(StopWords.contains(k) || k.equals("")){}
                else{
                    if(wordTable.containsKey(k))
                        wordTable.get(k).documentHashMap.put(d.url.getPath(), new documentNode(d.url, tmpTable.get(k).frequency, tmpTable.get(k).position));
                    else
                        wordTable.put(k, new wordTableNode(new documentNode(d.url, tmpTable.get(k).frequency, tmpTable.get(k).position )));

                    if(tmpTable.get(k).frequency > max)
                        max = tmpTable.get(k).frequency;
                    
                   
                }
            }

            documentTable.put(d.url.getPath(), new documentTableNode(max, d));
            reader.close();
            
            File tmp = new File(d.url.getFile());
            tmp.delete();
        }
        
        keys = wordTable.keySet();
        for(String key:keys)
            sortedWordTable_KEYS.add(key);
      
       System.out.println("Inexing Done!");
      
    } 
}
