package urlshortener.blackgoku.web.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import urlshortener.blackgoku.domain.User;
import urlshortener.blackgoku.repository.UserRepository;
import urlshortener.blackgoku.web.ws.schema.RegisterRequest;
import urlshortener.blackgoku.web.ws.schema.RegisterResponse;

/**
 * Created by Cristina on 21/12/2016.
 */
@Endpoint
public class RegisterEndpoint {

    private static final String NAMESPACE_URI = "http://urlshortener/blackgoku/web/ws/schema";

    private UserRepository userRepository;

    @Autowired
    public RegisterEndpoint(UserRepository userRepository){ this.userRepository = userRepository;

    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "registerRequest")
    @ResponsePayload
    public RegisterResponse register (@RequestPayload RegisterRequest request){

        RegisterResponse response = new RegisterResponse();
        User usuario = new User(request.getEMailUser(),request.getPasswordUser());

        if(userRepository.save(usuario)){
            response.setRegistradoCorrectamente("Se ha registrado correctamente el usuario con email: "+ request.getEMailUser());
        } else{
            //error al registrarse
            response.setRegistradoCorrectamente("ERROR al registrar al usuario con email: "+ request.getEMailUser());
        }
        return response;
    }
}