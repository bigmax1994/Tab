package com.dcm;
import com.dcm.Handlers.WebHandler;
import com.dcm.AllocationHelpers.Allocator;
import com.dcm.Handlers.UserHandler;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Handler;

import com.dcm.Utils.QRGenerator;
import java.awt.image.BufferedImage;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;

public class App {

    static int PORT = 8080;
    static String DNS = "";
    static String resourcesPath = "";

    static public String currentDebate = "";

    static public UserHandler userHandler;
    static public Allocator allocator;

    public static void main( String[] args )
    {
        // check for args
        // arg1 = port
        // arg2 = username
        // arg3 = password
        // arg6 = resources path
        // arg7 = dns
        // if args are not provided, print usage and exit
        if (args.length < 5) {
            System.out.println("Usage: java -jar dcm.jar <port> <username> <password> <resources path> <dns>");
            return;
        }
        try {
            App.PORT = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid port number: " + args[0]);
            return;
        }
        String username = args[1];
        String password = args[2];
        App.resourcesPath = args[3];
        App.DNS = args[4];

        // Print the configuration
        System.out.println("Configuration:");
        System.out.println("Port: " + PORT);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Resources Path: " + resourcesPath);
        System.out.println("DNS: " + DNS);
        System.out.println("Starting HTTP server on port " + PORT);

        // Initialize the database handler
        App.userHandler = new UserHandler(username, password);
        // Initialize the allocator
        App.allocator = new Allocator();
        System.out.println("Allocator initialized with code: " + App.allocator.getCurrentDebateCode());

        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setHost(null);
        connector.setPort(PORT);
        server.setConnectors(new Connector[] {connector});

        Handler handler = WebHandler.makeWebHandler(App.userHandler, resourcesPath);

        server.setHandler(handler);
        try {
            server.start();
            System.out.println("Server started on port " + PORT);
        } catch (Exception e) {
            System.out.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    public static void recreatePDF(String debateCode) {
        // check the debate code
        if (debateCode == null || debateCode.isEmpty()) {
            System.out.println("No debate code found, cannot recreate PDF.");
            return;
        }
        //make the qr code
        System.out.println("Recreating PDF for debate code: " + debateCode);
        String qrText = App.DNS + "/join/?code=" + debateCode;
        BufferedImage qrImage;
        try {
            qrImage = QRGenerator.generateQRCode(qrText, 200);
            System.out.println("QR code created successfully.");
        } catch (Exception e) {
            System.out.println("Error creating QR code: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        PDImageXObject pdImage;
        PDDocument pdDocument = new PDDocument();
        PDPage page = new org.apache.pdfbox.pdmodel.PDPage();
        pdDocument.addPage(page);
        try {
            pdImage = JPEGFactory.createFromImage(pdDocument, qrImage);
            System.out.println("QR code added to pdf succesfully.");
        } catch (Exception e) {
            System.out.println("Error adding QR code to pdf: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        // Create a content stream to write to the PDF
        PDPageContentStream contentStream;
        try {
            contentStream = new PDPageContentStream(pdDocument, page);
            PDFont fontBold = new PDType1Font(FontName.HELVETICA_BOLD);
            PDFont font = new PDType1Font(FontName.HELVETICA);
            int marginTop = 50; // Margin from the top of the page
            //write the title
            contentStream.beginText();
            String title = "Scan the QR Code to Join the Debate";
            int titleFontSize = 24;
            float titleWidth = fontBold.getStringWidth(title) / 1000 * titleFontSize;
            float titleHeight = fontBold.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * titleFontSize;
            contentStream.setFont(font, titleFontSize);
            contentStream.newLineAtOffset((page.getMediaBox().getWidth() - titleWidth) / 2, page.getMediaBox().getHeight() - marginTop - titleHeight);
            contentStream.showText(title);
            contentStream.endText();
            // Draw the image at the specified position
            float qrSize = 300; // Size of the QR code
            contentStream.drawImage(pdImage, (page.getMediaBox().getWidth() - qrSize) / 2, page.getMediaBox().getHeight() - marginTop - titleHeight - 10 - qrSize, qrSize, qrSize); // Adjust the position and size as needed
            // Add some text below the QR code
            contentStream.beginText();
            int fontSize = 12;
            float textWidth = font.getStringWidth(qrText) / 1000 * fontSize;
            float textHeight = font.getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * fontSize;
            contentStream.setFont(font, fontSize);
            contentStream.newLineAtOffset((page.getMediaBox().getWidth() - textWidth) / 2, page.getMediaBox().getHeight() - marginTop - textHeight - titleHeight - qrSize);
            contentStream.showText(qrText);
            contentStream.endText();
            // Close the content stream
            contentStream.close();
            System.out.println("QR code drawn on PDF successfully.");
        } catch (Exception e) {
            System.out.println("Error drawing QR code on PDF: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        //save the pdf
        try {
            pdDocument.save(App.resourcesPath + "admin/debate.pdf");
            System.out.println("PDF saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving PDF: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                pdDocument.close();
            } catch (Exception e) {
                System.out.println("Error closing PDF document: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
