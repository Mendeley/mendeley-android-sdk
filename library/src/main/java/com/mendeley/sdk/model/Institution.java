package com.mendeley.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mendeley.sdk.util.NullableList;
import com.mendeley.sdk.util.ParcelableUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing institution json object.
 *
 */
public class Institution implements Parcelable{

    public final int scivalId;
    public final String id;
    public final String name;
    public final String city;
    public final String state;
    public final String country;
    public final String parentId;
    public final String profilerUrl;
    public final NullableList<String> urls;
    public final NullableList<AlternativeName> altNames;

    public static final Creator<Institution> CREATOR = new Creator<Institution>() {

        @Override
        public Institution createFromParcel(Parcel in) {
            Institution.Builder builder = new Builder()
                    .setScivalId(in.readInt())
                    .setId(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setName(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setCity(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setState(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setCountry(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setParentId(ParcelableUtils.readOptionalStringFromParcel(in))
                    .setProfilerUrl(ParcelableUtils.readOptionalStringFromParcel(in));

            final ArrayList<String> urls = new ArrayList<>();
            in.readStringList(urls);
            builder.setUrls(urls);

            final ArrayList<AlternativeName> alternativeNames = new ArrayList<>();
            in.readTypedList(alternativeNames, AlternativeName.CREATOR);
            builder.setAltNames(alternativeNames);

            return builder.build();
        }

        @Override
        public Institution[] newArray(int size) {
            return new Institution[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(scivalId);
        ParcelableUtils.writeOptionalStringToParcel(parcel, id);
        ParcelableUtils.writeOptionalStringToParcel(parcel, name);
        ParcelableUtils.writeOptionalStringToParcel(parcel, city);
        ParcelableUtils.writeOptionalStringToParcel(parcel, state);
        ParcelableUtils.writeOptionalStringToParcel(parcel, country);
        ParcelableUtils.writeOptionalStringToParcel(parcel, parentId);
        ParcelableUtils.writeOptionalStringToParcel(parcel, profilerUrl);
        parcel.writeStringList(urls);
        parcel.writeTypedList(altNames);
    }

    public Institution(
            int scivalId,
            String id,
            String name,
            String city,
            String state,
            String country,
            String parentId,
            List<String> urls,
            String profilerUrl,
            List<AlternativeName> altNames) {

        this.scivalId = scivalId;
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.country = country;
        this.parentId = parentId;
        this.urls = new NullableList<String>(urls);
        this.profilerUrl = profilerUrl;
        this.altNames = new NullableList<AlternativeName>(altNames);
    }

    public static class Builder {
        private int scivalId;
        private String id;
        private String name;
        private String city;
        private String state;
        private String country;
        private String parentId;
        private List<String> urls;
        private String profilerUrl;
        private List<AlternativeName> altNames;

        public Builder() {}

        public Builder(Institution from) {
            this.scivalId = from.scivalId;
            this.id = from.id;
            this.name = from.name;
            this.city = from.city;
            this.state = from.state;
            this.country = from.country;
            this.parentId = from.parentId;
            this.urls = from.urls==null?new ArrayList<String>():from.urls;
            this.profilerUrl = from.profilerUrl;
            this.altNames = from.altNames==null?new ArrayList<AlternativeName>():from.altNames;
        }

        public Builder setScivalId(int scivalId) {
            this.scivalId = scivalId;
            return this;
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setCity(String city) {
            this.city = city;
            return this;
        }

        public Builder setState(String state) {
            this.state = state;
            return this;
        }

        public Builder setCountry(String country) {
            this.country = country;
            return this;
        }

        public Builder setParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder setUrls(List<String> urls) {
            this.urls = urls;
            return this;
        }

        public Builder setProfilerUrl(String profilerUrl) {
            this.profilerUrl = profilerUrl;
            return this;
        }

        public Builder setAltNames(List<AlternativeName> altNames) {
            this.altNames = altNames;
            return this;
        }

        public Institution build() {
            return new Institution(
                scivalId,
                id,
                name,
                city,
                state,
                country,
                parentId,
                urls,
                profilerUrl,
                altNames);
        }
    }
}
