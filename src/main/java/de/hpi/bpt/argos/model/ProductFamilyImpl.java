package de.hpi.bpt.argos.model;

import com.google.gson.Gson;

import java.util.Date;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ProductFamilyImpl implements ProductFamily {
	protected static final Gson serializer = new Gson();

	protected int numberOfDevices;
	protected int numberOfEvents;
	protected Date productionStart;
	protected ProductFamilyState state;
	protected String name;
	protected int id;
	protected ProductFamilyMetaData metaData;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfDevices() {
		return numberOfDevices;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNumberOfDevices(int numberOfDevices) {
		this.numberOfDevices = numberOfDevices;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
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
	public ProductFamilyState getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setState(ProductFamilyState state) {
		this.state = state;
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
	public int getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamilyMetaData getMetaData() {
		return metaData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMetaData(ProductFamilyMetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setExampleData() {
		setNumberOfDevices(1337);
		setNumberOfEvents(9001);
		setProductionStart(new Date(2199, 1, 1, 0, 0, 0));
		setState(ProductFamilyState.ERROR);
		setName("example family");
		setId(42);

		ProductFamilyMetaData exampleMetaData = new ProductFamilyMetaDataImpl();
		exampleMetaData.setBrand("Buderus");
		exampleMetaData.setLabel("product label 001");
		exampleMetaData.setOrderNumber(1234);
		exampleMetaData.setStatusDescription("everything is broken!");

		setMetaData(exampleMetaData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
