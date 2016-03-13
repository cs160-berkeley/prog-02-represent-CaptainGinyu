package com.example.cs160_ej.lordofrepresentatives;

import android.graphics.drawable.Drawable;

public class RepresentativeInfo
{
    public String name;
    public int repImageReference;
    public Drawable repImageDrawable;
    public String party;
    public String email;
    public String website;
    public String lastTweet;
    public String endOfTerm;
    public DetailedInfo detailedInfo;

    public RepresentativeInfo(String name, int repImageReference, String party, String email,
                              String website, String lastTweet, DetailedInfo detailedInfo)
    {
        this.name = name;
        this.repImageReference = repImageReference;
        this.party = party;
        this.email = email;
        this.website = website;
        this.lastTweet = lastTweet;
        this.detailedInfo = detailedInfo;
    }

    public RepresentativeInfo(String name, Drawable repImageDrawable, String party, String email,
                              String website, String lastTweet, String endOfTerm)
    {
        this.name = name;
        this.repImageDrawable = repImageDrawable;
        this.party = party;
        this.email = email;
        this.website = website;
        this.lastTweet = lastTweet;
        this.endOfTerm = endOfTerm;
    }
}
