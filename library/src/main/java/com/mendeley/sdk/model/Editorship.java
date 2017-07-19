package com.mendeley.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mendeley.sdk.util.ParcelableUtils;

import java.util.Date;

public class Editorship implements Parcelable, Epochable {

    public final String id;
    public final Date created;
    public final String position;
    public final String journal;
    public final Date startDate;
    public final Date endDate;

    private Editorship(String id, Date created, String position, String journal, Date startDate, Date endDate) {
        this.id = id;
        this.created = created;
        this.position = position;
        this.journal = journal;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Date getStartDate() {
        return this.startDate;
    }

    @Override
    public Date getEndDate() {
        return this.endDate;
    }

    public static class Builder {
        private String id;
        private Date created;
        private String position;
        private String journal;
        private Date startDate;
        private Date endDate;

        public Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setCreated(Date created) {
            this.created = created;
            return this;
        }

        public Builder setPosition(String position) {
            this.position = position;
            return this;
        }

        public Builder setJournal(String journal) {
            this.journal = journal;
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

        public Editorship build() {
            return new Editorship(
                    id,
                    created,
                    position,
                    journal,
                    startDate,
                    endDate);
        }
    }

    public static final Creator<Editorship> CREATOR = new Creator<Editorship>() {

        @Override
        public Editorship createFromParcel(Parcel in) {
            return new Builder()
                    .setId(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setCreated(ParcelableUtils.readOptionalDateFromParcel(in))
                    .setPosition(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setJournal(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setStartDate(ParcelableUtils.readOptionalDateFromParcel(in))
                    .setEndDate(ParcelableUtils.readOptionalDateFromParcel(in))
                    .build();
        }

        @Override
        public Editorship[] newArray(int size) {
            return new Editorship[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        ParcelableUtils.writeOptionalStringToParcel(parcel, id);
        ParcelableUtils.writeOptionalDateToParcel(parcel, created);
        ParcelableUtils.writeOptionalStringToParcel(parcel, position);
        ParcelableUtils.writeOptionalStringToParcel(parcel, journal);
        ParcelableUtils.writeOptionalDateToParcel(parcel, startDate);
        ParcelableUtils.writeOptionalDateToParcel(parcel, endDate);
    }

}
