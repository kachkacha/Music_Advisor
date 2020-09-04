package advisor;

import advisor.enums.ACTION;
import advisor.enums.ENDPOINTS;
import advisor.enums.JSONKEYS;
import advisor.enums.QUERY;

import java.io.IOException;

public class Controller {

    private final Model model;
    private final View view;

    public Controller(DataBase dataBase) throws IOException {
        this.model = new Model(dataBase);
        this.view = new View();
    }

    public void processInput(ACTION action, String argument) {
        switch (action) {
            case AUTH:
                auth();
                break;
            case NEW:
                newReleases();
                break;
            case FEATURED:
                featuredPlaylists();
                break;
            case CATEGORIES:
                categories();
                break;
            case PLAYLISTS:
                playlists(argument);
                break;
            case PREV:
            case NEXT:
                changePage(action);
                break;
            case EXIT:
                exit();
                model.stopServer();
                break;
        }
    }

    private void auth() {
        if (model.isAuthorized()) {
            outputMessage("already authorized");
            return;
        }

        outputMessage("use this link to request the access code: \n" +
                model.getAccessCodeURL() +
                "\nwaiting for code...");

        waitForUser : {
            for (int i = 0; i < 10; i++) {
                if (model.getAccessCode() != null) break waitForUser;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            outputMessage("---FAILURE---");
            return;
        }

        outputMessage("code received\n" + "making http request for access_token");

        try {
            String json = model.requestAccessToken();
            String error = view.errorCode(json);

            if (error != null) {
                outputMessage(error);
                return;
            }

            model.setAccessToken(view.getByKey(json, null, JSONKEYS.ACCESS_TOKEN));

            outputMessage("---SUCCESS---");

            updateHistory(null, null, null);
        } catch (IOException | InterruptedException e){
            outputMessage("---FAILURE---" + e.getMessage());
        }
    }

    private void newReleases() {
        if (!model.isAuthorized()) {
            outputMessage(notAuthorized());
            return;
        }

        try {
            String json = model.getRequest(ENDPOINTS.NEW_RELEASES, new QUERY[]{QUERY.OFFSET.setParam("0"), QUERY.LIMIT.setParam(model.getPageSize())});
            String error = view.errorCode(json);

            if (error != null) {
                outputMessage(error);
            }
            else {
                outputMessage(view.getNewReleases(json, model.getPageSize()));
                updateHistory(ACTION.NEW, view.getByKey(json, JSONKEYS.ALBUMS, JSONKEYS.PREVIOUS), view.getByKey(json, JSONKEYS.ALBUMS, JSONKEYS.NEXT));
            }
        } catch (IOException | InterruptedException e){
            outputMessage("---FAILURE---" + e.getMessage());
        }
    }

    private void featuredPlaylists() {
        if (!model.isAuthorized()) {
            outputMessage(notAuthorized());
            return;
        }

        try {
            String json = model.getRequest(ENDPOINTS.FEATURED_PLAYLISTS, new QUERY[]{QUERY.OFFSET.setParam("0"), QUERY.LIMIT.setParam(model.getPageSize())});
            String error = view.errorCode(json);

            if (error != null) {
                outputMessage(error);
            }
            else {
                outputMessage(view.getPlaylists(json, model.getPageSize()));
                updateHistory(ACTION.FEATURED, view.getByKey(json, JSONKEYS.PLAYLISTS, JSONKEYS.PREVIOUS), view.getByKey(json, JSONKEYS.PLAYLISTS, JSONKEYS.NEXT));
            }
        } catch (IOException | InterruptedException e){
            outputMessage("---FAILURE---" + e.getMessage());
        }
    }

    private void categories() {
        if (!model.isAuthorized()) {
            outputMessage(notAuthorized());
            return;
        }

        try {
            String json = model.getRequest(ENDPOINTS.CATEGORIES, new QUERY[]{QUERY.OFFSET.setParam("0"), QUERY.LIMIT.setParam(model.getPageSize())});
            String error = view.errorCode(json);

            if (error != null) {
                outputMessage(error);
            }
            else {
                outputMessage(view.getCategories(json, model.getPageSize()));
                updateHistory(ACTION.CATEGORIES, view.getByKey(json, JSONKEYS.CATEGORIES, JSONKEYS.PREVIOUS), view.getByKey(json, JSONKEYS.CATEGORIES, JSONKEYS.NEXT));
            }
        } catch (IOException | InterruptedException e){
            outputMessage("---FAILURE---" + e.getMessage());
        }
    }

    private void playlists(String category) {
        if (!model.isAuthorized()) {
            outputMessage(notAuthorized());
            return;
        }

        try {
            String categoryId = view.getCategoryId(model.getRequest(ENDPOINTS.CATEGORIES, new QUERY[]{QUERY.LIMIT.setParam("50"), QUERY.OFFSET.setParam("0")}), category);

            String json = model.getRequest(ENDPOINTS.PLAYLISTS.setCategoryId(categoryId), null);
            String error = view.errorCode(json);

            if (error != null) {
                outputMessage(error);
            }
            else {
                outputMessage(view.getPlaylists(json, model.getPageSize()));
                updateHistory(ACTION.PLAYLISTS, view.getByKey(json, JSONKEYS.PLAYLISTS, JSONKEYS.PREVIOUS), view.getByKey(json, JSONKEYS.PLAYLISTS, JSONKEYS.NEXT));
            }
        } catch (IOException | InterruptedException e){
            outputMessage("---FAILURE---" + e.getMessage());
        }
    }

    private void changePage(ACTION action) {

        String uri = model.getOtherPage(action);
        if (uri == null) {
            outputMessage("No more pages.");
            return;
        }

        try {
            String json = model.getRequest(uri);
            String error = view.errorCode(json);

            if (error != null) outputMessage(error);
            else {
                JSONKEYS jsonobjkey = null;
                switch (model.getLastSuccessfulAction()) {
                    case NEW:
                        outputMessage(view.getNewReleases(json, model.getPageSize()));
                        jsonobjkey = JSONKEYS.ALBUMS;
                        break;
                    case CATEGORIES:
                        outputMessage(view.getCategories(json, model.getPageSize()));
                        jsonobjkey = JSONKEYS.CATEGORIES;
                        break;
                    case FEATURED:
                    case PLAYLISTS:
                        outputMessage(view.getPlaylists(json, model.getPageSize()));
                        jsonobjkey = JSONKEYS.PLAYLISTS;
                        break;
                }
                updateHistory(model.getLastSuccessfulAction(),
                        view.getByKey(json, jsonobjkey, JSONKEYS.PREVIOUS),
                        view.getByKey(json, jsonobjkey, JSONKEYS.NEXT));
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void exit() {
        outputMessage("---GOODBYE!---");
        model.stopServer();
    }

    public void updateHistory(ACTION action, String prev, String next) {
        model.setLastSuccessfulAction(action);
        model.setPrevPage(prev);
        model.setNextPage(next);
    }

    private String notAuthorized() {
        return "Please, provide access for application.";
    }

    private void outputMessage(String message) {
        Main.outputMessage(message);
    }
}