package com.mendeley.api.model;

import com.mendeley.api.model.File.Builder;

/**
 * Model class representing folder json object.
 *
 */
public class Folder {

	public final String name;
	public final String id;
	public final String parentId;
	public final String groupId;
	public final String added;
	
	
	private Folder(String name, String id, String parentId, String groupId, String added) {
        this.name = name;
        this.id = id;
        this.parentId = parentId;
        this.groupId = groupId;
        this.added = added;
    }
	
	public static class Builder {
        private String name;
        private String id;
        private String parentId;
        private String groupId;
        private String added;

        public Builder() {}

        public Builder setName(String name) {
            this.name = name;
            return this;
        }
        
        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder setGroupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder setAdded(String added) {
            this.added = added;
            return this;
        }

        public Folder build() {
            return new Folder(
            		name,
                    id,
                    parentId,
                    groupId,
                    added
            );
        }
    }

	
	@Override
	public String toString() {
		return "name: " + name + 
			   ", parentId: " + parentId +
			   ", id: " + id + 
			   ", groupId: " + groupId + 
			   ", added: " + added;
	}
	
	@Override
	public boolean equals(Object object) {
		
		Folder other;
		
		try {
			other = (Folder) object;
		}
		catch (ClassCastException e) {
			return false;
		}
		
		if (other == null) {
			return false;
		} else {
			return other.id.equals(this.id);
		}
	}
}
