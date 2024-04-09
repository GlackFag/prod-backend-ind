package com.glackfag.travelgentle.util.telegram;

public interface Commands {
    String START = "/start";
    String ME = "/me";
    String SKIP = "Skip";
    String CANCEL = "Cancel";
    String SUGGEST_SIGHT = "/suggestsight";
    String NEXT_SUGGESTION = "/nextSuggestion?city=%s&ignore=%s";

    //IF COMMAND CONTAINS 'page' PARAMETER IT MUST BE FIRST
    interface Travel {
        String NEW_TRAVEL = "/newtravel";
        String MY_TRAVELS = "/mytravels";
        String CONFIRM_DELETE_TRAVEL_ID = "/confirmDeleteTravel?id=";
        String DELETE_TRAVEL_ID = "/deleteTravel?id=";
        String CANCEL_DELETION = "/cancelDeletion";
        String INVITE = "/invite";
        String JOIN_TRAVEL = "/jointravel";
        String SAVE_TRAVEL = "/saveTravel";
        String ANOTHER_POINT = "/anotherPoint";

        String INDEX_PAGE = "/indexTravel?page=%d";
        String INDEX_ID = "/indexTravel?id=";

        String INVITE_TRAVEL_ID = "/inviteTravel?id=";
    }

    interface IntermediatePoint {
        String INDEX_ID = "/indexPoint?id=%d&pageFromNumber=%d";
        String INDEX_PAGE = "/indexPoint?page=%d&travelId=%d";
        String INDEX_BY_TRAVEL_ID = "/indexPoint?travelId=";

    }

    interface Person {
        String INDEX_ID = "/indexPerson?id=%d&pageFromNumber=%d&travelId=%d";
        String INDEX_PARTICIPANTS = "/indexPerson?page=%d&travelId=%d";
        String INDEX_BY_TRAVEL_ID = "/indexPerson?travelId=";
        String EDIT_NAME = "/editName";
        String EDIT_AGE = "/editAge";
        String EDIT_BIO = "/editBio";
        String EDIT_HOME_CITY = "/editHomeCity";
    }

    interface Registration {
        String CANCEL = "Cancel registration";
    }
}
