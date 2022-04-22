package ie.ul.ihearthealth;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.view.Gravity;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

/**
 * Instrumented tests, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class InstrumentedTests {
    @Rule
    public ActivityScenarioRule<LoginActivity> activityRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("ie.ul.ihearthealth", appContext.getPackageName());
    }

    @Test
    public void testConnectUsingText() {
        onView(withText("Or connect using")).check(matches(isDisplayed()));
    }

    @Test
    public void testRegister() {
        onView(withId(R.id.register)).perform(click());
        onView(withId(R.id.newUserEmail))
                .perform(replaceText("nicole.berty@test.com"), closeSoftKeyboard());
        onView(withId(R.id.newUserPassword))
                .perform(replaceText("12345678"), closeSoftKeyboard());
        onView(withId(R.id.newUserRegisterBtn)).perform(click());
    }

    @Test
    public void testLogin() {
        onView(withId(R.id.username))
                .perform(replaceText("nicole.berty@test.com"), closeSoftKeyboard());
        onView(withId(R.id.password))
                .perform(replaceText("12345678"), closeSoftKeyboard());
        // Check that the text was changed.
        onView(withId(R.id.username)).check(matches(withText("nicole.berty@test.com")));
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
                .perform(replaceText("nicole.berty2@test.com"), closeSoftKeyboard());
        onView(withId(R.id.btn_change_email)).perform(scrollTo(), click());

        confirmCredentials("nicole.berty@test.com", "12345678");
    }

    @Test
    public void testRevertEmail() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_settings));
        onView(withId(R.id.email_address))
                .perform(replaceText(""),
                        replaceText("nicole.berty@test.com"), closeSoftKeyboard());
        onView(withId(R.id.btn_change_email)).perform(scrollTo(), click());

        confirmCredentials("nicole.berty2@test.com", "12345678");
    }

    @Test
    public void testUpdateName() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_settings));
        onView(withId(R.id.name))
                .perform(scrollTo(), replaceText("Nicole Berty"), closeSoftKeyboard());
        onView(withId(R.id.btn_change_name)).perform(scrollTo(), click());

        onView(withId(R.id.name)).check(matches(withText("Nicole Berty")));
        onView(withId(R.id.name))
                .perform(scrollTo(), replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.btn_change_name)).perform(scrollTo(), click());
        onView(withId(R.id.name)).check(matches(withText("")));
    }

    @Test
    public void testUpdatePass() {
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(DrawerActions.open()); // Open Drawer

        // Start the screen of your activity.
        onView(withId(R.id.nav_view))
                .perform(NavigationViewActions.navigateTo(R.id.nav_settings));
        fillPasswordFields("123456", "123456");

        confirmCredentials("nicole.berty@test.com", "12345678");

        fillPasswordFields("12345678", "12345678");
        confirmCredentials("nicole.berty@test.com", "123456");
        onView(withId(R.id.change_pass_text)).check(matches(withText("Change your password:")));
    }

    void fillPasswordFields(String pass, String repeatPass) {
        onView(withId(R.id.password))
                .perform(scrollTo(), replaceText(pass), closeSoftKeyboard());
        onView(withId(R.id.password)).check(matches(withText(pass)));
        onView(withId(R.id.password2))
                .perform(scrollTo(), replaceText(repeatPass), closeSoftKeyboard());
        onView(withId(R.id.btn_change_pass)).perform(scrollTo(), click());
    }

    void confirmCredentials(String email, String password) {
        try {
            onView(withId(R.id.username)).check(matches(isDisplayed()));
            onView(withId(R.id.username))
                    .perform(replaceText(email), closeSoftKeyboard());
            onView(withId(R.id.password))
                    .perform(replaceText(password), closeSoftKeyboard());
        } catch (NoMatchingViewException e) {
            System.out.println("No matching view");
        }
        onView(withId(android.R.id.button1)).perform(click());
    }
}