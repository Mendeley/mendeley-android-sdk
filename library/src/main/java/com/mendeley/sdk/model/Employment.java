package com.mendeley.sdk.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.mendeley.sdk.util.ParcelableUtils;

import java.util.Date;

/**
 * Model class representing employment json object.
 *
 */
public class Employment implements Parcelable, Epochable {

	public final String id;
	public final Institution institution;
	public final String position;
	public final String website;
	public final Date startDate;
	public final Date endDate;
	public final Boolean isMainEmployment;

	public static final Creator<Employment> CREATOR = new Creator<Employment>() {

		@Override
		public Employment createFromParcel(Parcel in) {
			return new Builder()
					.setId(ParcelableUtils.readOptionalStringFromParcel(in))
					.setInstitution((Institution) ParcelableUtils.readOptionalParcelableFromParcel(in, Institution.class.getClassLoader()))
					.setPosition(ParcelableUtils.readOptionalStringFromParcel(in))
					.setWebsite(ParcelableUtils.readOptionalStringFromParcel(in))
					.setStartDate(ParcelableUtils.readOptionalDateFromParcel(in))
					.setEndDate(ParcelableUtils.readOptionalDateFromParcel(in))
					.setIsMainEmployment(ParcelableUtils.readOptionalBooleanFromParcel(in))
					.build();
		}

		@Override
		public Employment[] newArray(int size) {
			return new Employment[size];
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
		ParcelableUtils.writeOptionalStringToParcel(parcel, position);
		ParcelableUtils.writeOptionalStringToParcel(parcel, website);
		ParcelableUtils.writeOptionalDateToParcel(parcel, startDate);
		ParcelableUtils.writeOptionalDateToParcel(parcel, endDate);
		ParcelableUtils.writeOptionalBooleanToParcel(parcel, isMainEmployment);
	}

	private Employment(
			String id,
			Institution institution,
			String position,
			String website,
			Date startDate,
			Date endDate,
			Boolean isMainEmployment) {

		this.id = id;
		this.institution = institution;
		this.position = position;
		this.startDate = startDate;
		this.endDate = endDate;
		this.website = website;
		this.isMainEmployment = isMainEmployment;
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
		private Institution institution;
		private String website;
		private String position;
		private Date startDate;
		private Date endDate;
		private Boolean isMainEmployment;

		public Builder() {}

		public Builder(Employment from) {
			this.id = from.id;
			this.institution = from.institution;
			this.position = from.position;
			this.startDate = from.startDate;
			this.endDate = from.endDate;
			this.website = from.website;
			this.isMainEmployment = from.isMainEmployment;
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public Builder setInstitution(Institution institution) {
			this.institution = institution;
			return this;
		}

		public Builder setPosition(String position) {
			this.position = position;
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

		public Builder setIsMainEmployment(Boolean isMainEmployment) {
			this.isMainEmployment = isMainEmployment;
			return this;
		}

		public Employment build() {
			return new Employment(
					id,
					institution,
					position,
					website,
					startDate,
					endDate,
					isMainEmployment);
		}
	}
}
