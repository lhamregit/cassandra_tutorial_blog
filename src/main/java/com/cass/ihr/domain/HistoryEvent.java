package com.cass.ihr.domain;

import java.io.Serializable;
import java.util.Date;

public class HistoryEvent implements Serializable {

	private static final long serialVersionUID = 2649413489226205671L;

	private Integer id;
	private transient Integer profileId;
	private transient Date date;
	private String message;

	public HistoryEvent() {

	}

	public HistoryEvent(Integer id, String message) {
		super();
		this.id = id;
		this.message = message;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result
				+ ((profileId == null) ? 0 : profileId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HistoryEvent other = (HistoryEvent) obj;

		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;

		return true;
	}

	@Override
	public String toString() {
		return "HistoryEvent [message=" + message + ", id=" + id + ", id=" + id
				+ ", profileId=" + profileId + ", date=" + date + "]";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
