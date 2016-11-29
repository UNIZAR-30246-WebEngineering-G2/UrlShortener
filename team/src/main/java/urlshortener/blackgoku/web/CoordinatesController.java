package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener.common.domain.CoordinatesHelper;

/**
 * Created by Fran Menendez Moya on 28/11/2016.
 */
@RestController
public class CoordinatesController extends CoordinatesHelper{

    private static final Logger logger = LoggerFactory.getLogger(CoordinatesController.class);

    @RequestMapping(value = "/blockedLatitude", method = RequestMethod.GET)
    public String getBlockedLatitude(){
        logger.info("Detected petition to show the current blocked latitude");
        return (blockedLatitude!=null?blockedLatitude.toString():"None");
    }

    @RequestMapping(value = "/blockedLongitude", method = RequestMethod.GET)
    public String getBlockedLongitude(){
        logger.info("Detected petition to show the current blocked longitude");
        return (blockedLongitude!=null?blockedLongitude.toString():"None");
    }

    @RequestMapping(value = "/blockedLatitude", method = RequestMethod.POST)
    public ResponseEntity<?> updateBlockedLatitude(@RequestParam(value = "latitude", required = false) String latitude){
        logger.info("Detected petition to modify the current blocked latitude to: " + (latitude!=null?latitude:""));
        if(latitude != null && !latitude.equals("")){
            blockedLatitude = Double.parseDouble(latitude);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            blockedLatitude = null;
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/blockedLongitude", method = RequestMethod.POST)
    public ResponseEntity<?> updateBlockedLongitude(@RequestParam(value = "longitude", required = false) String longitude){
        logger.info("Detected petition to modify the current blocked longitude to: " + (longitude!=null?longitude:""));
        if(longitude != null && !longitude.equals("")){
            blockedLongitude = Double.parseDouble(longitude);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } else {
            blockedLongitude = null;
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
