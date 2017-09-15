package com.speculation1000.specdb.start;

import java.sql.SQLException;
import java.time.Instant;
import java.util.logging.Level;

import org.h2.tools.Server;

import com.speculation1000.specdb.log.SpecDbLogger;

public class DbServer {
	
	private static final SpecDbLogger specLogger = SpecDbLogger.getSpecDbLogger();
	
	public static Server server;
	
	protected static final Instant DB_START_UP_TS = Instant.now();

    public static void startDB() throws SQLException {
    	server = Server.createTcpServer("-tcpPort", "8089", "-tcpAllowOthers").start();
    	specLogger.logp(Level.INFO, DbServer.class.getName(), "startDb", "H2 DbServer started");
    }

    public static void stopDB() throws SQLException {
        Server.shutdownTcpServer("tcp://localhost:8089", "", true, true);
        specLogger.logp(Level.INFO, DbServer.class.getName(), "startDb", "H2 DbServer stopped");
    }
    
    public static String getH2ServerStatus(){
    	return server.getStatus();
    }
    
    /**
	 * @returns seconds of db uptime
	 */
	public static long getSystemUptime(){
		return Instant.now().getEpochSecond() - DB_START_UP_TS.getEpochSecond();
	}

    public static void main(String[] args) {

        try {
            Class.forName("org.h2.Driver");

            if (args.length > 0) {
                if (args[0].trim().equalsIgnoreCase("start")) {
                    startDB();
                }

                if (args[0].trim().equalsIgnoreCase("stop")) {
                    stopDB();
                }
            } else {
                System.err
                        .println("Please provide one of following arguments: \n\t\tstart\n\t\tstop");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
