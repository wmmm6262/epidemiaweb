/*
 * This module calls running R modeling script on D:\Projects\EPIDEMIA\R
 * \epidemiatools_scripts to create the data used to generate a weekly EPIDMIA
 * forecast report. 
 */
package epidemiaweb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import javax.swing.JOptionPane;

public class runModel {
	public void execute() throws IOException, InterruptedException {
        
    
        Object[] message = {"Are you going to run the report generation script?"};
        int dialogButton =JOptionPane.showOptionDialog(null, message, "Report generation",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if ( dialogButton == JOptionPane.YES_OPTION) {
            
        	//change to your RPath
            String RPath = "D:\\Projects\\EPIDEMIA\\R\\epidemiatools_scripts\\semiautomated.R";
            
            File f = new File(RPath);
            if (f.exists() && !f.isDirectory()) {
                
            	// change to your Rscript execution path 
                String cmd = "C:\\Program Files\\R\\R-3.4.1\\bin\\Rscript";
                ProcessBuilder pb = new ProcessBuilder(cmd, RPath, "--save");
                pb.redirectOutput(Redirect.INHERIT);
                pb.redirectError(Redirect.INHERIT);
                Process p = pb.start();
                
                try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(p.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    String result = sb.toString();
                    System.out.println(result);
                    br.close();
                }
                p.waitFor();
                System.out.println("Done running report generation script.");
            } else {
                System.out.println("ERROR: Could not find " + RPath);
            }
        } else if(dialogButton == JOptionPane.CANCEL_OPTION){
            System.exit(0);
        }
    }
}
