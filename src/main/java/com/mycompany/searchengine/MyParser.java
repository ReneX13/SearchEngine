//*****************************************************************************
//*****************************************************************************
/*
    *AUTHOR: Rene Reyes
    *DATE: 6/14/2016
*/
//*****************************************************************************
//*****************************************************************************
/*
Documentation: 
    1. operator and operand or class derived from parser_node. parser_node
        is used to create Stacks(LinkedLists) to perform algebraic operations,
        parser_node also has a enum type {operator, operand} to distinguish 
        what it is.
    
    2. Differences between expressions(postfix or infix) & query.
        
        spaces: In the query spaces between each word, the space 
                between ')' <-> '(', or operand <-> '('  operand <-> ')',
                are considered OR operations thus in an expression it would be
                a '+'.
                Example: A B (C)(D) expression->A+B+C+D
                
        +: A plus sign in the query is an AND operations, thus in the expression
                it will be represented by a '*'.
                Example: A+B expression-> A*B

        " ": The quotations are used to check if the phrase exsists in a document.
                Example: " A B C" expression-> A " B " C.
                
                In the expression it is consider a operation that checks is A
                is next to B in a document and then if C is close to B.
                Giving it a preceden bigger than all the other operators except
                parenthasis helps to perform this operation before any other.

        ! vs -: !, is used to check if word B is close to word A by at leats 
                   3 words then it excludes that document if it is.
                -, is used to get all documents containing word A but not word B

//*****************************************************************************
//*****************************************************************************
/*                
*/
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static spark.Spark.get;

enum type implements Serializable {operator, operand}
enum associative implements Serializable{right, left}
public class MyParser {


    
    public abstract class parser_node implements Comparable<parser_node>, Serializable{
        public type T;       
        parser_node(){
            
        }
    }
    
    private class operand extends parser_node implements Serializable{
        public String data;
        
        operand(String d){
            data = d;
            T = type.operand;
        }

         @Override
        public boolean equals(Object o) {
            if(!(o instanceof operand))
                return false;
            
            else if(o == this)
                return true;
            else
                return false;
        }

        @Override
        public int compareTo(parser_node o) {
            operand tmp = (operand) o;
            
            return data.compareTo(tmp.data);
        }
    }
    private class operator extends parser_node implements Serializable{
        public char data;
        public int precedence;
        
        
        operator(char d){
            data = d;
            T = type.operator;
            
            if(d == '+' || d == '-')
                precedence = 1;
            
            else if(d == '*')
                precedence = 2;
            else if(d == '!')
                precedence = 4;
            else if(d == '(' || d == ')')
                precedence = 5;
            
            else if(d == '"')
                precedence = 3;
        }

         @Override
        public boolean equals(Object o) {
            if(!(o instanceof operator))
                return false;
            
            if(o == this)
                return true;
            
            return false;
        }

        @Override
        public int compareTo(parser_node o) {
            operator tmp = (operator) o;
            
            return new Character(data).compareTo(tmp.data);
        }
    }
    
    public LinkedList<parser_node> postfixExpression;
    public LinkedList<parser_node> infixExpression;
    public LinkedList<operator> operatorStack;
    
    public DocumentsBuilder documents; 
    public DocumentIndexTables indexAndtables;
    public LinkedList<MyDocument> testingList;
    public boolean phrase_flag;
    public LinkedList<HashMap<String,documentNode>> results = new LinkedList();
    
    public boolean Displaying_Results;
    
    MyParser() throws IOException, InterruptedException{
        postfixExpression = new LinkedList<>();
        infixExpression = new LinkedList<>();
        operatorStack = new LinkedList<>();        
        documents = new DocumentsBuilder();
        indexAndtables = new DocumentIndexTables();
        Displaying_Results = false;
        
        //setupTestList();
        //printTableContents(a);
    }
     
    private void reinitialize(){
        postfixExpression = new LinkedList<>();
        infixExpression = new LinkedList<>();
        operatorStack = new LinkedList<>();
        results = new LinkedList<>();
    }
    
    public void printInfix(){
        operator tmp1;
        operand tmp2;
        String output = "";

        for(parser_node pn: infixExpression){
            if(pn.T == type.operator){
                tmp1 = (operator)pn;
                output = Character.toString(tmp1.data)+" ";
            }
            else if(pn.T == type.operand){
                tmp2 = (operand)pn;
                output = tmp2.data+" ";
            }         

        }

    }
    
    public void printPostfix(){
        operator tmp1;
        operand tmp2;
        String output = "";
        
        for(parser_node pn: postfixExpression){
            if(pn.T == type.operator){
                tmp1 = (operator)pn;
                output = Character.toString(tmp1.data) +" ";
            }
            else if(pn.T == type.operand){
                tmp2 = (operand)pn;
                output = tmp2.data+" ";
            } 
            
        }
 
    }  

    
//******************************************************************************   
//******************************************************************************  
//SETUPS INFIX EXPRESSION   
//******************************************************************************
    public void process(String query_entered, Object o) throws FileNotFoundException, IOException{
        System.out.println("Process 1");
        boolean flag = true;
        String q = query_entered;
        reinitialize();
        phrase_flag = false;
        String[] query = process_FixSpaces(q).trim().split("\\s+");
        
        for(String s: query){
            if(phrase_flag){
                if(!s.equals(new Character('"').toString())){
                    process(s);
                    process('"');
                }
                else{
                    infixExpression.removeLast();
                    phrase_flag = false;
                }
            }
            else if(s.equals("+"))
                process('*');
            else if(s.equals("-"))
                process('-');
            else if(s.equals("!"))
                process('!');
            else if(s.equals("("))
                process('(');
            else if(s.equals(")"))
                process(')');    
            else if(s.equals(new Character('"').toString()))
                process("\"");
                 
            else {             
                process(s); 
                continue;
            }
            phrase_flag = false; 
            
        }
        
        
         processInfix();
    }
 
    //private function process 2: Push Operators 
    private void process(char c){
        System.out.println("Process 2");
        if(c =='(' || c == '!'){
            if(!infixExpression.isEmpty()){
                if(infixExpression.getLast().T == type.operand)
                    infixExpression.add(new operator('+'));
                else{
                    operator opTemp = (operator) infixExpression.getLast();
                    if(opTemp.data == ')')
                        infixExpression.add(new operator('+'));
                }
            }
            infixExpression.add(new operator(c));
        }
        
        else{
            infixExpression.add(new operator(c));
        }
    }
    
    //private function process 3: Push Operands 
    private void process(String c){
        System.out.println("Process 3");
        if(!infixExpression.isEmpty()){
            
            //Check for or's
            if(infixExpression.getLast().T == type.operand)
                infixExpression.add(new operator('+'));
            else if(infixExpression.getLast().T == type.operator){
                operator tmp = (operator) infixExpression.getLast();
                if(tmp.data ==')')
                    infixExpression.add(new operator('+'));
            }
        }
        
        infixExpression.addLast(new operand(c));
    }  
    
    //FIXES THE SPACES IN THE QUERY
    //USED TO READ QUERY PROPERLY
    private String process_FixSpaces(String query){
        System.out.println("Process fixspaces");
        StringBuilder tmp = new StringBuilder(query);
        char [] tmpArray = query.toCharArray();
        int counter = 0;
        int currentIndex = 0;
        
        while(currentIndex < tmpArray.length){
            if(tmpArray[currentIndex] == '+'
                    ||tmpArray[currentIndex] == '-'
                    ||tmpArray[currentIndex] == '('
                    ||tmpArray[currentIndex] == ')'
                    ||tmpArray[currentIndex] == '!'
                    ||tmpArray[currentIndex] == '"'){
                
                tmp.insert(currentIndex + counter, " ");                
                counter+=2;
                tmp.insert(currentIndex + counter, " ");
            }
            currentIndex++;
        }
         
        System.out.println(tmp.toString());
        return tmp.toString();
    }
    
//******************************************************************************   
//******************************************************************************  
//SETUPS POSTFIX EXPRESSION;  PROCESSES THE INFIX EXPRESSION TO POSTFIX
//******************************************************************************    
    private void processInfix() throws FileNotFoundException, IOException{
        System.out.println("Process infix");
        operator tmp1, tmp3;
        operand tmp2;
        
        for(parser_node pn : infixExpression ){
            if(pn.T == type.operand){
                tmp2 = (operand)pn;
                postfixExpression.addLast(tmp2);
                       
            }
            else{
                tmp1 = (operator)pn;
                
                while(true){
                    if(operatorStack.isEmpty()){
                        if(tmp1.data == '(' )
                            tmp1.precedence = 0;
                        
                        operatorStack.addLast(tmp1);
                        break;
                    }
                    else if(tmp1.precedence <=  operatorStack.getLast().precedence){
                        
                        postfixExpression.addLast((operator)operatorStack.getLast());
                        operatorStack.removeLast();
                    }
                    
                    else{
                        
                        if(tmp1.data == ')'){
                            
                            tmp3 = (operator)operatorStack.getLast();
                            
                            
                            
                            while(tmp3.data != '('){                                
                                postfixExpression.addLast(tmp3);
                                operatorStack.removeLast();
                                tmp3 = (operator)operatorStack.getLast();
                                if(tmp3 == null)
                                    break;
                            }
                            if(tmp3 == null){
                                System.out.println("tmp3 is null error");
                            }
                            else
                                operatorStack.removeLast();
                        }
                        else{

                            if(tmp1.data == '(')
                                tmp1.precedence = 0;
                            operatorStack.addLast(tmp1);     
                        }
                        break;
                    }    
                }
  
            }  
        }
        while(!operatorStack.isEmpty()){
                    postfixExpression.addLast((operator)operatorStack.getLast());
                    operatorStack.removeLast();
        }
        
        processPostfix(testingList);
    }  
    
//******************************************************************************   
//******************************************************************************  
//PROCESSES THE POSTFIX
//******************************************************************************
    public void processPostfix(LinkedList<MyDocument> list) throws FileNotFoundException, IOException{
        System.out.println("Process post fix");
        printInfix();
        printPostfix();

        int currentIndex = 0;
        operand tmpOperand;
        HashMap<String, documentNode> h1;
        HashMap<String, documentNode> h2;

        operator tmpOperator;
        results = new LinkedList();

        for(parser_node n: postfixExpression){
            if(postfixExpression.get(currentIndex).T == type.operator){
                tmpOperator = (operator)postfixExpression.get(currentIndex);                          
                if(tmpOperator.data == '+'){
                    h2 = results.getLast();
                    results.removeLast();

                    h1 = results.getLast();
                    results.removeLast();


                    results.addLast(operateOR(h1, h2));
                }
                else if(tmpOperator.data == '*'){
                    h2 = results.getLast();
                    results.removeLast();

                    h1 = results.getLast();
                    results.removeLast();

                    results.addLast(operateAND(h1, h2));
                }
                else if(tmpOperator.data == '-'){
                    h2 = results.getLast();
                    results.removeLast();

                    h1 = results.getLast();
                    results.removeLast();

                    results.addLast(operateNOT(h1, h2));
                }
                else if(tmpOperator.data == '!'){
                    //h2 = results.getLast();
                    //results.removeLast();

                    h1 = results.getLast();
                    results.removeLast();

                    results.addLast(operateEXCEPT(h1));
                }
                else if(tmpOperator.data == '"'){
                    h2 = results.getLast();
                    results.removeLast();

                    h1 = results.getLast();
                    results.removeLast();

                    results.addLast(operatePhrase(h1, h2));
                }    

            }
            else{
                tmpOperand = (operand)postfixExpression.get(currentIndex);
                if(indexAndtables.wordTable.containsKey(tmpOperand.data))
                    results.add((HashMap<String, documentNode>) indexAndtables.wordTable.get(tmpOperand.data).documentHashMap.clone());
                else
                    results.add(new HashMap<String, documentNode>());
            }

            currentIndex++;
        }
        
        
        if(results.size()>1)
            System.out.println("results size too big! Something happend! \n \n \n \n");
            System.out.println("Results ("+results.getFirst().size()+"): \n \n");
        //Displaying_Results = true;
        //Displaying(new StringBuil);
        
    }
    
    public String Displaying() throws IOException{ 
        StringBuilder str_builder = new StringBuilder();
        int counter = 0;
        
        System.out.println("Checking Results: ");
            if(results.isEmpty())
                System.out.println("Results are Empty.....");
        System.out.println(results);
        Set<String> set = results.getFirst().keySet();
     
        
        LinkedList<String> displayedKeys = new LinkedList<>();
        BufferedReader reader;
        
        System.out.println("Start Search!");
        for(String key: set){           
            System.out.println("Found!");
            str_builder.append( "<a href=\""+ indexAndtables.documentTable.get(key).doc.url_page.getPath() + "\"> ID: "+indexAndtables.documentTable.get(key).doc.id+";   "+ indexAndtables.documentTable.get(key).doc.url_page + "<a> \n <br> ");
            //get(indexAndtables.documentTable.get(key).doc.url_page.getPath(), (request, response) -> {
            //return new HTML_Reader(new File(indexAndtables.documentTable.get(key).doc.url_page.getPath())).html_str;
           // });
            System.out.println("STR_BUILDER");
            System.out.println(str_builder.toString());
            //System.out.println(results.getFirst().get(key).url.getPath());
            reader = new BufferedReader(new FileReader(indexAndtables.documentTable.get(key).doc.url_page.getPath()));
            String str;

            while(true){
                str = reader.readLine();

                if(str == null)
                    break;

                //textArea.append("       "+str + "\n");
            }
            //textArea.append("\n \n");
            
            displayedKeys.add(key);
            counter++;
            if(counter == 10)
                break; 
        }
        for(String k: displayedKeys)
            results.getFirst().remove(k);    
         
        
        if(results.getFirst().isEmpty()){
            str_builder.append("The list is empty \n <br>");
            Displaying_Results = false;
        }
        else
           str_builder.append("There are "+results.getFirst().size() +" left... \n <br>");
        
        System.out.println("STR_BUILDER");
        System.out.println(str_builder.toString());
        return str_builder.toString();
    }
    //******************************************************************************  
    //OPERATOR FUNCTIONS: OR
    //******************************************************************************
    private HashMap<String, documentNode> operateOR(HashMap<String, documentNode> x, 
            HashMap<String, documentNode> y){      
        HashMap<String, documentNode> tmp = new HashMap<>();
        Set<String> keys;
        boolean found = false;
        
        if(x.size() >= y.size()){
            tmp = x;
            keys = y.keySet();
            for(String n_y: keys){
                if(!x.containsKey(n_y)){
                    tmp.put(n_y, y.get(n_y));
                }
            }
        }
        else{
            tmp = y;
            keys = x.keySet();
            for(String n_x: keys){
                if(!y.containsKey(n_x)){
                    tmp.put(n_x, x.get(n_x));
                }
            }
        }
        for(String s: tmp.keySet())
            System.out.println("OR: "+s);
        return tmp;
    }
    //******************************************************************************  
    //OPERATOR FUNCTIONS: AND
    //******************************************************************************
    private HashMap<String, documentNode> operateAND(HashMap<String, documentNode> x, 
            HashMap<String, documentNode> y){
        HashMap<String, documentNode> tmp = new HashMap<>();
        boolean found = false;
        Set<String> keys;
        
        if(x.size() >= y.size()){
            keys = y.keySet();
            for(String n_y: keys){
                if(x.containsKey(n_y)){
                    tmp.put(n_y, y.get(n_y));
                }
            }
        }
        else{
            keys = x.keySet();
            for(String n_x: keys){
                if(y.containsKey(n_x)){
                    tmp.put(n_x, x.get(n_x));
                }
            }
        }
        
        return tmp;
    }
    //******************************************************************************  
    //OPERATOR FUNCTIONS: NOT
    //******************************************************************************
    private HashMap<String, documentNode>  operateNOT(HashMap<String, documentNode> x, 
            HashMap<String, documentNode> y){
        HashMap<String, documentNode> tmp = x;
        boolean found = false;
        Set<String> keys;
        HashSet<String> key_set = new HashSet<>();
        keys = y.keySet();
        
        for(String n_y: keys){
           if(x.containsKey(n_y)){
               key_set.add(n_y);
           }           
        }
        for(String n_y: key_set){
           tmp.remove(n_y);
        }
        return tmp;
    }

    //******************************************************************************  
    //OPERATOR FUNCTIONS: Phrase
    //******************************************************************************
    private HashMap<String, documentNode>  operatePhrase(HashMap<String, documentNode> x, 
            HashMap<String, documentNode> y){
        HashMap<String, documentNode> tmp = new HashMap<>();
        
        boolean flag = false;
        Set<String> keys;
        keys = y.keySet();
        
        for(String n_y: keys){
           if(x.containsKey(n_y)){               
                for(Integer i: x.get(n_y).position){
                   for(Integer j: y.get(n_y).position){
                       if(Math.abs(i.intValue()-j.intValue()) <2 && i!=j){
                           tmp.put(n_y, y.get(n_y));
                           flag = true;
                           break;
                       }
                   } 
                }           
            }  
        }
        return tmp;
    }

    //******************************************************************************  
    //OPERATOR FUNCTIONS: NOT BY
    //******************************************************************************
    private HashMap<String, documentNode>  operateEXCEPT(HashMap<String, documentNode> x) throws MalformedURLException{
        HashMap<String, documentNode> tmp = new HashMap<>();  
        LinkedList<String> tmpList = new LinkedList<>();
        boolean flag = false;
 
        Set<String> keys;
        keys = indexAndtables.documentTable.keySet();
        
        for(String key: keys){
           if(!x.containsKey(key))
               tmpList.add(key);
        }   
        //new documentNode(indexAndtables.documentTable.get(n_x).doc.url, 0, null)
        for(String n_x: tmpList){
            //documentNode tmpnode = new documentNode(indexAndtables.documentTable.get(n_x).doc.url, 0, null);
            tmp.put(indexAndtables.documentTable.get(n_x).doc.url.getPath(), 
                      new documentNode(indexAndtables.documentTable.get(n_x).doc.url, 0, null));
        }
        return tmp;
    }
   

}
