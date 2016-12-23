package net.opengis.sa.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SamplingPoint {

	@XmlAttribute(name = "id", namespace = "http://www.opengis.net/gml")
	private String id;
	
	@XmlElement(name = "name", namespace = "http://www.opengis.net/gml")
	private String name;
	
	@XmlElement(name = "description", namespace = "http://www.opengis.net/gml")
	private String description;
	
	@XmlElement(name = "position", namespace = "http://www.opengis.net/sampling/1.0")
	private Position position;
	

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
