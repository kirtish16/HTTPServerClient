package TrialJava;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main( String[] args ) throws Exception {
        try (ServerSocket serverSocket = new ServerSocket(8081)) {
            while (true) {
                try (Socket client = serverSocket.accept()) {
                    handleClient(client);
                }
            }
        }
    }

    private static void handleClient(Socket client) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));

        StringBuilder requestBuilder = new StringBuilder();
        String line,usrData ="",data="";

        String[] stdata = new String[7];

        System.out.println("--------------------------------------");
        while (!(line = br.readLine()).isBlank()) {
            requestBuilder.append(line + "\r\n");
            if(line.substring(0,3).equals("GET"))
            {
                usrData = line;
                System.out.println(usrData + "\r\n");
            }
        }

        int len = usrData.length();
        if(len>15)
            data = usrData.substring(6,len-9);

        stdata = data.split("&");

        for(int i=0;i<(stdata.length);i++)
        {
            System.out.println(stdata[i]);
        }
        //Data to text file
        File fp = new File("StudentData.txt");
        FileWriter fw=new FileWriter(fp); 

        for(int i=0;i<(stdata.length);i++)
        {
            for(int j=0;j<stdata[i].length();j++)
                fw.write(stdata[i].charAt(j));
            fw.write(",");
        }
        fw.close(); 

        String request = requestBuilder.toString();
        String[] requestsLines = request.split("\r\n");
        String[] requestLine = requestsLines[0].split(" ");
        String method = requestLine[0];
        String path = requestLine[1];
        
        String version = requestLine[2];
        String host = requestsLines[1].split(" ")[1];

        List<String> headers = new ArrayList<>();
        for (int h = 2; h < requestsLines.length; h++) {
            String header = requestsLines[h];
            headers.add(header);
        }

        String accessLog = String.format("Client %s, method %s, path %s, version %s, host %s, headers %s",
                client.toString(), method, path, version, host, headers.toString());
        System.out.println(accessLog);


        Path filePath = getFilePath(path);
        System.out.println(filePath);

        if (Files.exists(filePath)) {
            // file exist
            String contentType = guessContentType(filePath);
            sendResponse(client, "200 OK", contentType, Files.readAllBytes(filePath));
        } else {
            // 404
            byte[] notFoundContent = "<h1>Not found :(</h1>".getBytes();
            sendResponse(client, "404 Not Found", "text/html", notFoundContent);
        }

    }

    private static void sendResponse(Socket client, String status, String contentType, byte[] content) throws IOException {
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
            path = "C:\\KIRTISH\\vs code projects\\Java\\TrialJava\\index.html";
        }

        // if(path.indexOf("submit") != -1)
        //     path = "C:\\KIRTISH\\vs code projects\\Java\\TrialJava\\submit.html";
        
        return Paths.get("", path);
    }

    private static String guessContentType(Path filePath) throws IOException {
        return Files.probeContentType(filePath);
    }
}

// name=First&rollno=12&div=B&grno=123&sub=as1&sub1=as2&sub2=as1