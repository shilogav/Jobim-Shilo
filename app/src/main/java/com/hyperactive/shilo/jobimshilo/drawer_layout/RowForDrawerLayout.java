package com.hyperactive.shilo.jobimshilo.drawer_layout;



public class RowForDrawerLayout {
    private String nameOfRow;
    private int pictureId;

    public RowForDrawerLayout(String nameOfRow, int pictureId) {
        this.nameOfRow = nameOfRow;
        setPictureId(pictureId);
    }

    public String getNameOfRow() {
        return nameOfRow;
    }

    public void setNameOfRow(String nameOfRow) {
        this.nameOfRow = nameOfRow;
    }

    public int getPictureId() {
        return pictureId;
    }

    public void setPictureId(int pictureId) {

        this.pictureId = pictureId;
    }
}
