package main;

public class PlayerClosedConnectionCallback {

	private Paddle main;
	private Paddle opponent;
	private Panel panel;
	private PlayerActionsHandler handler;

	public PlayerClosedConnectionCallback(Paddle main, Paddle opponent, Panel panel, PlayerActionsHandler handler) {
		this.main = main;
		this.opponent = opponent;
		this.panel = panel;
		this.handler = handler;
	}
}
