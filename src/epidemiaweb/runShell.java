/*
 * This module calls running shell script on EPIDEMIA server to update EASTWeb data 
 * to EPIDEMIA database.
 *
 * Reference: http://www.codesandscripts.com/2014/10/java-program-to-execute-shell-scripts-on-remote-server.html
 */
package epidemiaweb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import static epidemiaweb.mainEntry.host;
import static epidemiaweb.mainEntry.password;
import static epidemiaweb.mainEntry.port;
import static epidemiaweb.mainEntry.user;

/**
*
* @author Meng.Wang
*/

public class runShell {
	public void execute() {
        Object[] message = {"Are you going to transfer environmental data to EPIDEMIA database?"};
        int dialogButton =JOptionPane.showOptionDialog(null, message, "Update database",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (dialogButton == JOptionPane.YES_OPTION) {
            try {
                /**
                 * Create a new Jsch object This object will execute shell
                 * commands or scripts on server
                 */
                JSch jsch = new JSch();          
                
                //set your know_hosts: login kabru, and run the command: ssh-keyscan kabru.sdstate.edu > known_hosts
                // a file named known_hosts will be generated. 
                // copy the file to your directory and put the path in jsch.setKnownHosts(known_hosts_path)
                jsch.setKnownHosts("\\known_hosts");
                
                //input username
                user = (String) JOptionPane.showInputDialog(null, "Login as:", host, JOptionPane.QUESTION_MESSAGE);
                if (user == null) {
                    return;
                }
                //input password
                JPasswordField passwordField = new JPasswordField();
                Object[] title = {"Enter password:", passwordField};
                Object button[] = {"OK", "Cancel"};
                if (JOptionPane.showOptionDialog(null, title, user+"@"+host,
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, button, button[0]) == JOptionPane.YES_OPTION) {
                    password = new String(passwordField.getPassword());
                } else {
                    return;
                }

                Session session = jsch.getSession(user, host, port);
                session.setPassword(password);
                session.setConfig("PreferredAuthentications",
                        "publickey,keyboard-interactive,password");
                session.connect();

                //input shell environment
                String[] dirs = {"Development", "Production"};
                String directoy = (String) JOptionPane.showInputDialog(null, "Choose the directory:",
                        "Shell in directory", JOptionPane.QUESTION_MESSAGE, null, dirs, dirs[0]);
                //mode options
                String[] options = {"", "--no-server", "--no-log", "--debug", "-f Forces", "--f-EASTWeb", "--f-EPIDEMIA"};
                String file = null;
                if ("Development".equals(directoy)) {
                    String mode = "--dev ";
                    for (int i=0; i<options.length;i++){
                        options[i]=mode+options[i];
                    }
                    file = "/data/epidemia/dev/modeling/EPIDEMIA_Automation.sh";
                } else if ("Production".equals(directoy)) {
                    String mode = "--prod";
                    for (int i=0; i<options.length;i++){
                        options[i]=mode+options[i];
                    }
                    file = "/data/epidemia/prod/modeling/EPIDEMIA_Automation.sh";
                } else {
                    return;
                }
                //input options            
                String mode = (String) JOptionPane.showInputDialog(null, "Choose the option:",
                        "Run shell with option", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (mode == null) {
                    return;
                }
                Object[] obj = {"You are going to run:\n\n", file, mode};

                if (JOptionPane.showOptionDialog(null, obj, "Run script",
                        JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null) == JOptionPane.YES_OPTION) {

                    //create the excution channel over the session
                    ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
                    // Gets an InputStream for this channel. All data arriving in as messages from the remote side can be read from this stream.
                    InputStream in = channelExec.getInputStream();
                    // Set the command that you want to execute
                    // In our case its the remote shell script
                    channelExec.setCommand("sh " + file + " " + mode);
                    // Execute the command
                    channelExec.connect();
                    // Read the output from the input stream we set above
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String line;

                    //Read each line from the buffered reader and add it to result list
                    // You can also simple print the result here 
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }

                    //retrieve the exit status of the remote command corresponding to this channel
                    //int exitStatus = channelExec.getExitStatus();
                    //Safely disconnect channel and disconnect session. If not done then it may cause resource leak
                    channelExec.disconnect();
                    session.disconnect();
                    System.out.println("Update done.");
              
                } 
            } catch (Exception e) {
                System.err.println("Error: " + e);
            }
        }else if (dialogButton == JOptionPane.CANCEL_OPTION){
            System.exit(0);
        }
    }
}
