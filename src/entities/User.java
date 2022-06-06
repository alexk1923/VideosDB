package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class User {
    private final String username;
    private final String subscriptionType;
    private final Map<String, Integer> history;
    private final ArrayList<String> favoriteMovies;
    private Map<String, Double> ratings = new HashMap<>();

    public User(User sourceUser) {
        this.username = sourceUser.username;
        this.subscriptionType = sourceUser.subscriptionType;
        this.history = sourceUser.history;
        this.favoriteMovies = sourceUser.favoriteMovies;
        this.ratings = sourceUser.ratings;
    }

    public User(final String username, final String subscriptionType,
                final Map<String, Integer> history, final ArrayList<String> favoriteMovies) {
        this.username = username;
        this.subscriptionType = subscriptionType;
        this.history = history;
        this.favoriteMovies = favoriteMovies;
    }

    public String getUsername() {
        return username;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public Map<String, Integer> getHistory() {
        return history;
    }

    public Map<String, Double> getRatings() {
        return ratings;
    }

    public ArrayList<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    @Override
    public String toString() {
        return "User{" + "username='" + username + '\''
                + ", subscriptionType='" + subscriptionType + '\''
                + ", history=" + history
                + ", favoriteMovies=" + favoriteMovies
                + '}';
    }

}
