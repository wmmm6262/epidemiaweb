package epidemiaweb;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;

public class runReport{
	public void runRNW() throws IOException, InterruptedException {

        Object[] message = {"Are you going to compile pdf report?"};
        int dialogButton = JOptionPane.showOptionDialog(null, message, "Generate report",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (dialogButton == JOptionPane.YES_OPTION) {
        	
        	//change to your rnwPath
            String rnwPath = "D:\\Projects\\EPIDEMIA\\R\\epidemiatools_scripts\\weekly_report\\weekly_report.Rnw";
            
            File f = new File(rnwPath);
            if (f.exists() && !f.isDirectory()) {
            	
            	//change to your Rscript path and rnwPath
                ProcessBuilder rnwPb = new ProcessBuilder("C:\\Program Files\\R\\R-3.4.1\\bin\\Rscript", "-e", "\"library(knitr); knit('D:/Projects/EPIDEMIA/R/epidemiatools_scripts/weekly_report/weekly_report.Rnw')\"");
                //change to your saved directory
                rnwPb.directory(new File("D:/Projects/EPIDEMIA/R/epidemiatools_scripts/test"));
                rnwPb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                rnwPb.redirectError(ProcessBuilder.Redirect.INHERIT);
                Process rnw = rnwPb.start();

                try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(rnw.getInputStream()))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    String result = sb.toString();
                    System.out.println(result);
                    br.close();
                }
                rnw.waitFor();
                toPDF();
            } else {
                System.out.println("ERROR: Could not find " + rnwPath);
            }
        }else if (dialogButton == JOptionPane.CANCEL_OPTION){
            System.exit(0);
        }
               
    }

    public void toPDF() throws IOException, InterruptedException {
        System.out.println("Generating PDF report...");
        //change to your texPath
        String texPath = "D:/Projects/EPIDEMIA/R/epidemiatools_scripts/test/weekly_report.tex";
        File f = new File(texPath);
        if (f.exists() && !f.isDirectory()) {
        	 //change to your pdflatex path and texPath
            ProcessBuilder pdfPb = new ProcessBuilder("C:\\Program Files\\MiKTeX 2.9\\miktex\\bin\\x64\\pdflatex", "D:/Projects/EPIDEMIA/R/epidemiatools_scripts/test/weekly_report.tex");
            //change to your saved directory
            pdfPb.directory(new File("D:/Projects/EPIDEMIA/R/epidemiatools_scripts/test"));
            pdfPb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            pdfPb.redirectError(ProcessBuilder.Redirect.INHERIT);
            Process pdf = pdfPb.start();

            try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(pdf.getInputStream()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                String result = sb.toString();
                System.out.println(result);
                br.close();
            }
            pdf.waitFor();
            
            System.out.println("Please find the report in D:/Projects/EPIDEMIA/R/epidemiatools_scripts/test.");
            
        } else {
            System.out.println("ERROR: Could not find " + texPath);
        }
    }
}
