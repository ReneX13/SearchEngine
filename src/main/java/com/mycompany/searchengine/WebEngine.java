/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.searchengine;
import static spark.Spark.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


import spark.*;
//import spark.template.freemarker.FreeMarkerEngine;
import spark.Request;
import spark.Response;
import spark.Route;



public class WebEngine {
    public String homepage;
    public String urllistpage;
    public MyParser parser;
    WebEngine(){
        
    }
    
    public void setParser(MyParser p){
        parser = p;
    }
    
    public void renderUrlList(){
        int i = 0;
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n" +
        "<html>\n" +
        "<body>\n" +
        "<form action=\"/entry\" method=\"POST\">"
        + "  QUERY:<br>\n"  
        + "  <input type=\"text\" name=\"query\">\n" +
        "  <br> \n");
        for(MyDocument doc: parser.documents.DocumentList){
            html.append("<a href="+ doc.url_page.getPath() + ">"+ doc.url_page +"<a> \n <br> ");
            
            
            if(i >10)
                break;
            i++;
        }
        html.append("</form>\n" +
        "\n" +
        "</body>\n" +
        "</html>");
        
        urllistpage = html.toString();
    }
}
