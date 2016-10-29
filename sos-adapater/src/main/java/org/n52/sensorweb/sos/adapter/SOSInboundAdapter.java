package org.n52.sensorweb.sos.adapter;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.messaging.MessagingException;

public class SOSInboundAdapter extends InboundAdapterBase
{
  /**
   * Initialize the i18n Bundle Logger
   * 
   * See {@link BundleLogger} for more info.
   */
  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(SOSInboundAdapter.class);

  public SOSInboundAdapter(AdapterDefinition definition) throws ComponentException
  {
    super(definition);
  }

  @Override
  public GeoEvent adapt(ByteBuffer buffer, String channelId)
  {
    buffer.mark();
    try
    {
      // This is how you get a single byte of data.
      // byte singleByte = buffer.get();

      // This is how you would get a fixed number of bytes from the buffer.
      byte[] data = new byte[10];
      buffer.get(data);

      // Create an instance of the message using the guid that we generated when
      // we started up.
      GeoEvent msg;
      try
      {
        msg = geoEventCreator.create(((AdapterDefinition) definition).getGeoEventDefinition("SampleGeoEventDefinition").getGuid());
        LOGGER.info("CREATED_MSG");
      }
      catch (MessagingException e)
      {
        return null;
      }

      // Populate the message with all the attribute values.
      int i = 0;
      msg.setField(i++, 1);
      double x = 1.0;
      double y = 1.0;
      int wkid = 4326;
      msg.setField(i++, new MapGeometry(new Point(x, y), SpatialReference.create(wkid)));
      LOGGER.info("POPULATED_FIELDS_SUCCESSFULLY");
      return msg;
    }
    catch (BufferUnderflowException ex)
    {
      buffer.reset();
      return null;
    }
    catch (FieldException e)
    {
      // log the error message. String key (message to look up in properties
      // file), exception to log, values to replace within the message (in
      // order).
      LOGGER.error("FIELD_SET_ERROR", e, e.getMessage());
      return null;
    }
  }
}
