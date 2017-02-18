package com.chaaps.syena.web.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetWatchersResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3358182997365163161L;
	private List<Member> watchers;

	/**
	 * @return the watchers
	 */
	public List<Member> getWatchers() {
		return watchers;
	}

	/**
	 * @param watchers
	 *            the watchers to set
	 */
	public void setWatchers(List<Member> watchers) {
		this.watchers = watchers;
	}

	public void addEntry(String email, String name) {
		if (watchers == null)
			watchers = new ArrayList<>();
		watchers.add(new Member(email, name));
	}

	class Member implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8090556785484270675L;
		String email;
		String name;

		Member(String email, String name) {
			this.email = email;
			this.name = name;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return "{email: \"" + email + "\", name: " + name + "\"}";
		}
	}
}
