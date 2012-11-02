@Grab('org.codehaus.groovy.modules:groovyws:0.5.1') 
import groovyx.net.ws.WSServer

println "ready to start"
double add(double arg0, double arg1) {
  return (arg0 + arg1)
}
double square(double arg0) {
  return (arg0 * arg0)
}

def server = new WSServer()

server.setNode("MathService", "http://localhost:6980/MathService")

server.start()
println "server is starting"
