/*
 * The program is used to generate EPIDEMIA malaria detection and warning report.
 *
 * First, it calls running shell script on Kabru server to update EASTWeb data 
 *        to EPIDEMIA database.
 * Second, it calls running R modeling script on D:\Projects\EPIDEMIA\R
 *         \epidemiatools_scripts to create the data used to generate a weekly
 *         EPIDMIA forecast report. After running this script, it generates a 
 *         report by running weekly_report.Rnw
 * Last, it transferred the report_data.Rdata from local machine to Kabru server
 *       to update the visualization on development website.
 *
 */
package epidemiaweb;

import java.io.IOException;

public class mainEntry {
    static String host = "remote_host_address"; 
    static String user = "your_username";
    static String password = "your_password";
    static int port = 00000000000;// change the port

    public static void main(String[] args) throws IOException, InterruptedException {
        runShell runShell = new runShell();
        runShell.execute();
        runModel runModel=new runModel();
        runModel.execute();
        runReport runReport=new runReport();
        runReport.runRNW();
        updateVisualization vis = new updateVisualization();
        vis.execute();
    }
}
