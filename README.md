## Computer Networks Project by Swathi Rao and Varsha Shree A

# Server Fingerprinter Technical Documentation

## Introduction

The **Server Fingerprinter** is a Java application designed to retrieve and present key technical information about a specified target website's server and its associated SSL certificate. By establishing a secure connection to the target server and utilizing HTTPS protocols, the program extracts a range of details, including server-related information, IP address allocation, SSL certificate specifications, and more! It helps users make informed decisions about visiting websites and trusting them, especially relevant in the age of data theft. However, it is important to note that this program is built for educational purposes and trusts all certificates.

## How does it work? 

1. **Default HTTP Headers Configuration:**
   - The application starts by setting up a default collection of HTTP headers, comprising standard values like User-Agent, Accept-Language, Accept-Charset, and Connection.
   - These headers are essential for establishing communications with the server. 

2. **User Input and URL Parsing:**
   - The user is asked to input the URL of the target website.
   - The user given URL is parsed to extract the hostname and (in some cases) the port number. If no port number is provided- we use the HTTPS protocol by default!
 
3. **SSL Context and Trust Management Configuration:**
   - An SSL context is established, incorporating a custom trust manager that accepts all certificates. It's still pretty rudimentary and accepts faked certificates. 

4. **Connection Establishment:**
   - Using the provided URL, an HTTPS connection is established with the target server.
   - The default SSL socket factory and the customized trust manager are associated with the connection, ensuring SSL handling.

5. **Setting HTTP Headers:**
   - The predefined default headers are added to the connection request.
   - These headers provide essential information to the server and contribute to a seamless connection.

6. **Connection and Response Handling:**
   - A GET request is sent to the server to retrieve the response.
   - The response headers and the content body are extracted for further processing.
   - If "Set-Cookie" headers are present, they are displayed to show any cookies provided by the server.

7. **Server Information Extraction:**
   - Details about the server are extracted from the response headers. This includes the server header, content type, content length, and keywords found within the response body.

8. **IP Address Allocation:**
   - The program obtains IP address allocations associated with the target URL by performing a DNS lookup.
   - The retrieved IP addresses are presented for informational purposes.

9. **SSL Certificate Information Retrieval:**
   - Another connection to the target server is established using the HEAD method, specifically to retrieve the SSL certificate information.
   - The program extracts various details from the SSL certificate, such as the subject, issuer, serial number, validity period, and expiration status.

10. **Certificate Expiration Check:**
    - A function is employed to evaluate whether the SSL certificate has expired.
    - The certificate's "notAfter" date is compared with the current date to ascertain its validity.
    - Depending on the result, an appropriate message is displayed indicating whether the certificate is expired or valid.

## Usage

1. **Compile!:** Compile the Java code using an appropriate compiler. I use BlueJ. (Time to grow up)
2. **Execute!:** Run the compiled program.
3. **Input!:** Provide the target URL when prompted.
4. **Results!:** The application will display comprehensive information about the server, IP address allocation, SSL certificate, and its validity status.

## Caution

This code employs a custom trust manager that trusts all certificates. While useful for educational purposes and exploring server details, this configuration is not recommended for the real world.
