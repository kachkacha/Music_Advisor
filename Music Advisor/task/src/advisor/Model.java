package advisor;

import advisor.enums.ACTION;
import advisor.enums.ENDPOINTS;
import advisor.enums.QUERY;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Model {

    private final HttpServer server;

    private final DataBase dataBase;

    public Model(DataBase dataBase) throws IOException {
        this.dataBase = dataBase;

        server = HttpServer.create();
        server.bind(new InetSocketAddress(dataBase.getPORT()), 0);

        server.createContext("/", exchange -> {
            String code = getQueryParam(exchange, QUERY.CODE);
            String success = "Got the code. Return back to your program.";
            String failure = "Not found authorization code. Try again.";

            if (code != null) {
                dataBase.setAccessCode(code);
                exchange.sendResponseHeaders(200, success.length());
                exchange.getResponseBody().write(success.getBytes());
            } else {
                exchange.sendResponseHeaders(200, failure.length());
                exchange.getResponseBody().write(failure.getBytes());
            }
            exchange.getResponseBody().close();
        });
        startServer();
    }

    private String getQueryParam(HttpExchange httpExchange, QUERY querry) {
        String query = httpExchange.getRequestURI().getQuery();
        if (query != null) {
            for (String part : query.split("&")) {
                String[] keyValue = part.split("=");
                if (keyValue.length > 1 && keyValue[0].equals(querry.getQuery())) {
                    return keyValue[1];
                }
            }
        }
        return null;
    }

    public String getAccessCodeURL() {
        return dataBase.getAUTHORIZATION_SERVER_PATH() +
                ENDPOINTS.AUTHORIZE.getEndpoint() + "?" +
                "client_id=" + dataBase.getCLIENT_ID() +
                "&redirect_uri=" + dataBase.getREDIRECT_URI() +
                "&response_type=code\n";
    }

    public String requestAccessToken() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .uri(URI.create(dataBase.getAUTHORIZATION_SERVER_PATH() + ENDPOINTS.ACCESS_TOKEN.getEndpoint()))
                .POST(HttpRequest.BodyPublishers.ofString("client_id=" + dataBase.getCLIENT_ID() +
                        "&client_secret=" + dataBase.getCLIENT_SECRET() +
                        "&grant_type=authorization_code" +
                        "&code=" + dataBase.getAccessCode() +
                        "&redirect_uri=" + dataBase.getREDIRECT_URI()))
                .build();

        dataBase.setAccessCode(null);

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String getRequest(ENDPOINTS endpoint, QUERY[] querey) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + dataBase.getAccessToken())
                .uri(URI.create(composeURI(dataBase.getAPI_SERVER_PATH() + endpoint.getEndpoint(), querey)))
                .GET()
                .build();
        System.out.println(composeURI(dataBase.getAPI_SERVER_PATH() + endpoint.getEndpoint(), querey));

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    public String getRequest(String uri) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().build();

        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + dataBase.getAccessToken())
                .uri(URI.create(uri))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private String composeURI(String uri, QUERY[] query) {
        if (query == null) return uri;

        uri += "?";
        for (var q : query) {
            uri += q.getQuery() + "=" + q.getParam() + "&";
        }

        return uri.substring(0, uri.length() - 1);
    }

    public void startServer() {
        server.start();
    }

    public void stopServer() {
        server.stop(1);
    }

    public boolean isAuthorized() {
        return dataBase.getAccessToken() != null;
    }

    public String getAccessCode() {
        return dataBase.getAccessCode();
    }

    public void setAccessToken(String accessToken) {
        dataBase.setAccessToken(accessToken);
    }


    public ACTION getLastSuccessfulAction() {
        return dataBase.getLastSuccessfulAction();
    }

    public void setLastSuccessfulAction(ACTION action) {
        dataBase.setLastSuccessfulAction(action);
    }

    public String getOtherPage(ACTION action) {
        if (action == ACTION.PREV) return dataBase.getPrev();
        if (action == ACTION.NEXT) return dataBase.getNext();
        return null;
    }

    public void setPrevPage(String prev) {
        dataBase.setPrev(prev);
    }

    public void setNextPage(String next) {
        dataBase.setNext(next);
    }

    public String getPageSize() {
        return dataBase.getPageSize();
    }
}
