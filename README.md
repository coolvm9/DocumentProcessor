public void extractTables(PDDocument document) {
        BasicExtractionAlgorithm algorithm = new BasicExtractionAlgorithm();
        for (int pageNumber = 1; pageNumber <= document.getNumberOfPages(); pageNumber++) {
            try {
                technology.tabula.Page page = new technology.tabula.Page(document.getPage(pageNumber - 1), pageNumber, 0, 0, 0, 0);
                List<Table> tables = algorithm.extract(page);
                for (Table table : tables) {
                    for (List<RectangularTextContainer<?>> row : table.getRows()) {
                        StringBuilder rowText = new StringBuilder("Table Row: ");
                        for (RectangularTextContainer<?> cell : row) {
                            rowText.append(cell.getText()).append("\t");
                        }
                        elements.add(rowText.toString().trim());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


public static CloseableHttpClient createHttpClientWithProxy(String proxyUrl) throws Exception {

        // Step 1: Parse the proxy URL to extract credentials, host, and port
        URI proxyUri = new URI(proxyUrl);
        String[] userInfo = proxyUri.getUserInfo().split(":");
        String username = userInfo[0];
        String password = userInfo[1];

        // Step 2: Set up credentials for the proxy using AuthScope
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxyUri.getHost(), proxyUri.getPort()),
                new UsernamePasswordCredentials(username, password.toCharArray()));

        // Step 3: Create an SSL context (trust all certificates for simplicity here)
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial((chain, authType) -> true)  // Trust all certificates (not recommended for production)
                .build();

        // Step 4: Create an SSLConnectionSocketFactory
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        // Step 5: Configure the connection manager with SSL and PlainSocket for HTTP
        PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory) // SSL support for HTTPS
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(Timeout.ofSeconds(30)).build())
                .build();

        // Step 6: Configure the proxy host and port
        HttpHost proxyHost = new HttpHost(proxyUri.getHost(), proxyUri.getPort());
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);

        // Step 7: Set up timeout configurations
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30))  // Connection timeout
                .setResponseTimeout(Timeout.ofSeconds(30))  // Response timeout
                .setConnectionRequestTimeout(Timeout.ofSeconds(30))  // Request timeout
                .build();

        // Step 8: Build the HTTP client with proxy, SSL, and credentials
        return HttpClients.custom()
                .setConnectionManager(connectionManager)  // Connection manager
                .setRoutePlanner(routePlanner)  // Proxy route planner
                .setDefaultCredentialsProvider(credsProvider)  // Proxy authentication using credentials
                .setDefaultRequestConfig(requestConfig)  // Timeout configurations
                .build();
    }


    public class HttpClientWithProxy {

    /**
     * Method to create an HTTP client with proxy settings and SSL configuration.
     *
     * @param proxyUrl Proxy URL in the format "http://username:password@proxy.company.com:8080"
     * @return Configured CloseableHttpClient
     * @throws Exception If any configuration error occurs
     */
    public static CloseableHttpClient createHttpClientWithProxy(String proxyUrl) throws Exception {

        // Step 1: Parse the proxy URL to extract credentials, host, and port
        URI proxyUri = new URI(proxyUrl);
        String[] userInfo = proxyUri.getUserInfo().split(":");
        String username = userInfo[0];
        String password = userInfo[1];

        // Step 2: Set up credentials for the proxy using AuthScope
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxyUri.getHost(), proxyUri.getPort()),
                new UsernamePasswordCredentials(username, password.toCharArray()));

        // Step 3: Create an SSL context (trust all certificates for simplicity here)
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial((chain, authType) -> true)  // Trust all certificates (not recommended for production)
                .build();

        // Step 4: Create an SSLConnectionSocketFactory
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        // Step 5: Configure the connection manager with SSL
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(
                RegistryBuilder.<ConnectionSocketFactory>create()
                        .register("https", sslSocketFactory)
                        .register("http", ConnectionSocketFactory.PLAIN)
                        .build());

        // Step 6: Configure the proxy host and port
        HttpHost proxyHost = new HttpHost(proxyUri.getHost(), proxyUri.getPort());
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);

        // Step 7: Set up timeout configurations
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30))  // Connection timeout
                .setResponseTimeout(Timeout.ofSeconds(30))  // Response timeout
                .setConnectionRequestTimeout(Timeout.ofSeconds(30))  // Request timeout
                .build();

        // Step 8: Build the HTTP client with proxy, SSL, and credentials
        return HttpClients.custom()
                .setConnectionManager(connectionManager)  // Connection manager
                .setRoutePlanner(routePlanner)  // Proxy route planner
                .setDefaultCredentialsProvider(credsProvider)  // Proxy authentication using credentials
                .setDefaultRequestConfig(requestConfig)  // Timeout configurations
                .build();
    }


    public static CloseableHttpClient createHttpClientWithProxy(String proxyUrl) throws Exception {

        // Step 1: Parse the proxy URL to extract credentials, host, and port
        URI proxyUri = new URI(proxyUrl);
        String[] userInfo = proxyUri.getUserInfo().split(":");
        String username = userInfo[0];
        String password = userInfo[1];

        // Step 2: Set up credentials for the proxy using AuthScope
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxyUri.getHost(), proxyUri.getPort()),
                new UsernamePasswordCredentials(username, password.toCharArray()));

        // Step 3: Create an SSL context (trust all certificates for simplicity here)
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial((chain, authType) -> true)  // Trust all certificates (not recommended for production)
                .build();

        // Step 4: Configure the proxy host and port
        HttpHost proxyHost = new HttpHost(proxyUri.getHost(), proxyUri.getPort());
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);

        // Step 5: Set up timeout configurations
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30))  // Connection timeout
                .setResponseTimeout(Timeout.ofSeconds(30))  // Response timeout
                .setConnectionRequestTimeout(Timeout.ofSeconds(30))  // Request timeout
                .build();

        // Step 6: Build the HTTP client with proxy and credentials
        return HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())  // Connection manager
                .setSSLContext(sslContext)  // SSL context for handling HTTPS requests
                .setRoutePlanner(routePlanner)  // Proxy route planner
                .setDefaultCredentialsProvider(credsProvider)  // Proxy authentication using credentials
                .setDefaultRequestConfig(requestConfig)  // Timeout configurations
                .build();
    }


import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.net.URIAuthority;
import org.apache.hc.core5.net.URIBuilder;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.auth.CredentialsProvider;
import org.apache.hc.core5.ssl.SSLContextBuilder;



public static CloseableHttpClient createHttpClientWithProxy(String proxyUrl) throws Exception {

        // Step 1: Parse the proxy URL to extract credentials and proxy host/port
        URI proxyUri = new URI(proxyUrl);
        String[] userInfo = proxyUri.getUserInfo().split(":");
        String username = userInfo[0];
        String password = userInfo[1];

        // Step 2: Set up credentials for the proxy (username and password)
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new URIAuthority(proxyUri), new UsernamePasswordCredentials(username, password.toCharArray()));

        // Step 3: Create an SSL context (trust all certificates for simplicity here, but adjust in production)
        SSLContextBuilder sslContextBuilder = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true);
        
        // Step 4: Configure the proxy host and port
        HttpHost proxyHost = new HttpHost(proxyUri.getHost(), proxyUri.getPort());
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxyHost);

        // Step 5: Build the HTTP client with proxy and credentials
        return HttpClients.custom()
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .setSSLContext(sslContextBuilder.build())  // SSL context to trust certificates
                .setRoutePlanner(routePlanner)  // Set the proxy route planner
                .setDefaultCredentialsProvider(credsProvider)  // Set credentials for proxy authentication
                .build();
    }

    public static void main(String[] args) {
        try {
            // Proxy URL with username and password
            String proxyUrl = "http://myenterpriseId:password@proxy.company.com:8080";

            // Create HTTP client with proxy settings
            CloseableHttpClient httpClient = createHttpClientWithProxy(proxyUrl);

            // Now you can use httpClient to make requests through the proxy
            // Example: Make a request to an external URL via the proxy

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    package dev.fusion;

import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.ssl.TrustStrategy;
import org.apache.hc.core5.util.Timeout;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.security.cert.X509Certificate;

public class HttpClientWithProxy {

    /**
     * Method to create an HTTP client with proxy settings and SSL configuration.
     *
     * @param proxyUrl Proxy URL in the format "http://username:password@proxy.company.com:8080"
     * @return Configured CloseableHttpClient
     * @throws Exception If any configuration error occurs
     */
    public static CloseableHttpClient createHttpClientWithProxy(String proxyUrl) throws Exception {

        // Step 1: Parse the proxy URL to extract credentials, host, and port
        URI proxyUri = new URI(proxyUrl);
        String[] userInfo = proxyUri.getUserInfo().split(":", 2);
        String username = userInfo[0];
        String password = userInfo[1];

        // Step 2: Set up credentials for the proxy using AuthScope
        BasicCredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(
                new AuthScope(proxyUri.getHost(), proxyUri.getPort()),
                new UsernamePasswordCredentials(username, password.toCharArray())
        );

        // Step 3: Create an SSL context that trusts all certificates (for testing purposes)
        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, (TrustStrategy) (X509Certificate[] chain, String authType) -> true)
                .build();

        // Step 4: Create an SSLConnectionSocketFactory with the custom SSL context
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext);

        // Step 5: Configure the connection manager with the SSL socket factory
        var connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build();

        // Step 6: Configure the proxy host and route planner
        HttpHost proxy = new HttpHost(proxyUri.getHost(), proxyUri.getPort());
        HttpRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);

        // Step 7: Set up request configuration
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(30))
                .setResponseTimeout(Timeout.ofSeconds(30))
                .setConnectionRequestTimeout(Timeout.ofSeconds(30))
                .build();

        // Step 8: Build the HTTP client without setSSLContext()
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .setDefaultCredentialsProvider(credsProvider)
                .setRoutePlanner(routePlanner)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    public static void main(String[] args) {
        try {
            // Proxy URL with username and password
            String proxyUrl = "http://myenterpriseId:password@proxy.company.com:8080";

            // Create HTTP client with proxy settings
            CloseableHttpClient httpClient = createHttpClientWithProxy(proxyUrl);

            // Now you can use httpClient to make requests through the proxy
            HttpGet request = new HttpGet("https://www.example.com");
            httpClient.execute(request, response -> {
                System.out.println("Status Code: " + response.getCode());
                System.out.println("Response Body: " + EntityUtils.toString(response.getEntity()));
                return null;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
