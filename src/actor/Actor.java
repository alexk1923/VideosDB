package actor;

import db.Database;
import entertainment.Movie;
import entertainment.Serial;
import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Actor {
    private final String name;
    private final String careerDescription;
    private final ArrayList<String> filmography;
    private final Map<ActorsAwards, Integer> actorsAwards;
    private double actorShowsAverage = 0.0;

    public Actor(final Actor sourceActor) {
        this.name = sourceActor.name;
        this.careerDescription = sourceActor.careerDescription;
        this.filmography = new ArrayList<>(sourceActor.filmography);
        this.actorsAwards = new HashMap<>(sourceActor.actorsAwards);
        this.actorShowsAverage = sourceActor.getActorShowsAverage();
    }

    public Actor(final String name, final String careerDescription,
                 final ArrayList<String> filmography,
                 final Map<ActorsAwards, Integer> actorsAwards) {
        this.name = name;
        this.careerDescription = careerDescription;
        this.filmography = filmography;
        this.actorsAwards = actorsAwards;
    }

    @Override
    public String toString() {
        return "Actor{"
                + "name='" + name + '\''
                + ", filmography=" + filmography
                + ", actorsAwards=" + actorsAwards
                + ", actorsShowsAverage=" + actorShowsAverage
                + '}' + "\n";
    }

    public String getName() {
        return name;
    }
    public ArrayList<String> getFilmography() {
        return filmography;
    }
    public double getActorShowsAverage() {
        return actorShowsAverage;
    }

    /** Calculate the average rating for all shows given in a list
     * @param db main Database
     * @param shows list with shows to be considered in rating determination
     * @return a double representing total average rating
     */

    private double calcShowsAverage(final Database db, final ArrayList<String> shows) {
        double totalScore = 0.00;
        int hasRatingCounter = 0;
        for (Movie movie : db.getMovies()) {
            if (shows.contains(movie.getTitle())) {
                if (Double.compare(movie.getRating(), 0) != 0) {
                    totalScore += movie.getRating();
                    hasRatingCounter++;
                }
            }
        }
        for (Serial serial : db.getSerials()) {
            if (shows.contains(serial.getTitle())) {
                if (Double.compare(serial.getRating(), 0) != 0) {
                    totalScore += serial.getRating();
                    hasRatingCounter++;
                }
            }
        }

        if (hasRatingCounter == 0) {
            return 0;
        } else {
            return totalScore / hasRatingCounter;
        }
    }


    /** update the actor shows average field based on the modified database
     * @param db main Database (modified after some commands)
     * @return void
     */
    public void updateActorShowsAverage(final Database db) {
        this.actorShowsAverage = this.calcShowsAverage(db, this.filmography);
    }

    public boolean hasAllAwards(final List<String> awards) {
        if (awards == null) {
            return false;
        }
        for (String award : awards) {
            if (!this.actorsAwards.containsKey(Utils.stringToAwards(award))) {
                return false;
            }
        }
        return true;
    }

    /** Getter for the total number of awards
     * @return int = number of awards for current actor
     */
    public int getTotalNoAwards() {
        int noAwards = 0;
        for (Map.Entry<ActorsAwards, Integer> entry : this.actorsAwards.entrySet()) {
            if (entry.getValue() > 0) {
                noAwards += entry.getValue();
            }
        }
        return noAwards;
    }

    /** Verify if the actor description contains all keywords
     * @param keywords list of words to be searched in the description
     * @return true if the actor has all keywords in his description, false otherwise
     */
    public boolean hasAllKeywords(final List<String> keywords) {
        if (keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            String patternString = ".*\\b" + keyword + "\\b.*";
            Pattern pattern = Pattern.compile(patternString);
            String description = this.careerDescription.toLowerCase();
            Matcher m = pattern.matcher(description);

            if (!m.find()) {
                return false;
            }
        }
        return true;
    }
}

