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
