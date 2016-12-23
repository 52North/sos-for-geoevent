package net.opengis.sa.x10;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import net.opengis.gml.Point;

/**
 * @author <a href="mailto:s.drost@52north.org">Sebastian Drost</a>
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class Position {
	
	@XmlElement(name="Point", namespace="http://www.opengis.net/gml")
	private Point point;
	
	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

}
