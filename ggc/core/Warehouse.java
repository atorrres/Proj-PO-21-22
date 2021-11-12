package ggc.core;

// FIXME import classes (cannot import from pt.tecnico or ggc.app)

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.IOException;

import ggc.core.exception.BadEntryException;
import ggc.core.exception.ImportFileException;
import ggc.core.exception.NegativeDaysException;
import ggc.core.exception.PartnerDoesNotExistException;
import ggc.core.exception.PartnerKeyAlreadyExistException;
import ggc.core.exception.ProductDoesNotExistException;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  /** Serial number for serialization. */
  private static final long serialVersionUID = 202109192006L;

  private static final String List = null;
  
  private double _balance;
  private Date _date = new Date();
  private Map<String, Product> _products = new HashMap<String, Product>();
  private Map<String, Partner> _partners = new HashMap<String, Partner>();
  private Map<String, Transaction> _transactions = new HashMap<String, Transaction>();

  /**
   * @param txtfile filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   * @throws PartnerKeyAlreadyExistException
   * @throws PartnerDoesNotExistException
   * @throws ProductDoesNotExistException
   * @throws ImportFileException
   */
  void importFile(String txtfile) throws ImportFileException {
    Parser parser = new Parser(this);
    try {
      parser.parseFile(txtfile);
    }
    catch (Exception e) {
      throw new ImportFileException();
    }
  }

  /**
   * @return current Date.
   */
  Date getCurrentDate() {
    return _date;
  }

  /**
   * @param days number of days to advance current Date.
   * @throws NegativeDaysException
   */
  void advanceDate(int days) throws NegativeDaysException {
    _date.add(days);
    for (Partner p : this.getPartners()) {
      p.verifyPaymentPeriod(_date);
    }
  }

  public void addTransaction(Transaction t) {
    _transactions.put("" + t.getID(), t);
  }

  /**
   * @return warehouse's products.
   */
  Collection<Product> getProducts() {
    return _products.values();
  }

  /**
   * @param id a product id.
   * @return true if product exists, false otherwise.
   */
  boolean existsProduct(String id) {
    return _products.containsKey(id.toUpperCase());
  }

  /**
   * @param id a product id.
   * @param simpleProduct the new simple product.
   */
  void addProduct(Product product) {
    _products.put(product.getID().toUpperCase(), product);
  }

  void addBatch(String productID, Batch batch) throws ProductDoesNotExistException {
    try {
      this.getProduct(productID).addBatch(batch, batch.getPrice());
    } catch(ProductDoesNotExistException pdne) {
      throw pdne;
    }
  }

  void changeBalance(double money) {
    _balance += money;
  }

  /**
   * @param id a product id.
   * @throws ProductDoesNotExistException
   * @return product with id id.
   */
  Product getProduct(String id) throws ProductDoesNotExistException {
    Product p = _products.get(id.toUpperCase());
    
    if(p == null)
      throw new ProductDoesNotExistException();
    return p;
  }

  /**
   * @return warehouse's current batches.
   */
  Collection<Batch> getBatches() {
    List<Batch> batches = new ArrayList<>();
    
    _products.forEach( (id, product)-> batches.addAll(product.getBatches()));
    return batches;
  }
  
  /**
   * @param id a partner id.
   * @throws PartnerDoesNotExistException
   * @return partner's batches.
   */
  Collection<Batch> getBatchesByPartner(String id) throws PartnerDoesNotExistException {
    List<Batch> batches = new ArrayList<>();
    
    batches.addAll(getPartner(id).getBatches());
    return batches;
  }
  
  /**
   * @param id a product id.
   * @throws ProductDoesNotExistException
   * @return product's batches.
   */
  Collection<Batch> getBatchesByProduct(String id) throws ProductDoesNotExistException {
    List<Batch> batches = new ArrayList<>();
    
    batches.addAll(getProduct(id).getBatches());
    return batches;
  }

  /**
   * @param id a partner id.
   * @throws PartnerDoesNotExistException
   */
  Partner getPartner(String id) throws PartnerDoesNotExistException {
    Partner p = _partners.get(id.toUpperCase());

    if(p == null)
      throw new PartnerDoesNotExistException(id);
    return p;
  }

  /**
   * @return warehouse's partners.
   */
  Collection<Partner> getPartners() {
    return _partners.values();
  }

  /**
   * @param id a partner id.
   * @param name the partner name.
   * @param adress the partner address.
   * @throws PartnerKeyAlreadyExistException
   */
  void addPartner(String id, String name, String address) 
      throws PartnerKeyAlreadyExistException {
    String ID = id.toUpperCase();

    if(_partners.containsKey(ID))
        throw new PartnerKeyAlreadyExistException();
    _partners.put(ID, new Partner(id, name, address));
  }

}
