//*****************************************************************************
//*****************************************************************************
/*
    *AUTHOR: Rene Reyes
    *DATE: 6/14/2016
*/
//*****************************************************************************
//*****************************************************************************
package com.mycompany.searchengine;
import java.io.File;
//*****************************************************************************
//*****************************************************************************
//USED TO STORE DIRECTORIES USED FOR THIS PROJECT
//*****************************************************************************
//*****************************************************************************
public class Directory {
    public String PROJECT_DIRECTORY;
    public String DOCUMENTS_DIRECTORY;
    public String TEMP_FILES;
    public String OLD89_DIRECTORY;
    public String TOMSAWYER_DOCUMENTS;
    public String RHF_DIRECTORY;
    public String RHF_TMP;
    Directory(){
        PROJECT_DIRECTORY = System.getProperty("user.dir") 
                    + File.separator
                    + "src"
                    + File.separator 
                    + "main"
                    + File.separator
                    + "java"
                    + File.separator
                    + "com"
                    + File.separator
                    + "mycompany"
                    + File.separator
                    + "searchengine"
                    + File.separator;
            
        DOCUMENTS_DIRECTORY = PROJECT_DIRECTORY 
                    + "Documents"
                    + File.separator;
        OLD89_DIRECTORY = DOCUMENTS_DIRECTORY
                    + "old89"
                    + File.separator;
        TEMP_FILES = DOCUMENTS_DIRECTORY
                    + "Temp_files"
                    + File.separator;
        RHF_TMP = DOCUMENTS_DIRECTORY
                    + "RHF_TMP"
                    + File.separator;
        TOMSAWYER_DOCUMENTS = DOCUMENTS_DIRECTORY
                    + "TomSawyer_Documents"
                    + File.separator;
        RHF_DIRECTORY = "C:/RHF";
    }
}
