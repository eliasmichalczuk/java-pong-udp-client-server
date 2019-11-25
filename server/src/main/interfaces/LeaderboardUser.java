package main.interfaces;

import java.io.Serializable;

public class LeaderboardUser implements Serializable {

	private static final long serialVersionUID = -132772524200481933L;
	public final String name;
	public int points;
	public final String password;

	public LeaderboardUser(
		String name,
		int points,
		String password
	) {
		this.name = name;
		this.points = points;
		this.password = password;
	}

	@Override
	public String toString() {
		return "LeaderboardUser [name=" + name + ", points=" + points + ", password=" + password + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
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
		LeaderboardUser other = (LeaderboardUser) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}
}
