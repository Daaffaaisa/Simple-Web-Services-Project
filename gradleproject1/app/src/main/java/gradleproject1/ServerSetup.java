package gradleproject1;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.nio.file.Paths;
import java.util.logging.Handler;

public class ServerSetup {

    private Server server;
    private int port;
    private String resourceBase;
    private GUIManager guiManager;
    private Logger logger;
    private String logDirectory;

    public ServerSetup(int port, String resourceBase, GUIManager guiManager) {
        this.port = port;
        this.resourceBase = resourceBase;
        this.guiManager = guiManager;
        this.logDirectory = "."; // Default log directory
        this.logger = Logger.getLogger(ServerSetup.class.getName()); // Inisialisasi logger
        server = new Server(port);
    }

    private void setupLogger() {
        try {
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String logFileName = Paths.get(logDirectory, "log-" + date + ".log").toString();
            FileHandler fh = new FileHandler(logFileName, true);
            fh.setFormatter(new SimpleFormatter());
            LogWritterHandlers();
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void LogWritterHandlers() {
        if (logger != null) {
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
        }
    }

    public void setLogDirectory(String logDirectory) {
        this.logDirectory = logDirectory;
        // Re-setup logger to use the new directory
       LogWritterHandlers();
        setupLogger();
    }

    public void setupServer() throws Exception {
        ResourceHandler resourceHandler = new ResourceHandler();
        
        resourceHandler.setResourceBase(resourceBase);

        LoggingHandler loggingHandler = new LoggingHandler();

        HandlerList handlers = new HandlerList();
        handlers.addHandler(loggingHandler);
        handlers.addHandler(resourceHandler);

        server.setHandler(handlers);
        server.start();
    }

    private class LoggingHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            // Set header untuk mengontrol cache
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            logAccess(request);
        }
    }

    private void logAccess(HttpServletRequest request) {
        String clientAddress = request.getRemoteAddr();
        String requestURI = request.getRequestURI();
        String fileName = requestURI.substring(requestURI.lastIndexOf('/') + 1);
        String actionDescription = fileName.isEmpty() ? "Membuka Folder" : "Membuka File";
        String logMessage = String.format("%s - %s - %s %s",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                clientAddress,
                actionDescription,
                fileName.isEmpty() ? requestURI : fileName);

        logger.info(logMessage);
        
        guiManager.updateLogArea(logMessage);
    }

    public void stopServer() throws Exception {
        server.stop();
    }
}