package com.afollestad.twitter.columns;

import com.afollestad.silk.caching.SilkComparable;

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

    public Column(Class<?> itemType, int id) {
        mItemType = itemType;
        mId = id;
    }

    public Column(Class<?> itemType, int id, String component) {
        this(itemType, id);
        mComponent = escape(component);
    }

    public Column(String str) {
        String[] split = str.split(":");
        mId = Integer.parseInt(split[0]);
        try {
            mItemType = Class.forName(split[1]);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (split.length > 2)
            mComponent = split[2];
    }

    private int mId;
    private Class<?> mItemType;
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
        return unescape(mComponent);
    }

    public Class<?> getType() {
        return mItemType;
    }

    @Override
    public String toString() {
        String str = mId + ":" + escape(mItemType.getName());
        if (mComponent != null) str += ":" + mComponent;
        return str;
    }

    private String escape(String str) {
        return str.replace(",", "&#44;").replace(":", "&colon;");
    }

    private String unescape(String str) {
        return str.replace("&#44;", ",").replace("&colon;", ":");
    }

    @Override
    public Object getSilkId() {
        return toString();
    }

    @Override
    public boolean equalTo(Column other) {
        return toString().equals(other.toString());
    }
}
