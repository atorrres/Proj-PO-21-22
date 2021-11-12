package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import java.util.ArrayList;
import java.util.List;

import ggc.app.exception.UnknownPartnerKeyException;
import ggc.core.Acquisition;
import ggc.core.WarehouseManager;
import ggc.core.exception.PartnerDoesNotExistException;
import ggc.core.Partner;
//FIXME import classes

/**
 * Show all transactions for a specific partner.
 */
class DoShowPartnerAcquisitions extends Command<WarehouseManager> {

  DoShowPartnerAcquisitions(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER_ACQUISITIONS, receiver);
    addStringField("id", Message.requestPartnerKey());
  }

  @Override
  public void execute() throws CommandException {
    String id = stringField("id");
    List<String> acquisitionsToString = new ArrayList<>();

    try {
      for(Acquisition acq : _receiver.getPartner(id).getAcquisitions()) {
        acquisitionsToString.add(acq.toString());
      }
    } 
    catch (PartnerDoesNotExistException pdnee) {
      throw new UnknownPartnerKeyException(id);
    }

    _display.popup(acquisitionsToString);
  }
}
