package ie.ul.ihearthealth.ht_nav_drawer;

/**
 * A Model for the recycler views in the hypertension info fragments
 */
public class MyModel
{
    private String title;

    public MyModel(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
