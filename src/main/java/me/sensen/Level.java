package me.sensen;

/**
 * @author Sensen
 */
public class Level {
    private int id;
    private String song, artist, creator, diff, pguDiff;

    public String getDiff() {
        if (diff.contains(".05")) {
            diff = diff.replace(".05", "+");
        } else if (diff.contains(".15")) {
            diff = diff.replace(".15", ".1+");
        } else if (diff.contains(".25")) {
            diff = diff.replace(".25", ".2+");
        } else if (diff.contains(".35")) {
            diff = diff.replace(".35", ".3+");
        } else if (diff.contains(".45")) {
            diff = diff.replace(".45", ".4+");
        } else if (diff.contains(".55")) {
            diff = diff.replace(".55", ".5+");
        } else if (diff.contains(".65")) {
            diff = diff.replace(".65", ".6+");
        } else if (diff.contains(".75")) {
            diff = diff.replace(".75", ".7+");
        } else if (diff.contains(".85")) {
            diff = diff.replace(".85", ".8+");
        } else if (diff.contains(".95")) {
            diff = diff.replace(".95", ".9+");
        }
        diff = diff.replace("0000000000002", "");
        return diff;
    }

    public int getId() {
        return id;
    }

    public String getSong() {
        return song;
    }

    public String getArtist() {
        return artist;
    }

    public String getCreator() {
        return creator;
    }

    public String getPguDiff() {
        return pguDiff;
    }
}
