package com.androidevlinux.percy.colourmemory;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Percy on 12/02/17.
 */
public class GameHighScore extends RealmObject {
    @PrimaryKey
    @Required
    private String name;
    private long score;
    @Required
    private String rank;

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setScore(long score) {
        this.score = score;
    }
    public long getScore() {
        return score;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
    public String getRank() {
        return rank;
    }

    public static String getRank(long score) {
        if(score <= 0)
            return "Beginner";
        else if(score > 0 && score <= 5)
            return "Amateur";
        else if(score > 5 && score <= 13)
            return "Expert";
        return "Master";
    }
}
