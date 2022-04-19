package ie.ul.ihearthealth;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.view.Gravity;

import androidx.annotation.IdRes;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented tests, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginRegisterInstrumentedTest {

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("ie.ul.ihearthealth", appContext.getPackageName());
    }

    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testConnectUsingText() {
        onView(withText("Or connect using")).check(matches(isDisplayed()));
    }

    @Test
    public void testRegister() {
        onView(withId(R.id.register)).perform(click());
        onView(withId(R.id.newUserEmail))
                .perform(typeText("nicole.berty@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.newUserPassword))
                .perform(typeText("12345678"), closeSoftKeyboard());
        onView(withId(R.id.newUserRegisterBtn)).perform(click());
    }

    @Test
    public void testLogin() {
        onView(withId(R.id.username))
                .perform(typeText("nicole.berty@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(typeText("12345678"), closeSoftKeyboard());
        // Check that the text was changed.
        onView(withId(R.id.username)).check(matches(withText("nicole.berty@gmail.com")));
        onView(withId(R.id.password)).check(matches(withText("12345678")));

        onView(withId(R.id.login)).check(matches(isEnabled()));

        onView(withId(R.id.login)).perform(click());
    }

    @Test
    public void testLogout() {

        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.logOut));

    }

    @Test
    public void testUpdateEmail() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_settings));
        onView(withId(R.id.email_address))
                .perform(typeText("nicole.berty2@gmail.com"), closeSoftKeyboard());
        onView(withId(R.id.btn_change_email)).perform(scrollTo(), click());

        try {
            onView(withId(R.id.username)).check(matches(isDisplayed()));
            onView(withId(R.id.username))
                    .perform(typeText("nicole.berty@gmail.com"), closeSoftKeyboard());
            onView(withId(R.id.password))
                    .perform(typeText("12345678"), closeSoftKeyboard());
        } catch (NoMatchingViewException e) {
            System.out.println("No matching view");
        }
        onView(withId(android.R.id.button1)).perform(click());

    }
}