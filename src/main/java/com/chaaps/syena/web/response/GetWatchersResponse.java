package com.chaaps.syena.web.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetWatchersResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3358182997365163161L;
	private List<Entry> watchers;

	/**
	 * @return the watchers
	 */
	public List<Entry> getWatchers() {
		return watchers;
	}

	/**
	 * @param watchers
	 *            the watchers to set
	 */
	public void setWatchers(List<Entry> watchers) {
		this.watchers = watchers;
	}

	public void addEntry(String email, String name, boolean enabled, String status, Date watchingSince) {
		if (watchers == null)
			watchers = new ArrayList<>();
		watchers.add(new Entry(email, name, enabled, status, watchingSince));
	}

	static class Entry implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8090556785484270675L;
		String email;
		String name;
		boolean enabled;
		String status;
		Date watchingSince;

		Entry(String email, String name, boolean enabled, String status, Date watchingSince) {
			this.email = email;
			this.name = name;
			this.enabled = enabled;
			this.status = status;
			this.watchingSince = watchingSince;
		}

		/**
		 * @return the email
		 */
		public String getEmail() {
			return email;
		}

		/**
		 * @param email
		 *            the email to set
		 */
		public void setEmail(String email) {
			this.email = email;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param name
		 *            the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @return the enabled
		 */
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * @param enabled
		 *            the enabled to set
		 */
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		/**
		 * @return the watchingSince
		 */
		public Date getWatchingSince() {
			return watchingSince;
		}

		/**
		 * @param watchingSince
		 *            the watchingSince to set
		 */
		public void setWatchingSince(Date watchingSince) {
			this.watchingSince = watchingSince;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "{email: \"" + email + "\", name: " + name + "\", enabled: " + enabled + "\", watchingSince: "
					+ watchingSince + "}";
		}
	}
}
