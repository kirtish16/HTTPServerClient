import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;

public class Server {

    public static void main(String[] args) throws Exception {

        // Step 1-Create a socket and assigned a custom port to it (eg-8081)
        try (ServerSocket serverSocket = new ServerSocket(8081)) {
            //Step 2-Wait for the client (web browser) to send request.
            while (true) {
                // Step 3- Accept client request on the server side by using socket.accept command. 
                try (Socket client = serverSocket.accept()) {
                    handleClient(client);
                }
            }
        }
    }

    private static void handleClient(Socket client) throws IOException {
        // Step 5-Get input from user and store it in txt file using file writer command.
        //     Input data – Name , Division , roll No. ,G.R No. and assignments status.
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        StringBuilder requestBuilder = new StringBuilder();
        String line, usrData = "", data = "";

        String[] stdata = new String[7];

        while (!(line = br.readLine()).isBlank()) {
            requestBuilder.append(line + "\r\n");
            if (line.substring(0, 3).equals("GET")) {
                usrData = line;
            }
        }

        int len = usrData.length();

        if (len > 15 && usrData.indexOf("submit.html") != -1) {
            data = usrData.substring(17, len - 9);

            stdata = data.split("&");

            String datapath = "StudentData.txt";

            try {
                FileWriter fw = new FileWriter(datapath, true);
                for (int i = 0; i < (stdata.length); i++) {
                    for (int j = 0; j < stdata[i].length(); j++)
                        fw.write(stdata[i].charAt(j));
                    fw.write(",");
                }
                fw.write("\n");

                fw.close();
            }

            catch (IOException e) {
            }

        }
        String request = requestBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String path = requestLine[1];

        List<String> headers = new ArrayList<>();
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }

        Path filePath = getFilePath(path);

        if (Files.exists(filePath)) {
            // file exist
            String contentType = guessContentType(filePath);
            sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            // 404
            byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
            sendResponse(client, "404 Not Found", "text/html", notFoundContent);
        }
        // Step 7 – Close the socket connection using socket.close command.
    }

    // Step 4- Send html file to the client using output stream.
    private static void sendResponse(Socket client, String status, String contentType, byte[] content)
            throws IOException {
        OutputStream clientOutput = client.getOutputStream();
        clientOutput.write(("HTTP/1.1 \r\n" + status).getBytes());
        clientOutput.write(("ContentType: " + contentType + "\r\n").getBytes());
        clientOutput.write("\r\n".getBytes());
        clientOutput.write(content);
        clientOutput.write("\r\n\r\n".getBytes());
        clientOutput.flush();

        client.close();
    }

    private static Path getFilePath(String path) {

        if ("/".equals(path)) {
            path = "index.html";
        }

        // Step 6 - Send response to the client about submission status.
        if (path.indexOf("submit") != -1)
            path = "submit.html";

        return Paths.get("", path);
    }

    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }
}
