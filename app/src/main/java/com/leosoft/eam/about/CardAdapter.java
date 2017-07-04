package com.leosoft.eam.about;

import android.support.v7.widget.CardView;

/**
 * Created by Leo on 2017-06-05.
 */

public interface CardAdapter {

    int MAX_ELEVATION_FACTOR = 8;

    float getBaseElevation();

    CardView getCardViewAt(int position);

    int getCount();
}
