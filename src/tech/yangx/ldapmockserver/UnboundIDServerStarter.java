package tech.yangx.ldapmockserver;

import java.io.File;
import java.io.IOException;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldif.LDIFReader;


public class UnboundIDServerStarter {

    private static final int PORT = 3893;  // Port for the mock server

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: UnboundIDServerStarter <baseDN> <ldifFile>");
            System.exit(1);
        }

        String baseDN = args[0];
        String ldifFilePath = args[1];

        try {
            
            // Create a temporary directory to store LDAP data
            File tempDirectory = File.createTempFile("ldap", "temp");
            tempDirectory.delete();
            tempDirectory.mkdir();
            

            // Set up the LDAP listener config
            InMemoryDirectoryServerConfig config = new InMemoryDirectoryServerConfig(
                    baseDN);
            config.setListenerConfigs(
                    InMemoryListenerConfig.createLDAPConfig("default", 3893));
            
            config.setSchema(null); // Disables schema checking
            @SuppressWarnings("resource")
			InMemoryDirectoryServer server = new InMemoryDirectoryServer(config);

            // Load the LDIF data into the server
            LDIFReader ldifReader = new LDIFReader(ldifFilePath);
            server.importFromLDIF(true,ldifReader);

            // Start the server
            server.startListening();
            System.out.println("UnboundID Directory Server started on port " + PORT);

        } catch (IOException e) {
            System.err.println("Failed to read LDIF file: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to start UnboundID Directory Server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
