package db;
import fileio.ActorInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import fileio.UserInputData;
import fileio.ActionInputData;
import org.json.simple.JSONArray;
import actions.Action;
import actions.Command;
import actions.Query;
import actions.Recommendation;
import actor.Actor;
import entertainment.Movie;
import entertainment.Serial;
import entities.User;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class Database {
    private final List<Movie> movies = new ArrayList<>();
    private final List<Serial> serials = new ArrayList<>();
    private final List<Actor> actors = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Action> actions = new ArrayList<>();

    public List<Movie> getMovies() {
        return movies;
    }

    public List<Serial> getSerials() {
        return serials;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Action> getActions() {
        return actions;
    }

    public Database(final List<MovieInputData> movies, final List<SerialInputData> serials,
                    final List<ActorInputData> actors, final List<UserInputData> users,
                    final List<ActionInputData> actions) {
        for (MovieInputData movie: movies) {
            this.movies.add(new Movie(movie.getTitle(), movie.getCast(), movie.getGenres(),
                    movie.getYear(), movie.getDuration()));
        }

        for (SerialInputData serial : serials) {
            this.serials.add(new Serial(serial.getTitle(), serial.getYear(), serial.getGenres(),
                    serial.getCast(), serial.getSeasons(), serial.getNumberSeason()));
        }

        for (ActorInputData actor: actors) {
            this.actors.add(new Actor(actor.getName(), actor.getCareerDescription(),
                    actor.getFilmography(), actor.getAwards()));
        }

        for (UserInputData user : users) {
            this.users.add(new User(user.getUsername(), user.getSubscriptionType(),
                    user.getHistory(), user.getFavoriteMovies()));
        }

        for (ActionInputData action: actions) {
            switch (action.getActionType()) {
                case "command" -> this.actions.add(new Command(action.getActionId(),
                        action.getActionType(), action.getType(), action.getUsername(),
                        action.getTitle(), action.getGrade(), action.getSeasonNumber()));
                case "query" -> this.actions.add(new Query(action.getActionId(),
                        action.getActionType(), action.getObjectType(), action.getSortType(),
                        action.getCriteria(), action.getNumber(), action.getFilters()));
                case "recommendation" -> this.actions.add(new Recommendation(action.getActionId(),
                        action.getActionType(), action.getType(), action.getUsername(),
                        action.getGenre()));
                default -> {
                }
            }
        }
    }


    public User searchUserByName(final String username) {
        for (User user : this.users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }

    public Movie searchMovieByName(final String movieName) {
        for (Movie movie: this.movies) {
            if (movie.getTitle().equals(movieName)) {
                return movie;
            }
        }
        return null;
    }

    public Serial searchSerialByName(final String serialName) {
        for (Serial serial: this.serials) {
            if (serial.getTitle().equals(serialName)) {
                return serial;
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public JSONArray executeActions() {
        JSONArray testResult = new JSONArray();
        for (Action action : this.actions) {
            JSONObject data = new JSONObject();
            data.put("id", action.getActionId());
            data.put("message", action.run(this));
            testResult.add(data);
        }
        return testResult;
    }
}

