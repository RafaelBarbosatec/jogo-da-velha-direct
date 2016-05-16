package com.mutationmob.jogodavelha;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.CircularPropagation;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.view.ViewGroup;

/**
 * Created by rafael on 16/05/16.
 */
public class Animations {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void showDelayedTransition(final ViewGroup container3) {

            TransitionSet set = new TransitionSet();
            set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
            Pop pop = new Pop();
            pop.setPropagation(new CircularPropagation() {
                @Override
                public long getStartDelay(ViewGroup sceneRoot, Transition transition, TransitionValues startValues, TransitionValues endValues) {
                    long delay = super.getStartDelay(sceneRoot, transition, startValues, endValues) * 6;
                    return delay;
                }
            });
            pop.setEpicenterCallback(new Transition.EpicenterCallback() {
                @Override
                public Rect onGetEpicenter(Transition transition) {
                    int[] loc = new int[2];
                    container3.getLocationOnScreen(loc);
                    return new Rect(loc[0], loc[1], loc[0] + container3.getWidth(), loc[1] + 40);
                }
            });

            set.addTransition(new ChangeBounds()).addTransition(pop);
            TransitionManager.beginDelayedTransition(container3, set);
        }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void hideDelayedTransition( final ViewGroup container3 ) {

            TransitionSet set = new TransitionSet();
            set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
            Pop pop = new Pop();
            pop.setPropagation(new CircularPropagation() {
                @Override
                public long getStartDelay(ViewGroup sceneRoot, Transition transition, TransitionValues startValues, TransitionValues endValues) {
                    long delay = super.getStartDelay(sceneRoot, transition, startValues, endValues) * 6;
                    return delay;
                }
            });
            pop.setEpicenterCallback(new Transition.EpicenterCallback() {
                @Override
                public Rect onGetEpicenter(Transition transition) {
                    int[] loc = new int[2];
                    container3.getLocationOnScreen(loc);
                    return new Rect(loc[0], loc[1], loc[0] + container3.getWidth(), loc[1] + 40);
                }
            });

            set.addTransition(pop).addTransition(new ChangeBounds());
            TransitionManager.beginDelayedTransition(container3, set);
        }

}
