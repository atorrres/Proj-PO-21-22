package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.app.exception.UnknownPartnerKeyException;
import ggc.app.exception.UnknownProductKeyException;
import ggc.core.WarehouseManager;
import ggc.core.exception.PartnerDoesNotExistException;
//FIXME import classes
import ggc.core.exception.ProductDoesNotExistException;

/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("productID", Message.requestProductKey());
    addStringField("pricePerUnit", Message.requestPrice());
    addIntegerField("amount", Message.requestAmount());
    addStringField("partnerID", Message.requestPartnerKey());
  }

  @Override
  public final void execute() throws CommandException {
    String productID = stringField("productID");
    double pricePerUnit = Double.parseDouble(stringField("pricePerUnit"));
    int amount = integerField("amount");
    String partnerID = stringField("partnerID");

    try {
      _receiver.addAcquisition(pricePerUnit, amount, _receiver.getProduct(productID), _receiver.getPartner(partnerID));
    } catch(ProductDoesNotExistException pdne) {
      throw new UnknownProductKeyException(productID);
    } catch(PartnerDoesNotExistException pdne) {
      throw new UnknownPartnerKeyException(partnerID);
    } 
  }
}
