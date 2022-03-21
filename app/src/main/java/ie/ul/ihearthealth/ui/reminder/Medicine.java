package ie.ul.ihearthealth.ui.reminder;

public class Medicine {

    private String id;
    private String medName;
    private String medDosage;
    private String medDesc;
    private String medTime;
    private String startDate;
    private String repeat;

    public Medicine(String id, String medName, String medDosage, String medDesc, String medTime, String startDate, String repeat) {
        this.id = id;
        this.medName = medName;
        this.medDosage = medDosage;
        this.medDesc = medDesc;
        this.medTime = medTime;
        this.startDate = startDate;
        this.repeat = repeat;
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
}
