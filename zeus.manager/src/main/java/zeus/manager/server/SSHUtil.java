package zeus.manager.server;
import java.io.BufferedReader;  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
  
import ch.ethz.ssh2.Connection;  
import ch.ethz.ssh2.Session;  
import ch.ethz.ssh2.StreamGobbler;  
  
public class SSHUtil {  
    Connection conn = null;  
    Session session = null;  
    InputStream stdout = null;  
    BufferedReader br = null;  
  
    public SSHUtil(String hostname, String username, String password) {  
        try {  
            conn = new Connection(hostname);  
            conn.connect();  
            boolean isAuthenticated = conn.authenticateWithPassword(username, password);  
            if (isAuthenticated == false) {  
                throw new IOException("Authentication failed.");  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }  
  
    public void close() {  
        if (session != null) {  
            session.close();  
        }  
        if (conn != null) {  
            conn.close();  
        }  
    }  
  
    public void closeSession() {  
        session.close();  
    }  
  
    public void execCommand(String command) throws IOException {  
        session = conn.openSession();  
        session.execCommand(command);  
        stdout = new StreamGobbler(session.getStdout());  
        br = new BufferedReader(new InputStreamReader(stdout));  
    }  
  
    public String readLine() throws IOException {  
        return br.readLine();  
    }  
}  