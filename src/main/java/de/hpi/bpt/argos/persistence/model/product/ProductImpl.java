package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
import de.hpi.bpt.argos.persistence.model.event.EventQueryImpl;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorType;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorTypeImpl;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "Product")
public class ProductImpl extends PersistenceEntityImpl implements Product {

	@ManyToOne(cascade = {CascadeType.ALL}, targetEntity = ProductFamilyImpl.class)
	@JoinColumn(name = "product_family_Id")
	protected ProductFamily productFamily;

	@Column(name = "ProductionStart")
	protected Date productionStart = new Date();

	@Column(name = "Name")
	protected String name = "Unknown Product";

	@Column(name = "OrderNumber")
	protected long orderNumber = 0;

	@Column(name = "NumberOfDevices")
	protected long numberOfDevices = 0;

	@Column(name = "NumberOfEvents")
	protected long numberOfEvents = 0;

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = ProductConfigurationImpl.class)
	protected Set<ProductConfiguration> productConfigurations = new HashSet<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily() {
		return productFamily;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductFamily(ProductFamily productFamily) {
		this.productFamily = productFamily;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getProductionStart() {
		return productionStart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductionStart(Date productionStart) {
		this.productionStart = productionStart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getOrderNumber() {
		return orderNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrderNumber(long orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getNumberOfDevices() {
		return numberOfDevices;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getNumberOfEvents() {
		return numberOfEvents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incrementNumberOfEvents(long count) {
		this.numberOfEvents += count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incrementNumberOfDevices(long count) {
		this.numberOfDevices += count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ProductConfiguration> getProductConfigurations() {
		return productConfigurations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductConfigurations(Set<ProductConfiguration> productConfigurations) {
		this.productConfigurations = productConfigurations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addProductConfiguration(ProductConfiguration productConfiguration) {
		productConfigurations.add(productConfiguration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductConfiguration getProductConfiguration(int codingPlugId, float codingPlugSoftwareVersion) {
		for (ProductConfiguration configuration : productConfigurations) {
			if (configuration.getCodingPlugId() == codingPlugId
					&& configuration.supports(codingPlugSoftwareVersion)) {
				return configuration;
			}
		}

		return null;
	}
}
