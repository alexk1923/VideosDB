package entertainment;

import java.util.ArrayList;
import java.util.List;

public final class Serial extends Show {
    private final ArrayList<Season> seasonsList;
    private final int numberOfSeasons;
    private double averageRating = 0.00;

    public Serial(final Serial sourceSerial) {
        super(sourceSerial);
        this.seasonsList = new ArrayList<>(sourceSerial.seasonsList);
        this.numberOfSeasons = sourceSerial.numberOfSeasons;
        this.averageRating = sourceSerial.averageRating;
    }

    public Serial(final String title, final Integer yearOfRelease, final ArrayList<String> genre,
                  final ArrayList<String> cast, final ArrayList<Season> seasonsList,
                  final int numberOfSeasons) {
        super(title, yearOfRelease, genre, cast);
        this.seasonsList = seasonsList;
        this.numberOfSeasons = numberOfSeasons;
    }

    @Override
    public String toString() {
        return "Serial{" + "seasonsList="
                + seasonsList + ", numberOfSeasons="
                + numberOfSeasons + ", ratings="
                + averageRating + "} " + super.toString() + "\n";
    }

    public ArrayList<Season> getSeasonsList() {
        return seasonsList;
    }

    public double getRating() {
        return averageRating;
    }

    public double calcSeasonAverageRating(Season season) {
        List<Double> seasonRatings =  season.getRatings();
        double seasonTotalRating = 0;
        for (double rating : seasonRatings) {
            seasonTotalRating += rating;
        }

        return seasonRatings.size() > 0 ? seasonTotalRating / seasonRatings.size() : 0;
    }

    public void updateRating() {
        double totalSeasonsRating = 0;
        for (Season season : this.seasonsList) {
            totalSeasonsRating += calcSeasonAverageRating(season);
        }

        if (this.seasonsHaveRating() != 0) {
            this.averageRating = totalSeasonsRating / this.seasonsList.size();
        }
    }

    public int seasonsHaveRating() {
        int counter = 0;
        for (Season season : this.seasonsList) {
            if (Double.compare(calcSeasonAverageRating(season), 0) != 0) {
                counter++;
            }
        }

        return counter;
    }

    @Override
    public int getDuration() {
        int duration = 0;
        for (Season season : this.seasonsList) {
            duration += season.getDuration();
        }
        return duration;
    }


}
