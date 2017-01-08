package urlshortener.blackgoku.web;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import urlshortener.common.domain.CoordinatesHelper;
import urlshortener.common.domain.MessageHelper;
import urlshortener.blackgoku.domain.User;
import urlshortener.blackgoku.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
public class UserController extends CoordinatesHelper {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    protected UserRepository userRepo;

    @RequestMapping(value="/")
    public ModelAndView home(HttpServletRequest request){

        String usuario = (String) request.getSession().getAttribute("user");
        if(usuario!=null){
            logger.info("Detected registered user, redirecting to shortener");
            ModelAndView mav = new ModelAndView("shortener");
            mav.addObject("blockedLatitude", blockedLatitude);
            mav.addObject("blockedLongitude", blockedLongitude);
            return mav;
        } else{
            logger.info("Detected unregistered user, redirecting to register");
            return new ModelAndView("register");
        }
    }

    @RequestMapping(value="/register", method= RequestMethod.POST)
    public ModelAndView register(@RequestParam("email")String email,
                                 @RequestParam("password")String password,
                                 HttpServletRequest request,
                                 RedirectAttributes ra){
     /**   logger.info("Detected petition to register user "+email);
        User usuario = new User(email,password);

        if(userRepo.save(usuario)){
            request.getSession().setAttribute("user",usuario.getEmail());
            MessageHelper.addSuccessAttribute(ra,"success.register",email);
        } else{
            //error al registrarse
            MessageHelper.addErrorAttribute(ra,"error.register",email);
        }

        return new ModelAndView("redirect:/");*/

        String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
                "                  xmlns:gs=\"http://urlshortener/blackgoku/web/ws/schema\">\n" +
                "    <soapenv:Header/>\n" +
                "    <soapenv:Body>\n" +
                "        <gs:registerRequest>\n" +
                "            <gs:eMailUser>" + email + "</gs:eMailUser>\n" +
                "            <gs:passwordUser>" + password + "</gs:passwordUser>\n" +
                "        </gs:registerRequest>\n" +
                "    </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        try{
            HttpClient client = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost("http://localhost:8080/ws");

            StringEntity input =  new StringEntity(xml);
            input.setContentType("text/xml");
            post.setEntity(input);


            HttpResponse response = client.execute(post);

            BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

            String output;
            String respuesta = "";

            while ((output = br.readLine()) != null) {
                respuesta += output;
            }

            if(respuesta.contains("Se ha registrado correctamente el usuario")){
                request.getSession().setAttribute("user",email);
                MessageHelper.addSuccessAttribute(ra,"success.register",email);
            } else{
                //error al registrarse
                MessageHelper.addErrorAttribute(ra,"error.register",email);
            }

        }catch (Exception e){
            MessageHelper.addErrorAttribute(ra,"error.register",email);
        }

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value="/login", method=RequestMethod.POST)
    public ModelAndView login(@RequestParam("email")String email,
                              @RequestParam("password")String password,
                              HttpServletRequest request,
                              RedirectAttributes ra){
        logger.info("Detected petition to login user "+email);
        User usuario = userRepo.findByEmail(email);
        if(usuario != null){
            if(usuario.getPassword().equals(password)){
                logger.info("User " + email + " entered valid credentials");
                request.getSession().setAttribute("user",usuario.getEmail());
            } else{
                MessageHelper.addErrorAttribute(ra,"error.login.password",email);
                logger.error("Wrong password for user " + email);
            }
        } else{
            MessageHelper.addErrorAttribute(ra,"error.login.email",email);
            logger.error("User "+ email + " not found");
        }

        return new ModelAndView("redirect:/");
    }

    @RequestMapping(value="/logout")
    public ModelAndView logout(HttpServletRequest request,
                               RedirectAttributes ra){
        logger.info("Detected petition to logout");
        MessageHelper.addSuccessAttribute(ra,"success.session.close","");
        request.getSession().invalidate();
        return new ModelAndView("redirect:/");
    }
}
