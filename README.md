# HTTPServerClient

## About the Project

The HTTP server has been created successfully and the data entered by the client is received and stored in a file for further assessment. Through this a student as well as teacher can easily accesses assignment status. 

**Tools:** Java IntelliJ IDE, web browser.

**Language:** Java.

**Libraries:** InputStream, OutputStream, Socket, Server Socket , java.nio.files.

## Implementation details
- Create a socket and assign a custom port to it (eg-8081)
- Wait for the client (web browser) to send request.
- Accept client request on the server side by using socket.accept command.
- Send html file to the client using output stream.
- Get input from user and store it in txt file using file writer command.
  - Input data –   Name, Division , roll no, G.R No. and assignments status.
- Send response to the client about submission status.
- Close the socket connection using socket.close command.
