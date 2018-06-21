package com.skoussou.brms;

import org.kie.api.definition.type.Position;

public class Question {
		@Position(0)
		private String entity;

		@Position(1)
		private String location;

		public Question(String entity, String location) {
			this.entity = entity;
			this.location = location;
		}

		public String getEntity() {
			return entity;
		}

		public void setEntity(String entity) {
			this.entity = entity;
		}

		public String getLocation() {
			return location;
		}

		public void setLocation(String location) {
			this.location = location;
		}


		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			Question location1 = (Question) o;

			if (entity != null ? !entity.equals(location1.entity)
					: location1.entity != null) {
				return false;
			}
			if (location != null ? !location.equals(location1.location)
					: location1.location != null) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			int result = entity != null ? entity.hashCode() : 0;
			result = 31 * result + (location != null ? location.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "Location{" + "entity='" + entity + '\'' + ", location='"
					+ location + '\'' + '}';
		}
}

