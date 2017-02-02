package de.hpi.bpt.argos.persistence.model.event;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductState;

import java.util.Date;

/**
 * This interface represents events which lead to changes in product states. It extends persistence entity.
 */
public interface UpdateProductStateEvent extends PersistenceEntity {

	/**
	 * This method returns the related product.
	 * @return - the related product
	 */
	Product getProduct();

	/**
	 * This method sets the related product.
	 * @param product - the related product
	 */
	void setProduct(Product product);

	/**
	 * This method get the state of the update state event.
	 * @return - the new state of the related product
	 */
	ProductState getState();

	/**
	 * This method sets the state of this update product state event.
	 * @param state - the new state of the related product to be set
	 */
	void setState(ProductState state);

	/**
	 * This methods return the timestamp of this update product state event.
	 * @return - the timestamp the update product state event
	 */
	Date getTimestamp();

	/**
	 * This method sets the timestamp of this update product state event.
	 * @param timestamp - the timestamp to be set
	 */
	void setTimestamp(Date timestamp);
}
