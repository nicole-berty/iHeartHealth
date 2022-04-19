package ie.ul.ihearthealth;

import org.junit.Test;

import static org.junit.Assert.*;

import ie.ul.ihearthealth.login.LoginViewModel;
import ie.ul.ihearthealth.main_nav_drawer.track_monitor.MeasureFragment;

/**
 * Unit tests for the app
 */
public class UnitTests {
    LoginViewModel loginViewModel;
    @Test
    public void TestValidLoginPassword() {
        loginViewModel = new LoginViewModel();
        assertEquals(true, loginViewModel.isPasswordValid("1234567"));
    }

    @Test
    public void TestInvalidLoginPassword() {
        loginViewModel = new LoginViewModel();
        assertEquals(false, loginViewModel.isPasswordValid("1234"));
    }

    @Test
    public void TestValidNumeric() {
        MeasureFragment measureFragment = new MeasureFragment();
        assertEquals(true, measureFragment.isNumeric("12345"));
        assertEquals(true, measureFragment.isNumeric("123.45"));
        assertEquals(true, measureFragment.isNumeric("12345.0"));
    }

    @Test
    public void TestInvalidNumeric() {
        MeasureFragment measureFragment = new MeasureFragment();
        assertEquals(false, measureFragment.isNumeric("1,2345"));
        assertEquals(false, measureFragment.isNumeric("123.45."));
        assertEquals(false, measureFragment.isNumeric("12345."));
    }
}