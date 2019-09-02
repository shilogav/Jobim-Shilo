package com.hyperactive.shilo.jobimshilo.search_jobs;



public class Careers {
    private String profession;
    private String company;
    private int idIcon;
    private boolean isChecked;



    public Careers(int idIcon, String profession) {
        this.idIcon = idIcon;
        this.profession = profession;
        this.isChecked=false;
        this.company="";
    }

    public Careers(String company) {
        this.company=company;
        this.profession = "";
        this.idIcon = -1;
        this.isChecked=false;
    }



    public int getIdIcon() {
        return idIcon;
    }

    public void setIdIcon(int idIcon) {
        this.idIcon = idIcon;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
