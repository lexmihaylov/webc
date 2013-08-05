/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package webc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author alexander
 */
public class Webc {
    private static String AbsolutePath;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if(args.length == 0) {
            System.err.println("Usage:");
            System.err.println("\t java -jar webc.jar <path_to_html_file>");
            System.exit(0);
        }
        
        String content = "";
        try {
            File fileInfo = new File(args[0]);
            String absolutePath = fileInfo.getAbsolutePath();
            
            AbsolutePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
            
            content = readFile(absolutePath);
        }catch(Exception e) {
            System.err.println(e.toString());
        }
        
        content = includeScriptsAndStyles(content);
        
        System.out.println(content);
    }
    
    private static String readFile(String filename) 
            throws FileNotFoundException, IOException {
        
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        StringBuilder content = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        
        return content.toString();
    }
    
    private static String readUrl (String url)
            throws FileNotFoundException, IOException {
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream(), "UTF-8"));
        StringBuilder content = new StringBuilder();
        String line;
        
        while((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        
        return content.toString();
    }
    
    private static String includeScriptsAndStyles(String content) {
        Document doc = Jsoup.parse(content);
        
        // Include javascript
        Elements scripts = doc.getElementsByTag("SCRIPT");
        for (int i = 0; i < scripts.size(); i++) {
            Element item = scripts.get(i);
            if(item.hasAttr("src")) {
                String url = item.attr("src");
                
                try {
                    String fileContent;
                    
                    if(url.matches("http(s|)://(.*)")) {
                        fileContent = readUrl(url);
                       
                    } else {
                        fileContent = readFile(new File(AbsolutePath, url).toString());
                    }
                    
                    String oldTag = item.toString();
                    item.html(fileContent);
                    item.removeAttr("src");
                    content = content.replace(oldTag, item.toString());
                    
                } catch(Exception e) {
                    System.err.println(e.toString());
                }
            }
        }
        
        scripts = null;
        
        // include css
        Elements styles = doc.getElementsByTag("LINK");
        for(int i = 0; i < styles.size(); i++) {
            Element item = styles.get(i);
            if(item.attr("type").toLowerCase().equals("text/css")
                    && item.hasAttr("href")) {
                String url = item.attr("href");
                
                try {
                    String fileContent;
                    
                    if(url.matches("http(s|)://(.*)")) {
                        fileContent = readUrl(url);
                       
                    } else {
                        fileContent = readFile(new File(AbsolutePath, url).toString());
                    }
                    
                    
                    content = content.replace(item.toString(), 
                            new StringBuilder().
                            append("<style>\n").
                            append(fileContent).
                            append("\n</style>\n")
                            );
                    
                } catch(Exception e) {
                    System.err.println(e.toString());
                }
            }
        }
        
        styles = null;
        
        
        return content;
    }
    
    
}
