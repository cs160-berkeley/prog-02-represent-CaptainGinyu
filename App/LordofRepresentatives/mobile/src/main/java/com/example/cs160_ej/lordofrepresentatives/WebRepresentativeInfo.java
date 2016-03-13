package com.example.cs160_ej.lordofrepresentatives;

import android.graphics.drawable.Drawable;

public class WebRepresentativeInfo
{
    public String name;
    public String repImgUrl;
    public String party;
    public String email;
    public String website;
    public String lastTweet;
    public String endOfTerm;
    public String bio_id;
    public String twitter_id;

    public WebRepresentativeInfo(String name, String repImgUrl, String party, String email,
                              String website, String lastTweet, String endOfTerm, String bio_id, String twitter_id)
    {
        this.name = name;
        this.repImgUrl = repImgUrl;
        this.party = party;
        this.email = email;
        this.website = website;
        this.lastTweet = lastTweet;
        this.endOfTerm = endOfTerm;
        this.bio_id = bio_id;
        this.twitter_id = twitter_id;
    }
}
