package fr.uge.chargepointconfiguration.chargepoint.ocpp.ocpp2;

import fr.uge.chargepointconfiguration.WebSocketHandler;
import fr.uge.chargepointconfiguration.chargepoint.ChargePointManager;
import fr.uge.chargepointconfiguration.chargepoint.OcppMessageSender;
import fr.uge.chargepointconfiguration.chargepoint.ocpp.OcppMessage;
import fr.uge.chargepointconfiguration.chargepoint.ocpp.OcppObserver;
import fr.uge.chargepointconfiguration.chargepoint.ocpp.RegistrationStatus;
import fr.uge.chargepointconfiguration.entities.Chargepoint;
import fr.uge.chargepointconfiguration.entities.Status;
import fr.uge.chargepointconfiguration.repository.ChargepointRepository;
import fr.uge.chargepointconfiguration.repository.FirmwareRepository;
import fr.uge.chargepointconfiguration.repository.StatusRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Defines the OCPP configuration message for the visitor.
 */
public class OcppConfigurationObserver2 implements OcppObserver {
  private final OcppMessageSender sender;
  private final ChargepointRepository chargepointRepository;
  private final FirmwareRepository firmwareRepository;
  private final StatusRepository statusRepository;
  private final Queue<SetVariablesRequest20> queue = new LinkedList<>();
  private Chargepoint currentChargepoint;

  /**
   * Constructor for the OCPP 2.0 configuration observer.
   *
   * @param sender websocket channel to send message
   * @param chargepointRepository charge point repository
   * @param firmwareRepository firmware repository
   * @param statusRepository charge point status repository
   */
  public OcppConfigurationObserver2(OcppMessageSender sender,
                                    ChargepointRepository chargepointRepository,
                                    FirmwareRepository firmwareRepository,
                                    StatusRepository statusRepository) {
    this.sender = sender;
    this.chargepointRepository = chargepointRepository;
    this.firmwareRepository = firmwareRepository;
    this.statusRepository = statusRepository;
  }

  @Override
  public void onMessage(OcppMessage ocppMessage,
                        ChargePointManager chargePointManager) {
    switch (ocppMessage) {
      case BootNotificationRequest20 b -> processBootNotification(b, chargePointManager);
      // TODO : Add switch case for the update firmware response and status firmware request.
      default -> {
        // Do nothing
      }
    }
  }

  @Override
  public void onConnection(ChargePointManager chargePointManager) {

  }

  @Override
  public void onDisconnection(ChargePointManager chargePointManager) {

  }

  private void processBootNotification(
          BootNotificationRequest20 bootNotificationRequest,
          ChargePointManager chargePointManager) {

    // Get charge point from database
    currentChargepoint = chargepointRepository.findBySerialNumberChargepointAndConstructor(
            bootNotificationRequest.chargingStation().serialNumber(),
            bootNotificationRequest.chargingStation().vendorName()
    );
    // If charge point is not found then skip it
    if (currentChargepoint == null) {
      var response = new BootNotificationResponse20(
              LocalDateTime.now().toString(),
              5,
              RegistrationStatus.Rejected
      );
      sender.sendMessage(response, chargePointManager);
      return;
    }
    var status = currentChargepoint.getStatus();
    status.setState(true);
    status.setStatus(Status.StatusProcess.PENDING);
    status.setLastUpdate(new Timestamp(System.currentTimeMillis()));
    currentChargepoint.setStatus(status);
    chargepointRepository.save(currentChargepoint);
    // Dispatch information to users
    WebSocketHandler.sendMessageToUsers("Charge point x connected !");
    // Send BootNotification Response
    var response = new BootNotificationResponse20(
            LocalDateTime.now().toString(),
            5,
            RegistrationStatus.Accepted
    );
    sender.sendMessage(response, chargePointManager);
    switch (status.getStep()) {
      case Status.Step.CONFIGURATION -> processConfigurationRequest(chargePointManager);
      case Status.Step.FIRMWARE -> processFirmwareRequest(chargePointManager);
      default -> {
        // ignore
      }
    }
  }

  private void processConfigurationRequest(ChargePointManager chargePointManager) {
    if (queue.isEmpty()) {
      var status = currentChargepoint.getStatus();
      status.setStatus(Status.StatusProcess.FINISHED);
      status.setLastUpdate(new Timestamp(System.currentTimeMillis()));
      currentChargepoint.setStatus(status);
      chargepointRepository.save(currentChargepoint);
    }
  }

  private void processFirmwareRequest(ChargePointManager chargePointManager) {
    var status = currentChargepoint.getStatus();
    status.setStatus(Status.StatusProcess.PROCESSING);
    status.setLastUpdate(new Timestamp(System.currentTimeMillis()));
    currentChargepoint.setStatus(status);
    chargepointRepository.save(currentChargepoint);
    // TODO : Send a update firmware request !
    status = currentChargepoint.getStatus();
    status.setStatus(Status.StatusProcess.PENDING);
    status.setLastUpdate(new Timestamp(System.currentTimeMillis()));
    status.setStep(Status.Step.CONFIGURATION);
    currentChargepoint.setStatus(status);
    chargepointRepository.save(currentChargepoint);
    processConfigurationRequest(chargePointManager);
  }
}