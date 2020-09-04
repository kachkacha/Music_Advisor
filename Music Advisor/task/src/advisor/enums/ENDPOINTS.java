package advisor.enums;

public enum ENDPOINTS {
    AUTHORIZE("/authorize"),
    ACCESS_TOKEN("/api/token"),
    NEW_RELEASES("/v1/browse/new-releases"),
    CATEGORIES("/v1/browse/categories"),
    FEATURED_PLAYLISTS("/v1/browse/featured-playlists"),
    PLAYLISTS("/v1/browse/categories/{category_id}/playlists");

    private String endpoint;

    ENDPOINTS(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public ENDPOINTS setCategoryId(String categoryId) {
        endpoint = endpoint.replace("{category_id}", categoryId);
        return this;
    }
}
