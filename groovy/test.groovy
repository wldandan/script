import javax.jws.WebService 
import javax.jws.WebMethod 
import javax.jws.soap.SOAPBinding 
import javax.xml.ws.Endpoint 

@WebService(targetNamespace="hello") 
@SOAPBinding(parameterStyle=SOAPBinding.ParameterStyle.BARE) 
  class CircleFunctions { 
    @WebMethod 
      double getArea(double radius) { 
        Math.PI * (radius * radius) 
      } 
    @WebMethod 
      double getCircumference(double radius) { 
        2 * Math.PI * radius 
      } 
  } 

Endpoint.publish("http://localhost:8080/WebServiceExample/circlefunctions", new CircleFunctions()) 

