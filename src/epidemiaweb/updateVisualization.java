package epidemiaweb;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javax.swing.JOptionPane;
import static epidemiaweb.mainEntry.host;
import static epidemiaweb.mainEntry.password;
import static epidemiaweb.mainEntry.port;
import static epidemiaweb.mainEntry.user;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.swing.JPasswordField;

public class updateVisualization {
	public void execute() {

        Object[] message = {"Are you going to update visualization on website?"};
        int dialogButton = JOptionPane.showOptionDialog(null, message, "Generate report",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if ( dialogButton == JOptionPane.YES_OPTION) {
            FileInputStream fis = null;
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
                if (user == null) {
                    user = (String) JOptionPane.showInputDialog(null, "Login as:", host, JOptionPane.QUESTION_MESSAGE);
                }
                //no username input
                if (user == null) {
                    return;
                }
                //input password
                if (password == null) {
                    JPasswordField passwordField = new JPasswordField();
                    Object[] title = {"Enter password:\n\n", passwordField};
                    Object options[] = {"OK", "Cancel"};
                    if (JOptionPane.showOptionDialog(null, title, user+"@"+host,
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]) == JOptionPane.YES_OPTION) {
                        password = new String(passwordField.getPassword());
                    } else {
                        //no password input
                        return;
                    }
                }

                Session session = jsch.getSession(user, host, port);
                session.setPassword(password);
                session.setConfig("PreferredAuthentications",
                        "publickey,keyboard-interactive,password");
                session.connect();

                boolean ptimestamp = true;
                
                //change to your path that locates the report_data.RData
                String lfile = "D:\\Projects\\EPIDEMIA\\R\\epidemiatools_scripts\\weekly_report\\report_data.RData";
                File _lfile = new File(lfile);
                if (_lfile.exists() && !_lfile.isDirectory()) {
                    
                	//remote file
                    String rfile = "/data/epidemia/dev/visualization/vis/data/report_data.RData";

                    // exec 'scp -t rfile' remotely
                    String command = "scp " + (ptimestamp ? "-p" : "") + " -t " + rfile;
                    Channel channel = session.openChannel("exec");
                    ((ChannelExec) channel).setCommand(command);

                    // get I/O streams for remote scp
                    OutputStream out = channel.getOutputStream();
                    InputStream in = channel.getInputStream();

                    channel.connect();

                    if (checkAck(in) != 0) {
                        System.exit(0);
                    }

                    if (ptimestamp) {
                        command = "T" + (_lfile.lastModified() / 1000) + " 0";
                        // The access time should be sent here,
                        // but it is not accessible with JavaAPI ;-<
                        command += (" " + (_lfile.lastModified() / 1000) + " 0\n");
                        out.write(command.getBytes());
                        out.flush();
                        if (checkAck(in) != 0) {
                            System.exit(0);
                        }
                    }

                    // send "C0644 filesize filename", where filename should not include '/'
                    long filesize = _lfile.length();
                    command = "C0644 " + filesize + " ";
                    if (lfile.lastIndexOf('/') > 0) {
                        command += lfile.substring(lfile.lastIndexOf('/') + 1);
                    } else {
                        command += lfile;
                    }
                    command += "\n";
                    out.write(command.getBytes());
                    out.flush();
                    if (checkAck(in) != 0) {
                        System.exit(0);
                    }

                    // send a content of lfile
                    fis = new FileInputStream(lfile);
                    byte[] buf = new byte[1024];
                    while (true) {
                        int len = fis.read(buf, 0, buf.length);
                        if (len <= 0) {
                            break;
                        }
                        out.write(buf, 0, len); //out.flush();
                    }
                    fis.close();
                    fis = null;
                    // send '\0'
                    buf[0] = 0;
                    out.write(buf, 0, 1);
                    out.flush();
                    if (checkAck(in) != 0) {
                        System.exit(0);
                    }
                    out.close();

                    channel.disconnect();
                    session.disconnect();
                    System.out.println("Tranfer done.");
                    System.exit(0);
                }else{
                    System.out.println("ERROR: Could not find " + lfile);
                }
            } catch (Exception e) {
                System.out.println(e);
                try {
                    if (fis != null) {
                        fis.close();
                    }
                } catch (Exception ee) {
                }
            }
        }else if ( dialogButton == JOptionPane.CANCEL_OPTION){
            System.exit(0);
        }
    }

    static int checkAck(InputStream in) throws IOException {
        int b = in.read();
        // b may be 0 for success,
        //          1 for error,
        //          2 for fatal error,
        //          -1
        if (b == 0) {
            return b;
        }
        if (b == -1) {
            return b;
        }

        if (b == 1 || b == 2) {
            StringBuffer sb = new StringBuffer();
            int c;
            do {
                c = in.read();
                sb.append((char) c);
            } while (c != '\n');
            if (b == 1) { // error
                System.out.print(sb.toString());
            }
            if (b == 2) { // fatal error
                System.out.print(sb.toString());
            }
        }
        return b;
    }

}
