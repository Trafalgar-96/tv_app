package net.auva.tv;

import java.util.List;

public class Category {
    public List name;
    public List<Channel> channels;

    Category(List Name, List<Channel> Channels){
        this.name=Name;
        this.channels=Channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public void setName(List name) {
        this.name = name;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List getName() {
        return name;
    }
}
