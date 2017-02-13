package com.androidevlinux.percy.colourmemory;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Percy on 12/02/17.
 */
public class ImageState extends RealmObject {
    @PrimaryKey
    private int position;
    private int content;
    private boolean gone;
    private boolean open;

    public void setPosition(int index) {
        this.position = index;
    }
    public int getPosition() {
        return position;
    }

    public void setContent(int content){
        this.content = content;
    }
    public int getContent() {
        return content;
    }

    public void setGone(boolean gone) {
        this.gone = gone;
    }
    public boolean getGone() {
        return gone;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
    public boolean getOpen() {
        return open;
    }
}
