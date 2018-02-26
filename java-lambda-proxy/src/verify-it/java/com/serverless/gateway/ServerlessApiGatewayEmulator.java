package com.serverless.gateway;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;


/**
 * Emulate the basic behavior of Serverless and API gateway so that we can run
 * end to end tests locally
 */
public class ServerlessApiGatewayEmulator {

    private final Logger logger = LogManager.getLogger(ServerlessApiGatewayEmulator.class);

    private static volatile ServerlessApiGatewayEmulator instance;

    private ServerlessApiGatewayEmulator(int port) {
        logger.info(" Serer start on port:" + port);
        Server server = new Server(port);
        ServletHandler servletHandler = new ServletHandler();;
        server.setHandler(servletHandler);

        servletHandler.addServletWithMapping(ApiGatewayServlet.class, "/*");

        try {
            server.start();
        } catch (Exception ex) {
            logger.error(ex);
            System.exit(1);
        }
    }

    public static ServerlessApiGatewayEmulator instance(int port) {
        if (instance == null) {
            synchronized (ServerlessApiGatewayEmulator.class) {
                if (instance == null) {
                    instance = new ServerlessApiGatewayEmulator(port);
                }
            }
        }
        return instance;
    }

}
