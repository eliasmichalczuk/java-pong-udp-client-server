package main.interfaces;

import java.io.Serializable;

public class BallLocalizationValues implements Serializable {

	public final int x, y;
	
	public BallLocalizationValues(int x, int y) {
		this.y = y;
		this.x = x;
	}
}
