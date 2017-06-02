package de.hpi.bpt.argos.parsing.staticData;

import de.hpi.bpt.argos.parsing.XMLFileParser;
import de.hpi.bpt.argos.parsing.XMLSubParserImpl;

/**
 * {@inheritDoc}
 * This is an abstract base class.
 */
public abstract class ArtifactParserImpl<ArtifactType> extends XMLSubParserImpl implements ArtifactParser<ArtifactType> {

	protected ArtifactType parentArtifact;
	protected ArtifactType artifact;
	protected EntityTypeList entityTypes;

	/**
	 * This constructor initializes all members with their given values.
	 * @param parentParser - the parentParser parser to be set
	 * @param entityTypes - all parsed entityTypes
	 * @param parentArtifact - the parent artifact of the artifact, this parser is responsible for
	 */
	public ArtifactParserImpl(XMLFileParser parentParser, EntityTypeList entityTypes, ArtifactType parentArtifact) {
		super(parentParser);
		this.parentArtifact = parentArtifact;
		this.entityTypes = entityTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArtifactType getParentArtifact() {
		return parentArtifact;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArtifactType getArtifact() {
		return artifact;
	}
}
