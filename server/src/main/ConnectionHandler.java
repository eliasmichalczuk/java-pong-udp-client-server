package main;

import java.util.ArrayList;
import java.util.List;

public class ConnectionHandler {
	
	/* Lista contendo joagadores disconectados, utilizada para saber quando
	 * jogadores saem das partidas.
	 */
	public List<Paddle> disconnectedPlayers = new ArrayList<Paddle>();
	
	/* Lista contendo todos os jogadores que estão conectados nos jogos
	 * devem ser removidos da lista quando disconectam
	 */
	private List<Panel> connectedPlayerPanels = new ArrayList<Panel>();

	public ConnectionHandler() {
	}
	
	public synchronized void playersLeftRemovePanel(Panel p) {
		this.connectedPlayerPanels.remove(p);
	}
	
	public synchronized void addNewGamePanel(Panel p) {
		this.connectedPlayerPanels.add(p);
	}
	
	public synchronized void addDisConnectedPlayer(Paddle p) {
		this.disconnectedPlayers.add(p);
		searchPairsOfAlonePlayers();
	}
	
	public synchronized void removeDisConnectedPlayer(Paddle p) {
		this.disconnectedPlayers.remove(p);
	}
	
	private synchronized void searchPairsOfAlonePlayers() {
		List<Panel> alonePlayersPanel = new ArrayList<Panel>();
		this.connectedPlayerPanels.forEach(p -> {
			if ((p.getMainPlayer().actuallyConnected() && !p.getOtherPlayer().actuallyConnected())
				|| (p.getOtherPlayer().actuallyConnected() && !p.getMainPlayer().actuallyConnected())) {
				alonePlayersPanel.add(p);
			}
		});
		while (alonePlayersPanel.size() > 1) {
			Panel p1 = alonePlayersPanel.remove(0);
			Panel p2 = alonePlayersPanel.remove(0);
			Paddle playerFromPanel2 = null;

			if (p2.getMainPlayer().actuallyConnected()) {
				playerFromPanel2 = p2.getMainPlayer();
			} else {
				playerFromPanel2 = p2.getOtherPlayer();
			}
			
			if (p1.getMainPlayer().actuallyConnected()) {
				p1.setOtherPlayer(playerFromPanel2); 
			} else {
				p1.setMainPlayer(playerFromPanel2);
			}
			
			p1.setZeroState();
			p2.getDisconnectedPlayer().otherConnectedPlayerMovedPanel = true;
		}
	}
}
