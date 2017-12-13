/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.searchengine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class HTML_Reader {
    private FileReader fileReader;
    private BufferedReader bReader;
    private File htmlFile;
    private StringBuilder html;
    public String html_str;
    HTML_Reader(File f) throws IOException{
        htmlFile = f;
        html = new StringBuilder();
        fileReader = new FileReader(htmlFile);
        bReader= new BufferedReader(fileReader);
        
        if(f.exists())
            System.out.println("YES IT DOES!");
        String str = bReader.readLine();
        while(str != null){
            html.append(str);
            str = bReader.readLine();
            
        }
        html_str = html.toString();
    }

}
