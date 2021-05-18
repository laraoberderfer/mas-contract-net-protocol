/*
 * 
 * -agents iniciator1:demo.ContractNetInitiatorAgent("resp1","resp2");
 * resp1:demo.ContractNetResponderAgent;
 * resp2:demo.ContractNetResponderAgent -gui
 * */
import java.util.ArrayList;
import java.util.List;

import demo.ContadorTempo;

/**
 *  Starts JADE main container with several agents
 * 
 *  @author Lara P Z B Oberderfer
 */
public class StartJade {    
    public static void main(String[] args) throws Exception {
    	ContadorTempo.comecar();
    	
    	long tempoInicio = System.currentTimeMillis();
    	int nIniciator = 1; //total de iniciators
    	int nResponders = 10; //total de responders
    	List aux1 = new ArrayList();
    	List aux2 = new ArrayList();    	
    	
    	for (int i=1; i<=nResponders; i++) {
    		aux1.add("cliente"+i+"");
			aux2.add("cliente"+i+":demo.ContractNetResponderAgent");
        }
    	
    	String aux3 = String.join(",", aux1);
    	String aux4 = String.join(";", aux2);
    	String aux5 = "";
    	
    	for (int i=1; i<=nIniciator; i++) {
    		aux5 = aux5+"restaurante"+i+":demo.ContractNetInitiatorAgent("+aux3+");";
    	}
    	
    	aux5 = aux5+aux4;
    	
    	System.out.println(aux5);
    	
    	String[] parametros = {"-gui", "-localhost", "127.0.0.1", aux5,};   	
    	
    	jade.Boot.main(parametros);
    	
    	ContadorTempo.comecar();
    }
}

