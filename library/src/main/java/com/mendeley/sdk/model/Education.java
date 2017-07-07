package com.mendeley.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mendeley.sdk.util.ParcelableUtils;

import java.util.Date;

/**
 * Model class representing education json object.
 */
public class Education implements Parcelable {

    public final String id;
    public final Institution institution;
    public final String degree;
    public final Date startDate;
    public final Date endDate;
    public final String website;

    public static final Creator<Education> CREATOR = new Creator<Education>() {

        @Override
        public Education createFromParcel(Parcel in) {
            return new Builder()
                    .setId(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setInstitution((Institution) ParcelableUtils.readOptionalParcelableFromParcel(in, Institution.class.getClassLoader()))
                    .setDegree(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setStartDate(ParcelableUtils.readOptionalDateFromParcel(in))
                    .setEndDate(ParcelableUtils.readOptionalDateFromParcel(in))
                    .setWebsite(ParcelableUtils.readOptionalStringFromParcel(in))
                    .build();
        }

        @Override
        public Education[] newArray(int size) {
            return new Education[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        ParcelableUtils.writeOptionalStringToParcel(parcel, id);
        ParcelableUtils.writeOptionalParcelableToParcel(parcel, institution, 0);
        ParcelableUtils.writeOptionalStringToParcel(parcel, degree);
        ParcelableUtils.writeOptionalDateToParcel(parcel, startDate);
        ParcelableUtils.writeOptionalDateToParcel(parcel, endDate);
        ParcelableUtils.writeOptionalStringToParcel(parcel, website);

    }

    private Education(
            String id,
            Institution institution,
            String degree,
            Date startDate,
            Date endDate,
            String website) {
        this.id = id;
        this.institution = institution;
        this.degree = degree;
        this.startDate = startDate;
        this.endDate = endDate;
        this.website = website;
    }

    public static class Builder {
        private String id;
        private Institution institution;
        private String degree;
        private Date startDate;
        private Date endDate;
        private String website;

        public Builder() {
        }

        public Builder(Education from) {
            this.id = from.id;
            this.institution = from.institution;
            this.degree = from.degree;
            this.startDate = from.startDate;
            this.endDate = from.endDate;
            this.website = from.website;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setInstitution(Institution institution) {
            this.institution = institution;
            return this;
        }

        public Builder setDegree(String degree) {
            this.degree = degree;
            return this;
        }

        public Builder setStartDate(Date startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(Date endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setWebsite(String website) {
            this.website = website;
            return this;
        }

        public Education build() {
            return new Education(
                    id,
                    institution,
                    degree,
                    startDate,
                    endDate,
                    website);
        }
    }
}
