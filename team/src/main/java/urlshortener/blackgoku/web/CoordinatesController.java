package urlshortener.blackgoku.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.blackgoku.domain.Coordinates;
import urlshortener.common.domain.CoordinatesHelper;
import urlshortener.common.domain.MessageHelper;

import javax.servlet.http.HttpServletRequest;


@RestController
public class CoordinatesController extends CoordinatesHelper{

    private static final Logger logger = LoggerFactory.getLogger(CoordinatesController.class);

    @RequestMapping(value = "/blockedCoordinates", method = RequestMethod.GET, produces = "application/json")
    public Coordinates getBlockedCoordinates(){
        logger.info("Detected petition to show the current blocked coordinates");
        return new Coordinates(blockedLatitude,blockedLongitude);
    }

    @RequestMapping(value = "/blockedCoordinates", method = RequestMethod.POST)
    public Object setBlockedCoordinates(@RequestParam(value = "latitude") String latitude,
                                        @RequestParam(value = "longitude") String longitude,
                                        HttpServletRequest request, RedirectAttributes ra){
        logger.info("Detected petition to set the current blocked coordinates to (" + (latitude!=null?latitude:"")
                    + "," + (longitude!=null?longitude:"") + ")");

        String user = (String) request.getSession().getAttribute("user");

        if(user != null &&  user.equals("admin@admin.com")){
            if(latitude != null && longitude != null){

                blockedLatitude = (latitude.equals("")?null:Double.parseDouble(latitude));
                blockedLongitude = (longitude.equals("")?null:Double.parseDouble(longitude));
            }

            return new ResponseEntity<>(HttpStatus.CREATED);
        } else{
            MessageHelper.addErrorAttribute(ra,"error.logged.other","");
            return new ModelAndView("redirect:/");
        }
    }
}
