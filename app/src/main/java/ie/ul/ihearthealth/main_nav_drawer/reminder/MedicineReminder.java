package ie.ul.ihearthealth.main_nav_drawer.reminder;

/**
 * A class to creates objects representing a Medication reminder
 */
public class MedicineReminder {

    private String id;
    private String medName;
    private String medDosage;
    private String medDesc;
    private String medTime;
    private String startDate;
    private String repeat;
    private int requestCode;

    public MedicineReminder(String id, String medName, String medDosage, String medDesc, String medTime, String startDate, String repeat, String requestCode) {
        this.id = id;
        this.medName = medName;
        this.medDosage = medDosage;
        this.medDesc = medDesc;
        this.medTime = medTime;
        this.startDate = startDate;
        this.repeat = repeat;
        requestCode = requestCode.replace("RequestCode:", "");
        if(!requestCode.equals("")) this.requestCode = Integer.parseInt(requestCode);
    }

    public String getId() {
        return id;
    }

    public String getMedName() {
        return medName;
    }

    public String getMedDesc() {
        return medDesc;
    }

    public String getMedDosage() {
        return medDosage;
    }

    public String getMedTime() {
        return medTime;
    }

    public String getRepeat() {
        return repeat;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getRequestCode() {
        return requestCode;
    }
}
