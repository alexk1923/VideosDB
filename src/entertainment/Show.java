package entertainment;

import common.Constants;
import db.Database;
import entities.User;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class Show {
    private final String title;
    private final Integer yearOfRelease;
    private final ArrayList<String> genre;
    private final ArrayList<String> cast;

    public Show(Show sourceShow) {
        this.title = sourceShow.title;
        this.yearOfRelease = sourceShow.yearOfRelease;
        this.genre = new ArrayList<>(sourceShow.genre);
        this.cast = new ArrayList<>(sourceShow.cast);
    }

    public Show(final String title, final Integer yearOfRelease, final ArrayList<String> genre,
                final ArrayList<String> cast) {
        this.title = title;
        this.yearOfRelease = yearOfRelease;
        this.genre = genre;
        this.cast = cast;
    }


    public final String getTitle() {
        return title;
    }
    public final Integer getYearOfRelease() {
        return yearOfRelease;
    }
    public final ArrayList<String> getCast() {
        return cast;
    }
    public final ArrayList<String> getGenre() {
        return genre;
    }


    @Override
    public String toString() {
        return "Show{"
                + "title='" + title + '\''
                + ", yearOfRelease=" + yearOfRelease
                + ", genre=" + genre
                + ", cast=" + cast + '}';
    }

    public boolean hasGivenFilter(final List<String> genres) {
        if (genres.get(0) == null) {
            return false;
        }

        for (String givenGenre : genres) {
            if (this.genre.contains(givenGenre)) {
                return false;
            }
        }
        return true;
    }

    public abstract double getRating();
    public abstract int getDuration();

    public int getFavoriteApps(final Database db) {
        int counterFavorites = 0;
        List<User> users = db.getUsers();
        for (User user : users) {
            if (user.getFavoriteMovies().contains(this.title)) {
                counterFavorites++;
            }
        }
        return counterFavorites;
    }

    public int getViews(final Database db) {
        int counterViews = 0;
        List<User> users = db.getUsers();
        for (User user : users) {
            if (user.getHistory().containsKey(this.title)) {
                counterViews += user.getHistory().get(this.title);
            }
        }
        return counterViews;
    }

    public static ArrayList<Show> copyVideosByObjectType(final String objectType,
                                                         final Database db) {
        ArrayList<Show> showsList = new ArrayList<>();

        if (objectType.equals(Constants.MOVIES)) {
            for (Movie movieSource : db.getMovies()) {
                showsList.add(new Movie(movieSource));
            }
        }

        if (objectType.equals(Constants.SERIALS) || objectType.equals(Constants.SHOWS)) {
            for (Serial serialSource : db.getSerials()) {
                showsList.add(new Serial(serialSource));
            }
        }

        return showsList;
    }

    public static ArrayList<Show> copyAllVideos(final Database db) {
        ArrayList<Show> showsList = new ArrayList<>();

        for (Movie movieSource : db.getMovies()) {
                showsList.add(new Movie(movieSource));
        }

        for (Serial serialSource : db.getSerials()) {
                showsList.add(new Serial(serialSource));
        }

        return showsList;
    }


    public static ArrayList<Show> copyUnseenVideos(final User user, final Database db) {
        ArrayList<Show> showsList = new ArrayList<>();

        for (Movie movieSource : db.getMovies()) {
            if (!user.getHistory().containsKey(movieSource.getTitle())) {
                showsList.add(new Movie(movieSource));
            }
        }

        for (Serial serialSource : db.getSerials()) {
            if (!user.getHistory().containsKey(serialSource.getTitle())) {
                showsList.add(new Serial(serialSource));
            }
        }

        return showsList;
    }


    public static int getPopularityCoeff(final Database db, final ArrayList<Show> showsList,
                                         final Genre genre) {
        int popularityCoeff = 0;
        for (Show show : showsList) {
            for (String showGenre : show.getGenre()) {
                if (Utils.stringToGenre(showGenre).equals(genre)) {
                    popularityCoeff += show.getViews(db);
                }
            }
        }
        return popularityCoeff;
    }

}
