/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.searchengine;

import java.io.Serializable;
import java.net.URL;
import java.util.LinkedList;

/**
 *
 * @author Rene
 */

    public class documentNode implements Comparable<documentNode>, Serializable{
        public int frequency;
        public LinkedList<Integer> position;
        public URL url;
        
        documentNode(URL u, int f, LinkedList<Integer> p){
            url = u;
            frequency = f;
            position = p;
        }
        
        public double normFrequency(DocumentIndexTables d){
            return (double)frequency/(double)d.documentTable.get(url.getPath()).max_frequency;
        }
        
        
            
        @Override
        public boolean equals(Object o) {
            if(this == o)
                return true;
            else
                return false;
        }
        
        @Override
        public int compareTo(documentNode o) {
            return new String(url.getFile()).compareTo(new String(o.url.getFile()));
        }       
    }
