package edu.dmitry.geomap.rpc;

import cz.eman.jsonrpc.client.AbstractClientProxy;
import cz.eman.jsonrpc.client.ClientProvider;
import edu.dmitry.geomap.MapInteraction.LocationsStatisticRequest;
import edu.dmitry.geomap.MapInteraction.LocationsStatisticRespond;
import edu.dmitry.geomap.datamodel.LocationStatistic;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

public class ServerProxy extends AbstractClientProxy<MapMethods> implements MapMethods {
    private Logger logger = LogManager.getRootLogger();

    public ServerProxy(ClientProvider clientProvider) {
        super(MapMethods.class, clientProvider);
    }

    @Override
    public LocationsStatisticRespond getLocationsStatistic(LocationsStatisticRequest locationsStatisticRequest) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonArray = mapper.writeValueAsString(super.callMethod("getLocationsStatistic", locationsStatisticRequest));
            return mapper.readValue(jsonArray, LocationsStatisticRespond.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    @Override
    public LocationStatistic getFullLocationStatistic(LocationStatistic locationStatistic) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonArray = mapper.writeValueAsString(super.callMethod("getFullLocationStatistic", locationStatistic));
            return mapper.readValue(jsonArray, LocationStatistic.class);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }


}