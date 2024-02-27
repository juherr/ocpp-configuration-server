package fr.uge.chargepointconfiguration.user;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint for user management.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * retrieve info in database for a given user.
   *
   * @param id The id of the user.
   * @return Details about the user.
   */
  @GetMapping("/{id}")
  public UserDto getUserById(@PathVariable int id) {
    // TODO : exception BAD REQUEST si id est pas un nombre
    System.out.println("getUser " + id);
    return userService.getUserById(id).toDto();
  }

  /**
   * retrieve info in database for all users.
   *
   * @return Details about all the users.
   */
  @GetMapping("/all")
  public List<UserDto> getAllUsers() {
    return userService.getAllUsers()
            .stream()
            .map(User::toDto)
            .toList();
  }

  /**
   * retrieve info in database for the current user.
   *
   * @return Details about the current user.
   */
  @GetMapping("/me")
  public UserDto getAuthenticatedUser() {
    return userService.getAuthenticatedUser().toDto();
  }

  /**
   * Update the password of the user.
   *
   * @param changePasswordUserDto a ChangePassworddUserDto.
   * @return a ResponseEntity of User.
   */
  @PostMapping("/updatePassword")
  public ResponseEntity<User> postNewPasswordUser(
          @Parameter(
                  name = "JSON with old and new password",
                  description = "Old and new password",
                  example = """
                          {
                            "oldPassword": "String",
                            "newPassword": "String"
                          }""",
                  required = true)
          @RequestBody ChangePasswordUserDto changePasswordUserDto) {
    var user = userService.updatePassword(changePasswordUserDto);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * Updadate the role of the user.
   *
   * @param changeRoleUserDto a ChangeRoleUserDto.
   * @return a ResponseEntity of User.
   */
  @PostMapping("/updateRole")
  public ResponseEntity<User> postUpdateRoleUser(
          @Parameter(
                  name = "JSON with id and new role of the user",
                  description = "Update the role of the user",
                  example = """
                          {
                            "id": "int",
                            "role": "String"
                          }
                          """,
                  required = true)
          @RequestBody ChangeRoleUserDto changeRoleUserDto) {
    var user = userService.updateRole(changeRoleUserDto);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}
