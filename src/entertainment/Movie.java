package entertainment;

import java.util.ArrayList;

public final class Movie extends Show {
    private final int duration;
    private ArrayList<Double> ratings = new ArrayList<>();

    public Movie(final Movie sourceMovie) {
        super(sourceMovie);
        this.duration = sourceMovie.duration;
        this.ratings = new ArrayList<>(sourceMovie.ratings);
    }


    public Movie(final String title, final ArrayList<String> cast, final ArrayList<String> genres,
                 final int year, final int duration) {
        super(title, year, genres, cast);
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Movie{" + "duration=" + duration
                + ", ratings=" + ratings + "} super:"
                + super.toString() + "\n";
    }

    public int getDuration() {
        return duration;
    }

    public ArrayList<Double> getRatings() {
        return ratings;
    }

    public double getRating() {
        double totalRating = 0;
        for (double rating : ratings) {
            totalRating += rating;
        }
        if (ratings.size() == 0) {
            return 0;
        } else {
            return totalRating / ratings.size();
        }
    }


}
