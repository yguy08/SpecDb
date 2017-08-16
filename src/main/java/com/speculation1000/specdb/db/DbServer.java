package com.speculation1000.specdb.db;

import java.sql.SQLException;

import org.h2.tools.Server;

public class DbServer {
	
	public static Server server;

    public static void startDB() throws SQLException {
    	server = Server.createTcpServer("-tcpPort", "8082", "-tcpAllowOthers").start();
    }

    public static void stopDB() throws SQLException {
        Server.shutdownTcpServer("tcp://localhost:8082", "", true, true);
    }
    
    public static String getH2ServerStatus(){
    	return server.getStatus();
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
