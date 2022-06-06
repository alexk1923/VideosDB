package actions;

import actor.Actor;
import common.Constants;
import db.Database;
import entertainment.Movie;
import entertainment.Serial;
import entertainment.Show;
import entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Query extends Action {

    private final String objectType;
    private final String sortType;
    private final String criteria;
    private final int number;
    private final List<List<String>> filters = new ArrayList<>();

    public Query(final int actionId, final String actionType, final String objectType,
                 final String sortType, final String criteria, final int number,
                 final List<List<String>> filters) {
        super(actionId, actionType);
        this.objectType = objectType;
        this.sortType = sortType;
        this.criteria = criteria;
        this.number = number;
        this.filters.addAll(filters);
    }

    public String getCriteria() {
        return criteria;
    }

    @Override
    public String toString() {
        return "Query{"
                + "objectType='" + objectType + '\''
                + ", sortType='" + sortType + '\''
                + ", criteria='" + criteria + '\''
                + ", number=" + number
                + ", filters=" + filters
                + '}';
    }

    @Override
    public String run(final Database db) {
        return switch (this.objectType) {
            case Constants.ACTORS -> ("Query result: " + this.actors(db));
            case Constants.SHOWS, Constants.MOVIES -> ("Query result: " + this.shows(db));
            case Constants.USERS -> ("Query result: " + this.users(db));
            default -> ("Query result: Invalid command");
        };
    }

    /** Execute a query for actors depending on its type
     *
     * @param db main Database
     * @return query result
     */
    public String actors(final Database db) {
        return switch (this.criteria) {
            case Constants.AVERAGE -> this.actorsAverage(db);
            case Constants.AWARDS -> this.actorsAwards(db);
            case Constants.FILTER_DESCRIPTIONS -> this.actorsFilterDescription(db);
            default -> ("Invalid command");
        };
    }

    /** Execute a query for videos depending on its type
     *
     * @param db main Database
     * @return query result
     */
    public String shows(final Database db) {
        return switch (this.criteria) {
            case Constants.RATINGS -> this.showsRating(db);
            case Constants.FAVORITE -> this.showsFavorite(db);
            case Constants.LONGEST -> this.showsLongest(db);
            case Constants.MOST_VIEWED -> this.showsMostViewed(db);
            default -> ("Invalid command");
        };
    }

    /** Execute a query for users depending on its type
     *
     * @param db main Database
     * @return query result
     */

    public String users(final Database db) {
        if (this.criteria.equals(Constants.NUM_RATINGS)) {
            return this.userNumRatings(db);
        } else {
            return ("Invalid command");
        }
    }

    private int compareActors(final Actor a1, final Actor a2, final int compare) {
        if (this.sortType.equals("asc")) {
            if (compare != 0) {
                return compare;
            } else {
                return (a1.getName().compareTo(a2.getName()));
            }
        } else {
            if (compare != 0) {
                return compare * (-1);
            } else {
                return (-1) * (a1.getName().compareTo(a2.getName()));
            }
        }
    }

    private int compareShows(final Show s1, final Show s2, final int compare) {
        if (this.sortType.equals("asc")) {
            if (compare != 0) {
                return compare;
            } else {
                return (s1.getTitle().compareTo(s2.getTitle()));
            }
        } else {
            if (compare != 0) {
                return compare * (-1);
            } else {
                return (-1) * (s1.getTitle().compareTo(s2.getTitle()));
            }
        }
    }

    private int compareUsers(final User u1, final User u2, final int compare) {
        if (this.sortType.equals("asc")) {
            if (compare != 0) {
                return compare;
            } else {
                return (u1.getUsername().compareTo(u2.getUsername()));
            }
        } else {
            if (compare != 0) {
                return compare * (-1);
            } else {
                return (-1) * (u1.getUsername().compareTo(u2.getUsername()));
            }
        }
    }

    /** Execute a query for actors based on average rating
     *
     * @param db main Database
     * @return N sorted actors by average rating of their filmography
     */
    public String actorsAverage(final Database db) {
        ArrayList<Actor> actorList = new ArrayList<>();
        for (Actor sourceActor : db.getActors()) {
            sourceActor.updateActorShowsAverage(db);
            actorList.add(new Actor(sourceActor));
        }

        actorList.sort((a1, a2) -> compareActors(a1, a2,  Double.compare(a1.getActorShowsAverage(),
                a2.getActorShowsAverage())));

        actorList.removeIf(actor -> Double.compare(actor.getActorShowsAverage(), 0.0) == 0);
        actorList.subList(Math.min(this.number, actorList.size()), (actorList.size())).clear();

        String res = actorList.stream().map(Actor::getName)
                .collect(Collectors.joining(", "));

        return ("[" + res + "]");
    }

    /** Execute a query for actors based on a list of awards
     *
     * @param db main Database
     * @return a list with all actors that won the awards given in the query
     */
    public String actorsAwards(final Database db) {
        ArrayList<Actor> actorList = new ArrayList<>();
        for (Actor sourceActor : db.getActors()) {
            actorList.add(new Actor(sourceActor));
        }
        List<String> awards = this.filters.get(3);

        actorList.removeIf(actor -> !actor.hasAllAwards(awards));
        actorList.sort((a1, a2) -> compareActors(a1, a2, Double.compare(a1.getTotalNoAwards(),
                a2.getTotalNoAwards())));

        actorList.subList(Math.min(this.number, actorList.size()), (actorList.size())).clear();
        String res = actorList.stream().map(Actor::getName)
                .collect(Collectors.joining(", "));
        return ("[" + res + "]");

    }

    /** Execute a query for actors based on their career description
     * @param db main Database
     * @return a list with all actors that have a description containing all keywords given in
     * the query
     */
    public String actorsFilterDescription(final Database db) {
        ArrayList<Actor> actorList = new ArrayList<>();
        for (Actor sourceActor : db.getActors()) {
            actorList.add(new Actor(sourceActor));
        }

        List<String> keywords = this.filters.get(2);
        actorList.removeIf(actor -> !actor.hasAllKeywords(keywords));

        actorList.sort((a1, a2) -> {
            final int compare = a1.getName().compareTo(a2.getName());
            if (this.sortType.equals("asc")) {
                return compare;
            } else {
                return compare * (-1);
            }
        });

        actorList.subList(Math.min(this.number, actorList.size()), (actorList.size())).clear();
        String res = actorList.stream().map(Actor::getName)
                .collect(Collectors.joining(", "));
        return ("[" + res + "]");
    }

    private void applyFilters(final ArrayList<Show> showsList) {

        if (this.filters.get(0).get(0) != null) {
            showsList.removeIf(show -> !show.getYearOfRelease().toString()
                    .equals(this.filters.get(0).get(0)));
        }

        if (this.filters.get(1) != null) {
            showsList.removeIf(show -> show.hasGivenFilter(this.filters.get(1)));
        }
    }

    /** Execute a query for videos based on their rating
     *
     * @param db main Database
     * @return N videos sorted by rating
     */
    public String showsRating(final Database db) {

        ArrayList<Show> showsList = Show.copyVideosByObjectType(objectType, db);

        showsList.removeIf(show -> Double.compare(show.getRating(), 0) == 0);
        applyFilters(showsList);

        showsList.sort((s1, s2) -> compareShows(s1, s2, Double.compare(s1.getRating(),
                                                s2.getRating())));

        if (showsList.size() != 0) {
            showsList.subList(Math.min(this.number, showsList.size()), (showsList.size())).clear();
        }

        String res = showsList.stream().map(Show::getTitle)
                .collect(Collectors.joining(", "));

        return ("[" + res + "]");
    }

    /** Execute a query for videos based on favorite appearances
     *
     * @param db main Database
     * @return N videos sorted by their number of appearances in users favorite lists
     */
    public String showsFavorite(final Database db) {

        ArrayList<Show> showsList = Show.copyVideosByObjectType(objectType, db);

        showsList.sort((s1, s2) -> compareShows(s1, s2,
                Double.compare(s1.getFavoriteApps(db), s2.getFavoriteApps(db))));

        applyFilters(showsList);
        showsList.removeIf(show -> show.getFavoriteApps(db) == 0);
        showsList.subList(Math.min(this.number, showsList.size()), (showsList.size())).clear();

        String res = showsList.stream().map(Show::getTitle)
                .collect(Collectors.joining(", "));
        return ("[" + res + "]");
    }

    /** Execute a query for videos based on duration
     *
     * @param db main Database
     * @return N videos sorted by their duration
     */
    public String showsLongest(final Database db) {
        ArrayList<Show> showsList = Show.copyVideosByObjectType(this.objectType, db);

        showsList.sort((s1, s2) -> compareShows(s1, s2,
                Double.compare(s1.getDuration(), s2.getDuration())));
        applyFilters(showsList);
        showsList.subList(Math.min(this.number, showsList.size()), (showsList.size())).clear();

        String res = showsList.stream().map(Show::getTitle)
                .collect(Collectors.joining(", "));
        return ("[" + res + "]");
    }

    /** Execute a query for videos based on views
     *
     * @param db main Database
     * @return N videos sorted by their number of views
     */
    public String showsMostViewed(final Database db) {
        ArrayList<Show> showsList = Show.copyVideosByObjectType(this.objectType, db);

        showsList.sort((s1, s2) -> compareShows(s1, s2,
                Double.compare(s1.getViews(db), s2.getViews(db))));

        applyFilters(showsList);
        showsList.removeIf(show -> show.getViews(db) == 0);
        showsList.subList(Math.min(this.number, showsList.size()), (showsList.size())).clear();

        String res = showsList.stream().map(Show::getTitle)
                .collect(Collectors.joining(", "));
        return ("[" + res + "]");
    }

    /** Execute a query for users based on their activity
     *
     * @param db main Database
     * @return N users sorted by number of rating given to videos
     */
    public String userNumRatings(final Database db) {
        ArrayList<User> userList = new ArrayList<>();
        for (User sourceUser : db.getUsers()) {
            userList.add(new User(sourceUser));
        }

        userList.sort((u1, u2) -> compareUsers(u1, u2,
                u1.getRatings().size() - u2.getRatings().size()));

        userList.removeIf(user -> user.getRatings().size() == 0);
        userList.subList(Math.min(this.number, userList.size()), (userList.size())).clear();

        String res = userList.stream().map(User::getUsername)
                .collect(Collectors.joining(", "));
        return ("[" + res + "]");
    }


}
