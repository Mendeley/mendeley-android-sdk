package com.mendeley.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mendeley.sdk.util.NullableList;
import com.mendeley.sdk.util.ParcelableUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Model class representing profile json object.
 */
public class Profile implements Parcelable {

    public final String id;
    public final String displayName;
    public final String userType;
    public final String url;
    public final String email;
    public final String link;
    public final String firstName;
    public final String lastName;
    public final String researchInterests;
    public final String academicStatus;
    public final String title;
    public final Boolean verified;
    public final Boolean marketing;
    public final Date createdAt;
    public final Boolean isMe;

    public final Discipline discipline;
    public final NullableList<Photo> photos;

    public final Institution institutionDetails;
    public final NullableList<Education> education;
    public final NullableList<Employment> employment;

    public Profile(
            String id,
            String displayName,
            String userType,
            String url,
            String email,
            String link,
            String firstName,
            String lastName,
            String title, String researchInterests,
            String academicStatus,
            Boolean verified,
            Boolean marketing,
            Date createdAt,
            Discipline discipline,
            List<Photo> photos,
            List<Education> education,
            Institution institutionDetails,
            List<Employment> employment,
            boolean isMe) {

        this.id = id;
        this.displayName = displayName;
        this.userType = userType;
        this.url = url;
        this.email = email;
        this.link = link;
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.researchInterests = researchInterests;
        this.academicStatus = academicStatus;
        this.verified = verified;
        this.marketing = marketing;
        this.createdAt = createdAt;
        this.discipline = discipline;
        this.photos = new NullableList<>(photos);
        this.institutionDetails = institutionDetails;
        this.education = new NullableList<>(education);
        this.employment = new NullableList<>(employment);
        this.isMe = isMe;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        ParcelableUtils.writeOptionalBooleanToParcel(parcel, isMe);
        parcel.writeString(id);
        ParcelableUtils.writeOptionalStringToParcel(parcel, displayName);
        ParcelableUtils.writeOptionalStringToParcel(parcel, userType);
        ParcelableUtils.writeOptionalStringToParcel(parcel, url);
        ParcelableUtils.writeOptionalStringToParcel(parcel, email);
        ParcelableUtils.writeOptionalStringToParcel(parcel, link);
        ParcelableUtils.writeOptionalStringToParcel(parcel, firstName);
        ParcelableUtils.writeOptionalStringToParcel(parcel, lastName);
        ParcelableUtils.writeOptionalStringToParcel(parcel, researchInterests);
        ParcelableUtils.writeOptionalStringToParcel(parcel, academicStatus);
        ParcelableUtils.writeOptionalStringToParcel(parcel, title);
        ParcelableUtils.writeOptionalBooleanToParcel(parcel, verified);
        ParcelableUtils.writeOptionalBooleanToParcel(parcel, marketing);
        ParcelableUtils.writeOptionalDateToParcel(parcel, createdAt);
        ParcelableUtils.writeOptionalParcelableToParcel(parcel, discipline, 0);
        ParcelableUtils.writeOptionalParcelableToParcel(parcel, institutionDetails, 0);
        parcel.writeList(photos);
        parcel.writeList(education);
        parcel.writeList(employment);
    }

    public static final Creator<Profile> CREATOR = new Creator<Profile>() {
        @Override
        public Profile createFromParcel(Parcel in) {
            Profile.Builder builder = new Profile.Builder()
                    .setIsMe(ParcelableUtils.readOptionalBooleanFromParcel(in))
                    .setId(in.readString())
                    .setDisplayName(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setUserType(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setUrl(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setEmail(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setLink(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setFirstName(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setLastName(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setResearchInterests(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setAcademicStatus(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setTitle(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setVerified(ParcelableUtils.readOptionalBooleanFromParcel(in))
                    .setMarketing(ParcelableUtils.readOptionalBooleanFromParcel(in))
                    .setCreatedAt(ParcelableUtils.readOptionalDateFromParcel(in))
                    .setDiscipline((Discipline) ParcelableUtils.readOptionalParcelableFromParcel(in, Discipline.class.getClassLoader()))
                    .setInstitutionDetails((Institution) ParcelableUtils.readOptionalParcelableFromParcel(in, Institution.class.getClassLoader()));

            final List<Photo> photos = new ArrayList<>();
            in.readList(photos, Photo.class.getClassLoader());
            builder.setPhotos(photos);

            final List<Education> education = new ArrayList<>();
            in.readList(education, Education.class.getClassLoader());
            builder.setEducation(education);

            final List<Employment> employment = new ArrayList<>();
            in.readList(employment, Employment.class.getClassLoader());
            builder.setEmployment(employment);

            return builder.build();
        }

        @Override
        public Profile[] newArray(int size) {
            return new Profile[size];
        }
    };

    @Override
    public String toString() {
        return " id: " + id +
                "  firstName: " + firstName +
                ", lastName: " + lastName;
    }

    public static class Builder {
        private String id;
        private String displayName;
        private String userType;
        private String url;
        private String email;
        private String link;
        private String firstName;
        private String lastName;
        private String researchInterests;
        private String academicStatus;
        private Boolean verified;
        private Boolean marketing;
        private Date createdAt;
        private Discipline discipline;
        private List<Photo> photos;
        private List<Education> education;
        private List<Employment> employment;
        private Institution institutionDetails = new Institution.Builder().build();
        private String title;
        private boolean isMe;

        public Builder() {
        }

        public Builder(Profile from) {
            this.id = from.id;
            this.displayName = from.displayName;
            this.userType = from.userType;
            this.url = from.url;
            this.email = from.email;
            this.link = from.link;
            this.firstName = from.firstName;
            this.lastName = from.lastName;
            this.title = from.title;
            this.researchInterests = from.researchInterests;
            this.academicStatus = from.academicStatus;
            this.verified = from.verified;
            this.marketing = from.marketing;
            this.createdAt = from.createdAt;
            this.discipline = from.discipline;
            this.photos = from.photos;
            this.institutionDetails = from.institutionDetails;
            this.education = from.education == null ? new ArrayList<Education>() : from.education;
            this.employment = from.employment == null ? new ArrayList<Employment>() : from.employment;
            this.isMe = from.isMe;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder setUserType(String userType) {
            this.userType = userType;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setLink(String link) {
            this.link = link;
            return this;
        }

        public Builder setFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder setLastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setResearchInterests(String researchInterests) {
            this.researchInterests = researchInterests;
            return this;
        }

        public Builder setAcademicStatus(String academicStatus) {
            this.academicStatus = academicStatus;
            return this;
        }

        public Builder setVerified(Boolean verified) {
            this.verified = verified;
            return this;
        }

        public Builder setMarketing(Boolean marketing) {
            this.marketing = marketing;
            return this;
        }

        public Builder setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setDiscipline(Discipline discipline) {
            this.discipline = discipline;
            return this;
        }

        public Builder setPhotos(List<Photo> photos) {
            this.photos = photos;
            return this;
        }

        public Builder setEducation(List<Education> education) {
            this.education = education;
            return this;
        }

        public Builder setEmployment(List<Employment> employment) {
            this.employment = employment;
            return this;
        }

        public Builder setInstitutionDetails(Institution institutionDetails) {
            this.institutionDetails = institutionDetails;
            return this;
        }

        public Builder setIsMe(Boolean isMe) {
            this.isMe = isMe;
            return this;
        }

        public Profile build() {
            return new Profile(
                    id,
                    displayName,
                    userType,
                    url,
                    email,
                    link,
                    firstName,
                    lastName,
                    title,
                    researchInterests,
                    academicStatus,
                    verified,
                    marketing,
                    createdAt,
                    discipline,
                    photos,
                    education,
                    institutionDetails,
                    employment,
                    isMe);
        }
    }

    /**
     * Model class representing the metadata for an image.
     */
    public static class Photo implements Parcelable{

        public final Integer width;
        public final Integer height;
        public final String url;
        public final boolean original;

        public Photo(
                Integer width,
                Integer height,
                String url,
                boolean original) {

            this.width = width;
            this.height = height;
            this.url = url;
            this.original = original;
        }

        public static final Creator<Photo> CREATOR = new Creator<Photo>() {

            @Override
            public Photo createFromParcel(Parcel parcel) {
                return new Builder()
                        .setWidth(ParcelableUtils.readOptionalIntegerFromParcel(parcel))
                        .setHeight(ParcelableUtils.readOptionalIntegerFromParcel(parcel))
                        .setUrl(ParcelableUtils.readOptionalStringFromParcel(parcel))
                        .setOriginal(parcel.readInt()==1)
                        .build();
            }

            @Override
            public Photo[] newArray(int size) {
                return new Photo[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int flags) {
            ParcelableUtils.writeOptionalIntegerToParcel(parcel, width);
            ParcelableUtils.writeOptionalIntegerToParcel(parcel, height);
            ParcelableUtils.writeOptionalStringToParcel(parcel, url);
            parcel.writeInt(original?1:0);
        }

        public static class Builder {
            private Integer width;
            private Integer height;
            private String url;
            private boolean original;

            public Builder() {
            }

            public Builder(Photo from) {
                this.width = from.width;
                this.height = from.height;
                this.url = from.url;
                this.original = from.original;
            }

            public Builder setWidth(Integer width) {
                this.width = width;
                return this;
            }

            public Builder setHeight(Integer height) {
                this.height = height;
                return this;
            }

            public Builder setUrl(String url) {
                this.url = url;
                return this;
            }

            public Builder setOriginal(boolean original) {
                this.original = original;
                return this;
            }

            public Photo build() {
                return new Photo(
                        width,
                        height,
                        url,
                        original);
            }
        }
    }
}
