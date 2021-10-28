package ggc.core;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.Reader;

import ggc.core.exception.BadEntryException;
import ggc.core.exception.PartnerDoesNotExistException;
import ggc.core.exception.ProductDoesNotExistException;

public class Parser {

  // It could be WarehouseManager too. Or something else.
  private Warehouse _store;

  public Parser(Warehouse w) {
    _store = w;
  }

  void parseFile(String filename) throws IOException, BadEntryException, ProductDoesNotExistException, PartnerDoesNotExistException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;

      while ((line = reader.readLine()) != null)
        parseLine(line);
    }
  }

  private void parseLine(String line) throws BadEntryException, ProductDoesNotExistException, PartnerDoesNotExistException {
    String[] components = line.split("\\|");

    switch (components[0]) {
      case "PARTNER":
        parsePartner(components, line);
        break;
      case "BATCH_S":
        parseSimpleProduct(components, line);
        break;

      case "BATCH_M":
        parseAggregateProduct(components, line);
        break;
        
      default:
        throw new BadEntryException("Invalid type element: " + components[0]);
    }
  }

  //PARTNER|id|nome|endereço
  private void parsePartner(String[] components, String line) throws BadEntryException {
    if (components.length != 4)
      throw new BadEntryException("Invalid partner with wrong number of fields (4): " + line);
    
    String id = components[1];
    String name = components[2];
    String address = components[3];
    
    // add code here to
    // register partner with id, name, address in _store;
  }

  //BATCH_S|idProduto|idParceiro|preco|stock-actual
  private void parseSimpleProduct(String[] components, String line) throws BadEntryException, ProductDoesNotExistException, PartnerDoesNotExistException {
    if (components.length != 5)
      throw new BadEntryException("Invalid number of fields (4) in simple batch description: " + line);
    
    String idProduct = components[1];
    String idPartner = components[2];
    double price = Double.parseDouble(components[3]);
    int stock = Integer.parseInt(components[4]);

    if(!_store.existsProduct(idProduct)) {
      _store.addSimpleProduct(idProduct);
    }

    Product product = _store.getProduct(idProduct);
    Partner partner = _store.getPartner(idPartner);

    product.addBatch(price, stock, product, partner);
  }
 
    
  //BATCH_M|idProduto|idParceiro|prec ̧o|stock-actual|agravamento|componente-1:quantidade-1#...#componente-n:quantidade-n
  private void parseAggregateProduct(String[] components, String line) throws BadEntryException, NumberFormatException, ProductDoesNotExistException, PartnerDoesNotExistException {
    if (components.length != 7)
      throw new BadEntryException("Invalid number of fields (7) in aggregate batch description: " + line);
    
    String idProduct = components[1];
    String idPartner = components[2];

    ArrayList<Component> recipeComponents = new ArrayList<>();

    if(!_store.existsProduct(idProduct)) {
      for (String component : components[6].split("#")) {
        String[] recipeComponent = component.split(":");
        recipeComponents.add(new Component(Integer.parseInt(recipeComponent[0]), _store.getProduct(components[1])));

      }
    }
    
    AggregateProduct aggregatedProduct = new AggregateProduct(idProduct);
    Recipe recipe = new Recipe(Double.parseDouble(components[5]), aggregatedProduct, recipeComponents);
    aggregatedProduct.addRecipe(recipe);
    
    _store.addAggregateProduct(idProduct, aggregatedProduct);

    Product product = _store.getProduct(idProduct);
    Partner partner = _store.getPartner(idPartner);
    double price = Double.parseDouble(components[3]);
    int stock = Integer.parseInt(components[4]);

    product.addBatch(price, stock, product, partner);
  }

}
