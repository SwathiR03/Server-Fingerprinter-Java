import java.net.InetAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

public class ServerFingerprinter1
 {

    private static final Map<String, String> headers = new HashMap<>();

    // Set default HTTP headers
    static {
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3");
        headers.put("Accept-Language", "en-US,en;q=0.9");
        headers.put("Accept-Charset", "UTF-8");
        headers.put("Connection", "close");
    }

    public static void main(String[] args) throws Exception {

        // Get target URL from user input
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter target URL: ");
        String targetUrl = scanner.nextLine();
        scanner.close();
        URL url = new URL(targetUrl);
        int portNumber = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();

        // Print the port number
        System.out.println("Port number: " + portNumber);

        SSLContext sslContext = SSLContext.getInstance("SSL");
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        // Set HTTP headers
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        connection.setRequestMethod("GET");
        connection.connect();

        // Extract cookies from response headers
        Map<String, List<String>> cookies = connection.getHeaderFields();
        if (cookies.containsKey("Set-Cookie")) {
            List<String> cookieHeaders = cookies.get("Set-Cookie");
            for (String cookieHeader : cookieHeaders) {
                System.out.println("Cookie: " + cookieHeader);
            }
        }

        // Extract server header, content type, content length, and response body
        String serverHeader = connection.getHeaderField("Server");
        String contentType = connection.getHeaderField("Content-Type");
        String contentLength = connection.getHeaderField("Content-Length");

        // Get IP address allocation for target URL
        InetAddress[] addresses = InetAddress.getAllByName(url.getHost());
        StringBuilder ipAllocation = new StringBuilder();
        for (InetAddress address : addresses) {
            ipAllocation.append(address.getHostAddress()).append(" ");
        }

        StringBuilder response = new StringBuilder();
        try (Scanner responseScanner = new Scanner(connection.getInputStream(), "UTF-8")) {
            while (responseScanner.hasNextLine()) {
                response.append(responseScanner.nextLine());
                response.append("\n");
            }
        }

        // Extract keywords from response body
        String regex = "\\b\\w+\\b";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(response);
        StringBuilder keywords = new StringBuilder();
        while (matcher.find()) {
            keywords.append(matcher.group()).append(" ");
        }

        // Print results
        System.out.println("Target URL: " + targetUrl);
        System.out.println("IP address allocation: " + ipAllocation.toString().trim());
        System.out.println("Server header: " + serverHeader);
        System.out.println("Content type: " + contentType);
        System.out.println("Content length: " + contentLength);
        System.out.println("Keywords: " + keywords.toString().trim());

        // Print SSL certificate information
        System.out.println("SSL Certificate Information:");
        SSLContext sslContext2 = SSLContext.getInstance("SSL");
        sslContext2.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext2.getSocketFactory());
        HttpsURLConnection connection2 = (HttpsURLConnection) url.openConnection();
        connection2.setRequestMethod("HEAD");
        connection2.connect();
        Certificate[] certs = connection2.getServerCertificates();
        for (Certificate cert : certs) {
            if (cert instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) cert;
                System.out.println("\nCertificate subject: " + x509Certificate.getSubjectDN());
                System.out.println("Certificate issuer: " + x509Certificate.getIssuerDN());
                System.out.println("Certificate serial number: " + x509Certificate.getSerialNumber().toString(16));
                System.out.println("Certificate valid from: " + x509Certificate.getNotBefore());
                System.out.println("Certificate valid until: " + x509Certificate.getNotAfter());

                // Check certificate information and display warning or "go ahead" message
                if (isCertificateExpired(x509Certificate)) {
                    System.out.println("\nCertificate is expired. Proceed with caution.");
                } else {
                    System.out.println("\nCertificate is valid. You can go ahead.");
                }
            }
        }
    }

    private static boolean isCertificateExpired(X509Certificate certificate) {
        // Compare certificate's notAfter date with the current date
        return certificate.getNotAfter().compareTo(new java.util.Date()) < 0;
    }
}
