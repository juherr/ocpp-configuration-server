package fr.uge.chargepointconfiguration.user;

import fr.uge.chargepointconfiguration.entities.User;

/**
 * DTO to read user in database.
 *
 * @param firstName first name of the user
 * @param lastName last name of the user
 * @param email email of the user
 * @param role role of the user
 */
public record UserDto(
      int id,
      String firstName,
      String lastName,
      String email,
      User.Role role) {
}
