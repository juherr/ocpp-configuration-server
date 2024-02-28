package fr.uge.chargepointconfiguration.logs.business;

import fr.uge.chargepointconfiguration.chargepoint.Chargepoint;
import fr.uge.chargepointconfiguration.chargepoint.ChargepointDto;
import fr.uge.chargepointconfiguration.logs.sealed.BusinessLog;
import fr.uge.chargepointconfiguration.user.User;
import fr.uge.chargepointconfiguration.user.UserDto;
import java.sql.Timestamp;

/**
 * DTO to read business log in database.
 *
 * @param id Database id of the log stored.
 * @param date Date when the log had been created.
 * @param user User logged currently implied with this log, null if not.
 * @param chargepoint Chargepoint implied with this log, null if not.
 * @param category {@link BusinessLog.Category}
 * @param completeLog All the log in one String.
 */
public record BusinessLogDto(
    int id,
    Timestamp date,
    UserDto user,
    ChargepointDto chargepoint,
    BusinessLog.Category category,
    String level,
    String completeLog) {
}