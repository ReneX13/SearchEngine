I took a Web Search Engine course during the summer of 2016, and this is the project we worked on. Since
We hade to cramp everything into 1 month, the code is badly orginzed and there is alot to fix, but it works.

  The DocumentBuilder class crawls through html files. It analysis each file, collects its contents and hyperlinks. Then 
it does the same for the hyperlinks found in the starting html file. As it does that it creates text file of the content so the the
DocumentIndexTable class can use the data to index the html files. The MyParser class, then uses the DocumentIndexTables 
to search for the html files using a query.

  The program also uses Sparks framework to render html code to use as an interface. The interface is extremly simple, you type in a 
  query and it searches for documents. It displays 10 at a time, and if you want to look at the next 10 after a query, just enter "./more".
  
  I used the folder called RHF, that contains alot of html files. I put it in the C:/ directory.
  
  When you run the program for the first type, it takes a while to analyze the files that you give it. However, once it indexes everything
  it save the indexing data, and is loaded using that whenever you run it again.
  
  To crawl through the files again, make user to delete the Indexing_Saved_Data.txt file in the Documents folder.
  
  After the indexing is ready you can go to the browser and go to localhost:4567, which will open the interface. To stop the 
  program you need to manually close it on netbeans.
  
  other than that... I'll update a better organized version of this program in the future.
