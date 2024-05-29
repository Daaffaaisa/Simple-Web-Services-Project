package gradleproject1;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


/*
 * kelas ini bertanggung jawab untuk mengatur dan menjalankan server web menggunakan jetty. Sebuah server HTTP
 * dan servlet container yang berbasis Java. Kelas ini juga mengintegrasikan logging untuk memantau akses ke server
 */
public class ServerSetup {

    private Server server;
    private Logger logger;
    private GUIManager guiManager;

    /*
     * Konstruktor menerima port server dan instance GUIManager untuk interaksi GUI, juga memanggil setupLogger untuk
     menginisialisasi logging
     */
    public ServerSetup(int port, GUIManager guiManager) {
        this.server = new Server(port);
        this.guiManager = guiManager;
        setupLogger();
    }

    /*
     * metode ini mengatur logger dengan naa fie log yang berformat tanggal. Logger ini mencatat semua aktivitas
     ke file di direktori yang di tentukan
     */
    private void setupLogger() {
        logger = Logger.getLogger(ServerSetup.class.getName());
        try {
            // Format the current date for the log file name
            String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String logFileName = String.format("D:/Web/Log/log-%s.log", date);

            // Create the FileHandler with the formatted log file name
            FileHandler fh = new FileHandler(logFileName, true);
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Metode ini mengonfirmasi dan memulai server dengan beberapa handler yang diatur
    public void setupServer() throws Exception {
        // resourceHandler adalah handler kustom yang menangani permintaan untuk mengakses file atau folder
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase("D:/Web/File");
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{});

        //LoggingHandler adalah handler kustom yang menangani permintaan untuk mengakses server
        LoggingHandler loggingHandler = new LoggingHandler();

        // HandlerCollection berfungsi untuk memastikan LoggingHandler diproses sebelum resourceHandler
        HandlerCollection handlers = new HandlerCollection();
        handlers.addHandler(loggingHandler);
        handlers.addHandler(resourceHandler);

        server.setHandler(handlers);
        server.start();
    }

    /*
     * kelas yang menangani logika untuk menangkap dan mencatat setiap request ke server.
     */
    private class LoggingHandler extends AbstractHandler {

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
            logAccess(request);
            baseRequest.setHandled(false); // Pastikan tidak menghentikan chain handling
        }
    }

    /*
     * kelas ini mencatat detail seperti alamat IP klien, URL, dan waktu akses ke server.
     */

   private void logAccess(HttpServletRequest request) {
    String clientAddress = request.getRemoteAddr();  // Ambil alamat IP langsung dari request

    String requestURI = request.getRequestURI();
    String fileName = requestURI.substring(requestURI.lastIndexOf('/') + 1);
    String filePath = request.getRequestURL().toString(); // Mendapatkan URL lengkap

    // Cek apakah requestURI berakhir dengan '/' yang menandakan akses ke folder
    String actionDescription = fileName.isEmpty() ? "Membuka Folder" : "Membuka File";

    // Membuat log message dengan format baru yang mencakup nama file
    String logMessage = String.format("%s - %s - %s %s",
            new SimpleDateFormat("yyyy-MM-dd:HH:ss").format(new Date()),
            clientAddress,
            actionDescription,
            fileName.isEmpty() ? requestURI : fileName);  // Tampilkan nama file atau URI jika folder

    logger.info(logMessage);
    System.out.println("Logged access: " + logMessage);
    if (guiManager != null) {
        guiManager.updateLogArea(logMessage);
    }
}

    public void stopServer() throws Exception {
        server.stop();
    }
}
