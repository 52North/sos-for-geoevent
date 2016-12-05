@XmlSchema(namespace = "http://www.opengis.net/sampling/1.0", elementFormDefault = XmlNsForm.QUALIFIED, xmlns = {
		@XmlNs(prefix = "om", namespaceURI = "http://www.opengis.net/om/1.0"),
		@XmlNs(prefix = "gml", namespaceURI = "http://www.opengis.net/gml"),
		@XmlNs(prefix = "sa", namespaceURI = "http://www.opengis.net/sampling/1.0"),
		@XmlNs(prefix = "swe", namespaceURI = "http://www.opengis.net/swe/1.0.1"),
		@XmlNs(prefix = "xlink", namespaceURI = "http://www.w3.org/1999/xlink")})
package net.opengis.sa.x10;

import javax.xml.bind.annotation.*;