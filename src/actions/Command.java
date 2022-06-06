package actions;

import common.Constants;
import db.Database;
import entertainment.Movie;
import entertainment.Serial;
import entities.User;


public final class Command extends Action {
    private final String type;
    private final String username;
    private final String title;
    private final Double grade;
    private final int seasonNumber;

    public Command(final int actionId, final String actionType, final String type,
                   final String username, final String title,
                   final Double grade, final int seasonNumber) {
        super(actionId, actionType);
        this.type = type;
        this.username = username;
        this.title = title;
        this.grade = grade;
        this.seasonNumber = seasonNumber;
    }

    @Override
    public String toString() {
        return "Command{"
                + "type='" + type + '\''
                + ", username='" + username
                + '\'' + ", title='" + title + '\''
                + ", grade=" + grade + ", seasonNumber="
                + seasonNumber + "}\n";
    }

    /**  Apply the command depending on its type
     *
     * @param db main Database
     * @return command result
     */
    public String run(final Database db) {
        return switch (this.type) {
            case Constants.FAVORITE -> this.favorite(db);
            case Constants.VIEW -> this.view(db);
            case Constants.RATING -> this.rating(db);
            default -> ("Invalid command");
        };
    }

    /** Execute a favorite type command
     *
     * @param db main Database
     * @return command result
     */
    public String favorite(final Database db) {
        User user = db.searchUserByName(this.username);
        if (user == null) {
            return ("error ->" + this.username + "doesn't exist in the database !");
        }
        Movie newMovie = db.searchMovieByName(this.title);
        Serial serial = db.searchSerialByName(this.title);

        if (newMovie == null && serial == null) {
                return ("error -> " + this.title + " doesn't exist in the videos database");
        }

        if (!user.getHistory().containsKey(this.title)) {
            return ("error -> " + this.title + " is not seen");
        }
        if (user.getFavoriteMovies().contains(this.title)) {
            return ("error -> " + this.title + " is already in favourite list");
        }

        user.getFavoriteMovies().add(this.title);
        return ("success -> " + this.title + " was added as favourite");
    }

    /** Execute a view type command
     *
     * @param db main Database
     * @return command result
     */
    public String view(final Database db) {
        User user = db.searchUserByName(this.username);
        if (user == null) {
            return ("error ->" + this.username + "doesn't exist in the database !");
        }
        Movie movie = db.searchMovieByName(this.title);
        Serial serial = db.searchSerialByName(this.title);
        if (movie == null && serial == null) {
            return ("error -> " + this.title + " nu exista in baza de date de SHOWS");
        }

        if (user.getHistory().containsKey(this.title)) {
            user.getHistory().replace(this.title, user.getHistory().get(this.title) + 1);
        } else {
            user.getHistory().put(this.title, 1);
        }

        return ("success -> " + this.title + " was viewed with total views of "
                + user.getHistory().get(this.title));

    }

    /** Execute a rating type command
     *
     * @param db main Database
     * @return command result
     */
    public String rating(final Database db) {
        User user = db.searchUserByName(this.username);
        if (user == null) {
            return ("error -> " + this.username + " doesn't exist in the USER DATABASE");
        } else {
            // Daca e film (movie)
            if (this.seasonNumber == 0) {
                Movie movie = db.searchMovieByName(this.title);
                if (movie == null) {
                    return ("error -> " + this.title + " nu exista in baza de date MOVIES");
                }

                if (!user.getHistory().containsKey(this.title)) {
                    return ("error -> " + this.title + " is not seen");
                }

                if (user.getRatings().containsKey(this.title)) {
                    return ("error -> " + this.title + " has been already rated");
                } else {
                    user.getRatings().put(this.title, this.grade);
                    movie.getRatings().add(this.grade);
                    return ("success -> " + this.title + " was rated with " + this.grade
                            + " by " + this.username);
                }
                // daca e serial (serial)
            } else {
                Serial serial = db.searchSerialByName(this.title);
                if (serial == null) {
                    return ("error -> " + this.title + "nu exista in baza de date SERIALS");
                }

                if (!user.getHistory().containsKey(this.title)) {
                    return ("error -> " + this.title + " is not seen");
                }
                if (user.getRatings().containsKey(this.title + " " + this.seasonNumber)) {
                    return ("error -> " + this.title + " has been already rated");
                } else {
                    user.getRatings().put(this.title + " " + this.seasonNumber, this.grade);
                    serial.getSeasonsList().get(this.seasonNumber - 1)
                            .getRatings().add(this.grade);
                    serial.updateRating();
                    return ("success -> " + this.title + " was rated with " + this.grade
                            + " by " + this.username);
                }
            }
        }
    }
}
