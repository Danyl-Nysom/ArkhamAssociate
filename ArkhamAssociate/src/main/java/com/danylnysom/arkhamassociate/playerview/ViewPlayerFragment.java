package com.danylnysom.arkhamassociate.playerview;

import android.database.Cursor;

/**
 * Created by Dylan on 10/12/13.
 */
public interface ViewPlayerFragment {

    public void update(Cursor player, Cursor investigator);
}
