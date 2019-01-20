package com.sample.myapplication.display_model;

public class FlickrImagesDisplayModel {
    String urlOne;
    String urlTwo;
    String urlThree;

    public FlickrImagesDisplayModel(String urlOne, String urlTwo, String urlThree) {
        this.urlOne = urlOne;
        this.urlTwo = urlTwo;
        this.urlThree = urlThree;
    }

    public String getUrlOne() {
        return urlOne;
    }

    public void setUrlOne(String urlOne) {
        this.urlOne = urlOne;
    }

    public String getUrlTwo() {
        return urlTwo;
    }

    public void setUrlTwo(String urlTwo) {
        this.urlTwo = urlTwo;
    }

    public String getUrlThree() {
        return urlThree;
    }

    public void setUrlThree(String urlThree) {
        this.urlThree = urlThree;
    }
}
