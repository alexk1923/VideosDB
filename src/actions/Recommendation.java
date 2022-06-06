package actions;

import common.Constants;
import db.Database;
import entertainment.Genre;
import entertainment.Show;
import entities.User;
import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public final class Recommendation extends Action {
    private final String type;
    private final String username;
    private final String genre;

    public Recommendation(final int actionId, final String actionType, final String type,
                          final String username, final String genre) {
        super(actionId, actionType);
        this.type = type;
        this.username = username;
        this.genre = genre;
    }

    @Override
    public String toString() {
        return "Recommendation{"
                + "type='" + type + '\''
                + ", username='" + username
                + '\'' + ", genre='" + genre
                + '\'' + "} " + super.toString();
    }

    @Override
    public String run(final Database db) {
            return switch (this.type) {
                case Constants.STANDARD -> ("StandardRecommendation " + this.standard(db));
                case Constants.BEST_UNSEEN -> ("BestRatedUnseenRecommendation "
                        + this.bestUnseen(db));
                case Constants.POPULAR -> ("PopularRecommendation " + this.popular(db));
                case Constants.FAVORITE -> ("FavoriteRecommendation " + this.favorite(db));
                case Constants.SEARCH -> ("SearchRecommendation " + this.search(db));
                default -> (this.type + "Recommendation cannot be applied");
            };
    }

    private int compareShows(final Show s1, final Show s2, final int compare) {
        if (compare != 0) {
            return compare;
        } else {
            return (s1.getTitle().compareTo(s2.getTitle()));
        }
    }

    private String standard(final Database db) {
        User user = db.searchUserByName(this.username);
        ArrayList<Show> showsList = Show.copyAllVideos(db);

        for (Show show : showsList) {
            if (!user.getHistory().containsKey(show.getTitle())) {
                return ("result: " + show.getTitle());
            }
        }
        return "cannot be applied!";
    }

    private String bestUnseen(final Database db) {
        User user = db.searchUserByName(this.username);
        ArrayList<Show> unseenShowList = Show.copyUnseenVideos(user, db);
        unseenShowList.sort(Comparator.comparingDouble(Show::getRating));

        if (unseenShowList.size() == 0) {
            return ("cannot be applied!");
        }

        /* Check if all movies have 0 rating */
        if (unseenShowList.get(unseenShowList.size() - 1).getRating() == 0) {
            return ("result: " + unseenShowList.get(0).getTitle());
        }

        return ("result: " + unseenShowList.get(unseenShowList.size() - 1).getTitle());
    }

    private String popular(final Database db) {

        User user = db.searchUserByName(this.username);
        if (!user.getSubscriptionType().equals(Constants.PREMIUM)) {
            return ("cannot be applied!");
        }

        ArrayList<Show> showsList = Show.copyAllVideos(db);

        ArrayList<Genre> popularGenres = new ArrayList<>();
        Collections.addAll(popularGenres, Genre.values());
        popularGenres.sort((g1, g2) ->
                Show.getPopularityCoeff(db, showsList, g2)
                        - Show.getPopularityCoeff(db, showsList, g1));

        ArrayList<Show> unseenShowsList = Show.copyUnseenVideos(user, db);

        if (unseenShowsList.size() == 0) {
            return ("cannot be applied!");
        }

        for (Genre g : popularGenres) {
            for (Show unseenShow : unseenShowsList) {
                for (String unseenShowGenre : unseenShow.getGenre()) {
                    if (Utils.stringToGenre(unseenShowGenre).equals(g)) {
                        return ("result: " + unseenShow.getTitle());
                    }
                }
            }
        }

        return ("cannot be applied!");
    }

    private boolean checkSearchGenreIsValid() {
        boolean ok = false;
        for (Genre g : Genre.values()) {
            if (g.equals(Utils.stringToGenre(this.genre))) {
                ok = true;
                break;
            }
        }
        return ok;
    }

    private String search(final Database db) {

        User user = db.searchUserByName(this.username);
        if (!user.getSubscriptionType().equals(Constants.PREMIUM)) {
            return ("cannot be applied!");
        }

        ArrayList<Show> unseenShowsList = Show.copyUnseenVideos(user, db);


        if (!checkSearchGenreIsValid()) {
            return ("cannot be applied!");
        }

        unseenShowsList.removeIf(show -> !show.getGenre().contains(this.genre));
        unseenShowsList.sort((s1, s2) -> {
            final int compare = Double.compare(s1.getRating(), s2.getRating());
            return compareShows(s1, s2, compare);
        });


        if (unseenShowsList.size() == 0) {
            return ("cannot be applied!");
        }

        String res = unseenShowsList.stream().map(Show::getTitle)
                .collect(Collectors.joining(", "));
        return ("result: [" + res + "]");
    }


    private String favorite(final Database db) {

        User user = db.searchUserByName(this.username);

        if (!user.getSubscriptionType().equals(Constants.PREMIUM)) {
            return ("cannot be applied!");
        }

        ArrayList<Show> unseenShowsList = Show.copyUnseenVideos(user, db);

        unseenShowsList.sort((s1, s2) -> {
            final int compare = Double.compare(s1.getFavoriteApps(db), s2.getFavoriteApps(db));
            if (compare == 0) {
                return -1;
            }
            return compare;
        });

        unseenShowsList.removeIf(show -> show.getFavoriteApps(db) == 0);

        if (unseenShowsList.size() == 0) {
            return ("cannot be applied!");
        }

        return ("result: " + (unseenShowsList.get(unseenShowsList.size() - 1).getTitle()));
    }
}
