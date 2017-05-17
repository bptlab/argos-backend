package de.hpi.bpt.argos.parsing.staticData;

import de.hpi.bpt.argos.parsing.XMLSubParser;

/**
 * This interface offers methods to parse artifacts from a XML file.
 */
public interface ArtifactParser<ArtifactType> extends XMLSubParser {

	/**
	 * This method returns the parent artifact of the currently parsed artifact.
	 * @return - the parent artifact of the currently parsed artifact
	 */
	ArtifactType getParentArtifact();

	/**
	 * This method returns the artifact this parser is currently parsing.
	 * @return - the artifact this parser is currently parsing
	 */
	ArtifactType getArtifact();

	/**
	 * This method finishes the parsing of the current artifact.
	 */
	void finish();
}
