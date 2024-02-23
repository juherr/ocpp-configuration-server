package fr.uge.chargepointconfiguration.chargepoint;

import fr.uge.chargepointconfiguration.configuration.Configuration;
import fr.uge.chargepointconfiguration.firmware.Firmware;
import fr.uge.chargepointconfiguration.status.Status;

/**
 * DTO to create configuration in database.
 *
 * @param serialNumberChargepoint The chargepoint's unique serial id.
 * @param type                    The commercial name of the chargepoint.
 * @param constructor             The chargepoint's manufacturer.
 * @param clientId                The client's name of the chargepoint.
 * @param serverAddress           The server's URL of the chargepoint.
 * @param configuration           A JSON containing the chargepoint's configuration.
 * @param status                  {@link Status}.
 * @param firmware                {@link Firmware}.
 */
public record CreateChargepointDto(
    String serialNumberChargepoint,
    String type,
    String constructor,
    String clientId,
    String serverAddress,
    int configuration,
    int status,
    int firmware) {

}