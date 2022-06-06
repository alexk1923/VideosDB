package actions;

import db.Database;

public abstract class Action {

    private final int actionId;
    private final String actionType;

    public Action(final int actionId, final String actionType) {
        this.actionId = actionId;
        this.actionType = actionType;
    }

    public final int getActionId() {
        return actionId;
    }

    public final String getActionType() {
        return actionType;
    }

    /** Print actionId and actionType
     *
     * @return an Action String
     */
    @Override
    public String toString() {
        return "Action{"
                + "actionId=" + actionId
                + ", actionType='" + actionType
                + '\'' + '}';
    }

    /** Method to execute an action from database
     *
     * @param db main Database
     * @return a result String
     */
    public abstract String run(Database db);

}

