package advisor;

import advisor.enums.ACTION;

public class DataBase {

    private int PORT = 8080;
    private String CLIENT_ID = "8a6ab0488d2e4821a92e890bdf176b5d";
    private String CLIENT_SECRET = "97da1b626d37462c9236c2f6aa2bcbca";
    private String REDIRECT_URI = "http://localhost:" + PORT;
    private String DEFAULT_AUTHORIZATION_SERVER_PATH = "https://accounts.spotify.com";
    private String AUTHORIZATION_SERVER_PATH;
    private String DEFAULT_API_SERVER_PATH = "https://api.spotify.com";
    private String API_SERVER_PATH;

    private String accessCode = null;
    private String accessToken = null;
    private String prev = null;
    private String next = null;
    private ACTION lastSuccessfulAction = null;
    private String defaultPageSize = "5";
    private String pageSize = null;

    public DataBase(String[] args) {
        String access = null, resource = null, pageSize = null;
        for (int i = 0; i < args.length; i += 2) {
            if ("-access".equals(args[i])) access = args[i + 1];
            else if ("-resource".equals(args[i])) resource = args[i + 1];
            else if ("-page".equals(args[i])) pageSize = args[i + 1];
        }
        AUTHORIZATION_SERVER_PATH = access == null ? DEFAULT_AUTHORIZATION_SERVER_PATH : access;
        API_SERVER_PATH = resource == null ? DEFAULT_API_SERVER_PATH : resource;
        this.pageSize = pageSize == null ? defaultPageSize : pageSize;
    }

    public int getPORT() {
        return PORT;
    }

    public String getCLIENT_ID() {
        return CLIENT_ID;
    }

    public String getCLIENT_SECRET() {
        return CLIENT_SECRET;
    }

    public String getREDIRECT_URI() {
        return REDIRECT_URI;
    }

    public String getAUTHORIZATION_SERVER_PATH() {
        return AUTHORIZATION_SERVER_PATH;
    }

    public String getAPI_SERVER_PATH() {
        return API_SERVER_PATH;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getPrev() {
        return prev;
    }

    public void setPrev(String prev) {
        this.prev = prev;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public ACTION getLastSuccessfulAction() {
        return lastSuccessfulAction;
    }

    public void setLastSuccessfulAction(ACTION lastSuccessfulAction) {
        this.lastSuccessfulAction = lastSuccessfulAction;
    }

    public String getPageSize() {
        return pageSize;
    }
}
