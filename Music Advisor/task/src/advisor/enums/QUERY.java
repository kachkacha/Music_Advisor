package advisor.enums;

public enum QUERY {
    CODE("code"),
    OFFSET("offset"),
    LIMIT("limit");

    private final String query;
    private String param;

    QUERY(String query) {
        this.query = query;
    }

    public QUERY setParam(String param) {
        this.param = param;
        return this;
    }

    public String getParam() {
        return param;
    }

    public String getQuery() {
        return query;
    }
}
