package com.androidevlinux.percy.colourmemory;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class randomtest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void randomtest() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.imageButtonHighScore), isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.imageButtonHome), isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction roundedImageView = onView(
                allOf(childAtPosition(
                        withId(R.id.gridViewCards),
                        4),
                        isDisplayed()));
        roundedImageView.perform(click());

        ViewInteraction roundedImageView2 = onView(
                allOf(childAtPosition(
                        withId(R.id.gridViewCards),
                        10),
                        isDisplayed()));
        roundedImageView2.perform(click());

        ViewInteraction roundedImageView3 = onView(
                allOf(childAtPosition(
                        withId(R.id.gridViewCards),
                        12),
                        isDisplayed()));
        roundedImageView3.perform(click());

        ViewInteraction roundedImageView4 = onView(
                allOf(childAtPosition(
                        withId(R.id.gridViewCards),
                        4),
                        isDisplayed()));
        roundedImageView4.perform(click());

        ViewInteraction roundedImageView5 = onView(
                allOf(childAtPosition(
                        withId(R.id.gridViewCards),
                        11),
                        isDisplayed()));
        roundedImageView5.perform(click());

        ViewInteraction roundedImageView6 = onView(
                allOf(childAtPosition(
                        withId(R.id.gridViewCards),
                        8),
                        isDisplayed()));
        roundedImageView6.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
