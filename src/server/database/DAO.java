/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package server.database;

import java.util.List;
import common.CredentialsMessage;
import common.Player;
import java.sql.SQLException;

/**
 * @author Mess
 */
public interface DAO {
    public boolean verifyUser(CredentialsMessage credentials);

    public boolean insertUser(CredentialsMessage credentials);
    public List<Player> getAllPlayer();
    public int getPlayerWin(String username);
    public int getPlayerMatch(String username);
    public double getPlayerResponseTime(String username);
    public void getPlayersMatchHistory(String username); /* ho messo void ma credo sia una lista */
    public void insertText(String title, int length, String path, String analysis) throws SQLException;
}
