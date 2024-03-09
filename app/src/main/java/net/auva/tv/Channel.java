package net.auva.tv;

/**
 * Created by ankit on 27/10/17.
 */

public class Channel {

    public String id;
    public String CName;
    public String Image;
    public String StreamName;
    public String Cat;
    public String StreamName360;
    public String StreamName480;
    public String StreamName720;

    public Category category;

    public Channel() {

    }

    public Channel(String id, String cname, String image, String streamName , String cat, String streamName360, String streamName480, String streamName720) {
        this.id = id;
        this.CName = cname;
        this.Image = image;
        this.Cat = cat;
        this.StreamName = streamName;
        this.StreamName360 = streamName360;
        this.StreamName480 = streamName480;
        this.StreamName720 = streamName720;

    }

    public void setCName(String CName) {
        this.CName = CName;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setStreamName360(String streamName360) {
        StreamName360 = streamName360;
    }

    public void setCat(String cat) {
        Cat = cat;
    }

    public void setStreamName480(String streamName480) {
        StreamName480 = streamName480;
    }

    public void setStreamName720(String streamName720) {
        StreamName720 = streamName720;
    }

    public String getCName() {
        return CName;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return Image;
    }

    public void setStreamName(String streamName) {
        StreamName = streamName;
    }

    public String getStreamName() {
        return StreamName;
    }

    public String getCat() {
        return Cat;
    }

    public String getStreamName720() {
        return StreamName720;
    }

    public String getStreamName480() {
        return StreamName480;
    }

    public String getStreamName360() {
        return StreamName360;
    }
}
