/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package server.database;

import java.util.List;
import common.CredentialsMessage;
import server.User;

/**
 *
 * @author Mess
 */
public interface DAO {
    public boolean verifyUser(CredentialsMessage credentials);

    public boolean addUser(CredentialsMessage credentials);
    public List<User> getAllPlayer();
}
