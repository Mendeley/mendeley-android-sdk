package com.mendeley.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mendeley.sdk.util.NullableList;

import java.util.Date;
import java.util.List;

public class Annotation {
    public enum Type {
        STICKY_NOTE("sticky_note"), HIGHLIGHT("highlight"), DOCUMENT_NOTE("note");

        public final String name;

        public static Type fromName(String name) {
            for (Type type : values()) {
                if (type.name.equals(name))
                    return type;
            }
            return null;
        }
        Type(String name) {
            this.name = name;
        }
    }

    public enum PrivacyLevel {
        PRIVATE("private"), GROUP("group"), PUBLIC("public");

        public final String name;

        public static PrivacyLevel fromName(String name) {
            for (PrivacyLevel level : values()) {
                if (level.name.equals(name))
                    return level;
            }
            return null;
        }

        PrivacyLevel(String name) {
            this.name = name;
        }
    }

    public final String id;
    public final Type type;
    public final String previousId;
    public final Integer color;
    public final String text;
    public final String profileId;
    public final NullableList<Position> positions;
    public final Date created;
    public final Date lastModified;
    public final PrivacyLevel privacyLevel;
    public final String fileHash;
    public final String documentId;

    private Annotation(
            String id,
            Type type,
            String previousId,
            Integer color,
            String text,
            String profileId,
            List<Position> positions,
            Date created,
            Date lastModified,
            PrivacyLevel privacyLevel,
            String fileHash,
            String documentId) {
        this.id = id;
        this.type = type;
        this.previousId = previousId;
        this.color = color;
        this.text = text;
        this.profileId = profileId;
        this.positions = new NullableList<Position>(positions);
        this.created = created;
        this.lastModified = lastModified;
        this.privacyLevel = privacyLevel;
        this.fileHash = fileHash;
        this.documentId = documentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Annotation that = (Annotation) o;

        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        if (documentId != null ? !documentId.equals(that.documentId) : that.documentId != null)
            return false;
        if (fileHash != null ? !fileHash.equals(that.fileHash) : that.fileHash != null)
            return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (positions != null ? !positions.equals(that.positions) : that.positions != null)
            return false;
        if (privacyLevel != that.privacyLevel) return false;
        if (profileId != null ? !profileId.equals(that.profileId) : that.profileId != null)
            return false;
        if (text != null ? !text.equals(that.text) : that.text != null) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (profileId != null ? profileId.hashCode() : 0);
        result = 31 * result + (positions != null ? positions.hashCode() : 0);
        result = 31 * result + (privacyLevel != null ? privacyLevel.hashCode() : 0);
        result = 31 * result + (fileHash != null ? fileHash.hashCode() : 0);
        result = 31 * result + (documentId != null ? documentId.hashCode() : 0);
        return result;
    }

    public static class Builder {
        private String id;
        private Type type;
        private String previousId;
        private Integer color;
        private String text;
        private String profileId;
        private List<Position> positions;
        private Date created;
        private Date lastModified;
        private PrivacyLevel privacyLevel;
        private String fileHash;
        private String documentId;

        public Builder() {
        }

        public Builder(Annotation from) {
            this.id = from.id;
            this.type = from.type;
            this.previousId = from.previousId;
            this.color = from.color;
            this.text = from.text;
            this.profileId = from.profileId;
            this.positions = from.positions;
            this.created = from.created;
            this.lastModified = from.lastModified;
            this.privacyLevel = from.privacyLevel;
            this.fileHash = from.fileHash;
            this.documentId = from.documentId;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setPreviousId(String previousId) {
            this.previousId = previousId;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setText(String text) {
            this.text = text;
            return this;
        }

        public Builder setProfileId(String profileId) {
            this.profileId = profileId;
            return this;
        }

        public Builder setPositions(List<Position> positions) {
            this.positions = positions;
            return this;
        }

        public Builder setCreated(Date created) {
            this.created = created;
            return this;
        }

        public Builder setLastModified(Date lastModified) {
            this.lastModified = lastModified;
            return this;
        }

        public Builder setPrivacyLevel(PrivacyLevel privacyLevel) {
            this.privacyLevel = privacyLevel;
            return this;
        }

        public Builder setFileHash(String fileHash) {
            this.fileHash = fileHash;
            return this;
        }

        public Builder setDocumentId(String documentId) {
            this.documentId = documentId;
            return this;
        }

        public Annotation build() {
            return new Annotation(
                    id,
                    type,
                    previousId,
                    color,
                    text,
                    profileId,
                    positions,
                    created,
                    lastModified,
                    privacyLevel,
                    fileHash,
                    documentId);
        }
    }

    /**
     * Page number and coordinates of one {@link Annotation}
     */
    public static class Position implements Parcelable {
        public final Point topLeft;
        public final Point bottomRight;
        public final Integer page;

        public Position(Point topLeft, Point bottomRight, Integer page) {
            this.topLeft = topLeft;
            this.bottomRight = bottomRight;
            this.page = page;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Position position = (Position) o;

            if (bottomRight != null ? !bottomRight.equals(position.bottomRight) : position.bottomRight != null)
                return false;
            if (page != null ? !page.equals(position.page) : position.page != null) return false;
            if (topLeft != null ? !topLeft.equals(position.topLeft) : position.topLeft != null) return false;

            return true;
        }

        @Override
        public String toString() {
            return "Position{" +
                    "topLeft=" + topLeft +
                    ", bottomRight=" + bottomRight +
                    ", page=" + page +
                    '}';
        }

        @Override
        public int hashCode() {
            int result = topLeft != null ? topLeft.hashCode() : 0;
            result = 31 * result + (bottomRight != null ? bottomRight.hashCode() : 0);
            result = 31 * result + (page != null ? page.hashCode() : 0);
            return result;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(topLeft.x);
            dest.writeDouble(topLeft.y);
            dest.writeDouble(bottomRight.x);
            dest.writeDouble(bottomRight.y);
            dest.writeInt(page);
        }

        public static final Creator<Position> CREATOR = new Creator<Position>() {
            public Position createFromParcel(Parcel in) {
                return new Position(new Point(in.readDouble(), in.readDouble()), new Point(in.readDouble(), in.readDouble()), in.readInt());
            }

            public Position[] newArray(int size) {
                return new Position[size];
            }
        };

    }
}
