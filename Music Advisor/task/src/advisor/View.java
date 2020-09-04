package advisor;

import advisor.enums.JSONKEYS;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class View {

    private void build(StringBuilder stringBuilder, String arg) {
        stringBuilder.append(arg);
    }

    public String errorCode(String json) {
        JsonObject element = JsonParser.parseString(json)
                .getAsJsonObject();

        JsonElement elem = element.get(JSONKEYS.ERROR_DESCRIPTION.getKey());
        if (elem != null)
            return elem.getAsString();

        if (element.get(JSONKEYS.ERROR.getKey()) == null) return null;

        return element.getAsJsonObject(JSONKEYS.ERROR.getKey())
                .get(JSONKEYS.MESSAGE.getKey())
                .getAsString();
    }

    public String getByKey(String json, JSONKEYS jsonobjkey, JSONKEYS jsonkey) {
        if (jsonobjkey == null) {
            JsonElement element = JsonParser.parseString(json)
                    .getAsJsonObject()
                    .get(jsonkey.getKey());
            return element.isJsonNull() ? null : element.getAsString();
        }

        else {
            JsonElement element = JsonParser.parseString(json)
                    .getAsJsonObject()
                    .getAsJsonObject(jsonobjkey.getKey())
                    .get(jsonkey.getKey());
            return element.isJsonNull() ? null : element.getAsString();
        }
    }

    public String getNewReleases(String json, String pageSize) {
        StringBuilder stringBuilder = new StringBuilder();

        JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonObject(JSONKEYS.ALBUMS.getKey())
                .getAsJsonArray(JSONKEYS.ITEMS.getKey())
                .forEach(jsonElement -> {
                    build(stringBuilder, jsonElement.getAsJsonObject()
                            .get(JSONKEYS.NAME.getKey())
                            .getAsString() + "\n[");
                    jsonElement.getAsJsonObject()
                            .getAsJsonArray(JSONKEYS.ARTISTS.getKey())
                            .forEach(jsonElement1 -> build(stringBuilder, jsonElement1.getAsJsonObject()
                                    .get(JSONKEYS.NAME.getKey())
                                    .getAsString() + ", "));
                    stringBuilder.delete(stringBuilder.length() -2, stringBuilder.length());
                    build(stringBuilder, "]\n" + jsonElement.getAsJsonObject()
                            .getAsJsonObject(JSONKEYS.EXTERNAL_URLS.getKey())
                            .get(JSONKEYS.SPOTIFY.getKey())
                            .getAsString() + "\n\n");
                });

        stringBuilder.append("---PAGE " +
                (JsonParser.parseString(json)
                        .getAsJsonObject()
                        .getAsJsonObject(JSONKEYS.ALBUMS.getKey())
                        .get(JSONKEYS.OFFSET.getKey())
                        .getAsInt() / Integer.parseInt(pageSize) + 1)
                + " OF " +
                (int) Math.ceil(JsonParser.parseString(json)
                        .getAsJsonObject()
                        .getAsJsonObject(JSONKEYS.ALBUMS.getKey())
                        .get(JSONKEYS.TOTAL.getKey())
                        .getAsInt() / (double) Integer.parseInt(pageSize)) + "---");

        return stringBuilder.toString();
    }

    public String getPlaylists(String json, String pageSize) {
        StringBuilder stringBuilder = new StringBuilder();

        JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonObject(JSONKEYS.PLAYLISTS.getKey())
                .getAsJsonArray(JSONKEYS.ITEMS.getKey())
                .forEach(jsonElement -> {
                    build(stringBuilder, jsonElement.getAsJsonObject()
                            .get(JSONKEYS.NAME.getKey())
                            .getAsString() + "\n");
                    build(stringBuilder, jsonElement.getAsJsonObject()
                            .getAsJsonObject(JSONKEYS.EXTERNAL_URLS.getKey())
                            .get(JSONKEYS.SPOTIFY.getKey())
                            .getAsString() + "\n\n");
                });

        stringBuilder.append("---PAGE " +
                (JsonParser.parseString(json)
                        .getAsJsonObject()
                        .getAsJsonObject(JSONKEYS.PLAYLISTS.getKey())
                        .get(JSONKEYS.OFFSET.getKey())
                        .getAsInt() / Integer.parseInt(pageSize) + 1)
                + " OF " +
                (int) Math.ceil(JsonParser.parseString(json)
                        .getAsJsonObject()
                        .getAsJsonObject(JSONKEYS.PLAYLISTS.getKey())
                        .get(JSONKEYS.TOTAL.getKey())
                        .getAsInt() / (double) Integer.parseInt(pageSize)) + "---");


        return stringBuilder.toString();
    }

    public String getCategories(String json, String pageSize) {
        StringBuilder stringBuilder = new StringBuilder();

        JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonObject(JSONKEYS.CATEGORIES.getKey())
                .getAsJsonArray(JSONKEYS.ITEMS.getKey())
                .forEach(jsonElement -> build(stringBuilder, jsonElement.getAsJsonObject()
                        .get(JSONKEYS.NAME.getKey())
                        .getAsString() + "\n"));

        stringBuilder.append("---PAGE " +
                (JsonParser.parseString(json)
                        .getAsJsonObject()
                        .getAsJsonObject(JSONKEYS.CATEGORIES.getKey())
                        .get(JSONKEYS.OFFSET.getKey())
                        .getAsInt() / Integer.parseInt(pageSize) + 1)
                + " OF " +
                (int) Math.ceil(JsonParser.parseString(json)
                        .getAsJsonObject()
                        .getAsJsonObject(JSONKEYS.CATEGORIES.getKey())
                        .get(JSONKEYS.TOTAL.getKey())
                        .getAsInt() / (double) Integer.parseInt(pageSize)) + "---");

        return stringBuilder.toString();
    }

    public String getCategoryId(String json, String category) {
        StringBuilder stringBuilder = new StringBuilder();

        JsonParser.parseString(json)
                .getAsJsonObject()
                .getAsJsonObject(JSONKEYS.CATEGORIES.getKey())
                .getAsJsonArray(JSONKEYS.ITEMS.getKey())
                .forEach(jsonElement -> {
                    if (jsonElement.getAsJsonObject()
                            .get(JSONKEYS.NAME.getKey())
                            .getAsString()
                            .equals(category))
                        build(stringBuilder, jsonElement.getAsJsonObject().get(JSONKEYS.ID.getKey()).getAsString());
                });

        return stringBuilder.toString();
    }
    
}
