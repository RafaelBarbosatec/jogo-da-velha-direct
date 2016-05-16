package com.mutationmob.jogodavelha;

/**
 * Created by rafael on 16/05/16.
 */
import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.os.Build;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

/**
 * Created by luciofm on 10/27/14.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class Pop extends Visibility {
    private boolean overshoot = true;

    public Pop(boolean overshoot) {
        super();
        this.overshoot = overshoot;
    }

    public Pop() {
        this(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        view.setScaleX(0f);
        view.setScaleY(0f);

        PropertyValuesHolder[] pvh = new PropertyValuesHolder[2];
        pvh[0] = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f);
        pvh[1] = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, pvh);
        anim.setInterpolator(overshoot ? new OvershootInterpolator() : new DecelerateInterpolator());

        return anim;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        PropertyValuesHolder[] pvh = new PropertyValuesHolder[2];
        pvh[0] = PropertyValuesHolder.ofFloat(View.SCALE_X, 0f);
        pvh[1] = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0f);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(view, pvh);
        anim.setInterpolator(overshoot ? new AnticipateInterpolator() : new DecelerateInterpolator());

        return anim;
    }
}
