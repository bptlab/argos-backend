package de.hpi.bpt.argos.model;

import com.google.gson.Gson;

import java.util.Date;

public class ProductFamilyImpl implements ProductFamily {
	protected static final Gson serializer = new Gson();

	protected int numberOfDevices;
	protected int numberOfEvents;
	protected Date productionStart;
	protected ProductFamilyState state;
	protected String name;
	protected int id;
	protected ProductFamilyMetaData metaData;

	@Override
	public int getNumberOfDevices() {
		return numberOfDevices;
	}

	@Override
	public void setNumberOfDevices(int numberOfDevices) {
		this.numberOfDevices = numberOfDevices;
	}

	@Override
	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	@Override
	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}

	@Override
	public Date getProductionStart() {
		return productionStart;
	}

	@Override
	public void setProductionStart(Date productionStart) {
		this.productionStart = productionStart;
	}

	@Override
	public ProductFamilyState getState() {
		return state;
	}

	@Override
	public void setState(ProductFamilyState state) {
		this.state = state;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public ProductFamilyMetaData getMetaData() {
		return metaData;
	}

	@Override
	public void setMetaData(ProductFamilyMetaData metaData) {
		this.metaData = metaData;
	}

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

	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
