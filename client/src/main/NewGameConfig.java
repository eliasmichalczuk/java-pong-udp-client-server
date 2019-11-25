package main;

import java.util.InputMismatchException;
import java.util.Scanner;

public class NewGameConfig extends Thread {

	private Panel panel;
	private Paddle player;

	public NewGameConfig(Panel panel, Paddle player) {
		this.panel = panel;
		this.player = player;
	}
	
	@Override
	public void run() {
        int roundNumbers = -1, points = -1;
        while (true) {
        	Scanner scan = new Scanner(System.in);
        	try {
        		System.out.println("Insert max round (1 min 10 max): ");
        		roundNumbers = scan.nextInt();
        		System.out.println("Insert max score (1 min 10 max): ");
        		points = scan.nextInt();
        	} catch (InputMismatchException e)  {
        		System.out.println("Only numbers for round and score. ");
        	}
        	if (roundNumbers <= 10 && roundNumbers >= 0 && points <= 10 && points >= 0) {
            	scan.close();
            	break;
        	}
        	scan.close();
        }
        if (roundNumbers == 0 || points == 0) {
        	this.panel.newMaxRound = 0;
            this.panel.newMaxScore = 0;
        	this.player.insertingNewConfig = false;
        } else {
        	this.panel.newMaxRound = roundNumbers;
            this.panel.newMaxScore = points;
        }
	}
}
