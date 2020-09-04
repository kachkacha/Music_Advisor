package advisor.enums;

public enum JSONKEYS {
    ACCESS_TOKEN("access_token"),
    ALBUMS("albums"),
    ITEMS("items"),
    NAME("name"),
    EXTERNAL_URLS("external_urls"),
    ARTISTS("artists"),
    SPOTIFY("spotify"),
    PLAYLISTS("playlists"),
    CATEGORIES("categories"),
    ERROR("error"),
    ERROR_DESCRIPTION("error_description"),
    MESSAGE("message"),
    ID("id"),
    NEXT("next"),
    PREVIOUS("previous"),
    OFFSET("offset"),
    TOTAL("total");

    private final String key;

    JSONKEYS(String key) {this.key = key;}

    public String getKey() {return key;}
}
