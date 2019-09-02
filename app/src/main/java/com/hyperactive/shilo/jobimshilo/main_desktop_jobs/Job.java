package com.hyperactive.shilo.jobimshilo.main_desktop_jobs;

//The job class represent the job characters

import java.io.Serializable;

public class Job implements Serializable {
    private String companyName,branchName;
    private String kindOfJob;
    private String infoTitle,infoContent;
    private String location;
    private String jobID;
    private String phonenumber;
    private String mail;
    private String distance;

    public Job() {
    }

    public Job(String companyName, String branchName,
               String kindOfJob, String infoTitle,
               String infoContent, String location, String jobID)
    {
        this.companyName = companyName;
        this.branchName = branchName;
        this.kindOfJob = kindOfJob;
        this.infoTitle = infoTitle;
        this.infoContent = infoContent;
        this.location = location;
        this.jobID = jobID;
        this.distance="0";
        this.phonenumber="0";
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getKindOfJob() {
        return kindOfJob;
    }

    public void setKindOfJob(String kindOfJob) {
        this.kindOfJob = kindOfJob;
    }

    public String getInfoTitle() {
        return infoTitle;
    }

    public void setInfoTitle(String infoTitle) {
        this.infoTitle = infoTitle;
    }

    public String getInfoContent() {
        return infoContent;
    }

    public void setInfoContent(String infoContent) {
        this.infoContent = infoContent;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
