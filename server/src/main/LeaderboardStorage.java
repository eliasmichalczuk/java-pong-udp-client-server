package main;

import java.util.ArrayList;
import java.util.List;

import main.interfaces.LeaderboardUser;

public class LeaderboardStorage {

	public List<LeaderboardUser> storage = new ArrayList<LeaderboardUser>();
	
	public synchronized void addPointsToPaddle(Paddle p) {
		boolean foundUser = false;
		if (storage.isEmpty()) {
			return;
		}
		for (int i = 0; i < storage.size(); i++) {
			if (storage.get(i).name == p.name && storage.get(i).password == p.password) {
				++storage.get(i).points;
				foundUser = true;
			}
		}
		if (!foundUser) {
			this.storage.add(new LeaderboardUser(p.name, 1, p.password));
		}
	}
	
	public synchronized List<LeaderboardUser> getTenBestPlayers() {
		return this.storage;
	}
}
