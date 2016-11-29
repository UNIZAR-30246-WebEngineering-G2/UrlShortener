package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.blackgoku.domain.Coordinates;
import urlshortener.common.domain.CoordinatesHelper;


@RestController
public class CoordinatesController extends CoordinatesHelper{

    private static final Logger logger = LoggerFactory.getLogger(CoordinatesController.class);

    @RequestMapping(value = "/blockedCoordinates", method = RequestMethod.GET, produces = "application/json")
    public Coordinates getBlockedCoordinates(){
        logger.info("Detected petition to show the current blocked coordinates");
        return new Coordinates(blockedLatitude,blockedLongitude);
    }

    @RequestMapping(value = "/blockedCoordinates", method = RequestMethod.POST)
    public ResponseEntity<?> setBlockedCoordinates(@RequestParam(value = "latitude") String latitude,
                                                   @RequestParam(value = "longitude") String longitude){
        logger.info("Detected petition to set the current blocked coordinates to (" + (latitude!=null?latitude:"")
                    + "," + (longitude!=null?longitude:"") + ")");
        if(latitude != null && longitude != null){

            blockedLatitude = (latitude.equals("")?null:Double.parseDouble(latitude));
            blockedLongitude = (longitude.equals("")?null:Double.parseDouble(longitude));
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
