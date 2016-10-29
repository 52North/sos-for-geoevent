package org.n52.sensorweb.sos.adapter;

import java.util.ArrayList;
import java.util.List;

import com.esri.ges.adapter.AdapterDefinitionBase;
import com.esri.ges.adapter.AdapterType;
import com.esri.ges.core.AccessType;
import com.esri.ges.core.ConfigurationException;
import com.esri.ges.core.geoevent.DefaultFieldDefinition;
import com.esri.ges.core.geoevent.DefaultGeoEventDefinition;
import com.esri.ges.core.geoevent.FieldDefinition;
import com.esri.ges.core.geoevent.FieldType;
import com.esri.ges.core.geoevent.GeoEventDefinition;

public class SOSInboundAdapterDefinition extends AdapterDefinitionBase
{
  public SOSInboundAdapterDefinition()
  {
    super(AdapterType.INBOUND);
    try
    {
      GeoEventDefinition md = new DefaultGeoEventDefinition();
      md.setName("SOSGeoEventDefinition");
      md.setAccessType(AccessType.editable);
      List<FieldDefinition> fieldDefinitions = new ArrayList<FieldDefinition>();
      fieldDefinitions.add(new DefaultFieldDefinition("track_id", FieldType.Long));
      fieldDefinitions.add(new DefaultFieldDefinition("location", FieldType.Geometry));
      md.setFieldDefinitions(fieldDefinitions);
      geoEventDefinitions.put(md.getName(), md);
    }
    catch (ConfigurationException ex)
    {
      ;
    }
  }

  @Override
  public String getName()
  {
    return "SOS";
  }

  @Override
  public String getLabel()
  {
    /**
     * Note: by using the ${myBundle-symbolic-name.myProperty} notation, the
     * framework will attempt to replace the string with a localized string in
     * your properties file.
     * 
     */
    return "${n52.sensorweb.sos-adapter.ADAPTER_IN_LABEL}";
  }

  @Override
  public String getDomain()
  {
    return "sos.adapter.inbound";
  }

  @Override
  public String getDescription()
  {
    /**
     * Note: by using the ${myBundle-symbolic-name.myProperty} notation, the
     * framework will attempt to replace the string with a localized string in
     * your properties file.
     */
    return "${n52.sensorweb.sos-adapter.ADAPTER_IN_DESC}";
  }

  @Override
  public String getContactInfo()
  {
    return "s.drost@52north.com";
  }

  @Override
  public String getVersion()
  {
    return "10.4.1";
  }
}
