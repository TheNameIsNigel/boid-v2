package com.afollestad.twitter.columns;

import com.afollestad.silk.cache.SilkComparable;

/**
 * @author Aidan Follestad (afollestad)
 */
public class Column implements SilkComparable<Column> {

    public final static int PROFILE_BUTTON = -2;

    public final static int TIMELINE = 0;
    public final static int MENTIONS = 1;
    public final static int MESSAGES = 2;
    public final static int TRENDS = 3;
    public final static int SEARCH = 4;
    public final static int LIST = 5;

    public Column(int id) {
        mId = id;
    }

    public Column(int id, String component) {
        mId = id;
        mComponent = component.replace(",", "&#44;");
    }

    public Column(String str) {
        if (str.contains(":")) {
            mId = Integer.parseInt(str.substring(0, str.indexOf(':')));
            mComponent = str.substring(str.indexOf(':') + 1).replace("&#44;", ",");
        } else mId = Integer.parseInt(str);
    }

    private int mId;
    private String mComponent;

    public int getId() {
        return mId;
    }

    public String getName(boolean upperFirst) {
        String name;
        switch (mId) {
            default:
                name = getComponent();
                break;
            case TIMELINE:
                name = "timeline";
                break;
            case MENTIONS:
                name = "mentions";
                break;
            case MESSAGES:
                name = "messages";
                break;
            case TRENDS:
                name = "trends";
                break;
        }
        if (upperFirst) name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
        return name;
    }

    public String getComponent() {
        return mComponent;
    }

    @Override
    public String toString() {
        if (mComponent == null) return mId + "";
        return mId + ":" + mComponent;
    }

    @Override
    public boolean isSameAs(Column another) {
        boolean equal = getId() == another.getId();
        if (getComponent() != null) equal = equal && getComponent().equals(another.getComponent());
        return equal;
    }

    @Override
    public boolean shouldIgnore() {
        return getId() == PROFILE_BUTTON;
    }
}
