package javacouchdb;

import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbException;
import org.lightcouch.CouchDbProperties;

public class CouchDBAccess {
    private CouchDbClient client;
    private String databaseName;
    private String mainUser;
    private String mainUserPassword;
    private final static String PROTOCOL = "http";
    private final static int PORT = 5984;
    private final static String HOST_SERVER = "localhost";

    public CouchDBAccess(String databaseName, String mainUser, String mainUserPassword) {
        this.databaseName = databaseName;
        this.mainUser = mainUser;
        this.mainUserPassword = mainUserPassword;
    }

    public CouchDBAccess(CouchDBAccess couchDBAccess) {
        this.databaseName = couchDBAccess.databaseName;
        this.mainUser = couchDBAccess.mainUser;
        this.mainUserPassword = couchDBAccess.mainUserPassword;
    }

    private CouchDbProperties createProperties() {
        CouchDbProperties properties = new CouchDbProperties();
        properties.setDbName(databaseName);
        properties.setCreateDbIfNotExist(true);
        properties.setHost(HOST_SERVER);
        properties.setPort(PORT);
        properties.setProtocol(PROTOCOL);
        properties.setUsername(mainUser);
        properties.setPassword(mainUserPassword);
        return properties;
    }

    private void openConnection() {
        try {
            client = new CouchDbClient(createProperties());
        } catch (CouchDbException couchDbException) {
            System.out.println(couchDbException.getMessage());
        }
    }

    public CouchDbClient getClient() {
        if (client == null) {
            openConnection();
        }
        return  client;
    }
}