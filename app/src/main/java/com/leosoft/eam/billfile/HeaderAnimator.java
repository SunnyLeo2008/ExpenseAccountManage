package com.leosoft.eam.billfile;

import android.view.View;

import com.leosoft.eam.R;

import it.carlom.stikkyheader.core.animator.AnimatorBuilder;
import it.carlom.stikkyheader.core.animator.HeaderStikkyAnimator;

/**
 * Created by Leo on 2017-06-10.
 */

public class HeaderAnimator extends HeaderStikkyAnimator {

    @Override
    public AnimatorBuilder getAnimatorBuilder() {

        View viewToAnimate = getHeader().findViewById(R.id.bill_file_header);

        AnimatorBuilder animatorBuilder = AnimatorBuilder.create()
                .applyScale(viewToAnimate,1f,0.6f);

        return animatorBuilder;
    }

}
