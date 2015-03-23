/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jdbcApp.gui.dialogs;



import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.File;
import java.net.URL;
import javax.swing.*;

public class HTMLDisplayDialog extends JDialog {
    private final String _class;
    private JEditorPane editorPane ;
    private URL fileURL;
    
    public HTMLDisplayDialog() {
        setTitle("HTML display dialog");
        setSize(500,500);
        setModal(true);
        
        this._class = this.getClass().getName();
        
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        
        setJMenuBar(genMenuBar());
        
        add(new JScrollPane(editorPane), BorderLayout.CENTER);
    }
    
    //***********************
    
    private JMenuBar genMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu fileM = new JMenu("File");
        
        JMenuItem saveAsMI = new JMenuItem("Save As...");  saveAsMI.addActionListener(new saveAsAction(this));
        fileM.add(saveAsMI);
        
        mb.add(fileM);
        return mb;
    }
    
    //**********************
    
    public void setPage(String url) { 
        System.out.println(_class+"/setPage(String) -entering");
        try {
            System.out.println("i'm in the try statement now...");
            this.setPage(new URL(url));
            System.out.println("i'm the otherside of the method call...");
        } catch(java.net.MalformedURLException mue) { System.out.println(_class+"/setPage(String) - "+mue); 
        } catch(java.io.IOException ioe) { System.out.println(_class+"/setPage(String) - "+ioe); 
        }
    }
    public void setPage(URL url) { 
        this.fileURL = url;
        System.out.println(_class+"/setPage(url) -entering");
        System.out.println("************** --- "+url.toString());
        try { editorPane.setPage(url); 
        } catch(java.io.IOException ioe) { System.out.println(_class+"/setPage(URL) - "+ioe); }
    }
    
    
    //***********************
    /*class printAction extends AbstractAction {
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            JOptionPane.showMessageDialog(null, "Function not currently implemented", "INFORMATION", JOptionPane.INFORMATION_MESSAGE);
        }
    }*/
    
    class saveAsAction extends AbstractAction {
        public JDialog owner;
        public saveAsAction(JDialog owner) {
            this.owner = owner;
        }
        public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
            JFileChooser fc = new JFileChooser(new File("/"));
            
            File selFile = fc.getSelectedFile();
            fc.setFileFilter(new CustomFileFilter("html", "HTML files"));
            int option = fc.showSaveDialog(owner);
            if (option == JFileChooser.APPROVE_OPTION) {
                try {
                    BufferedReader buff = new BufferedReader(new FileReader(fileURL.getPath()));
                    BufferedWriter bw = new BufferedWriter(new FileWriter(fc.getSelectedFile()));
                    while(buff.ready())
                        bw.write(buff.readLine());
                    System.out.println("done...");
                    
                    bw.flush();
                    bw.close();
                    buff.close();
                } catch(java.io.FileNotFoundException fnfe) { System.out.println(fnfe); 
                } catch(java.io.IOException ioe) { System.out.println(ioe); 
                }
            }
        }
    }
}