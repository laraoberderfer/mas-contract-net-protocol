package demo;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.wrapper.AgentController;
import jade.domain.FIPANames;
import jade.util.leap.LinkedList;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.*;
import java.util.Date;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.Enumeration;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import demo.ContadorTempo;

/**
 * This example shows how to implement the initiator role in 
 * a FIPA-contract-net interaction protocol. In this case in particular 
 * we use a <code>ContractNetInitiator</code>  
 * to assign a dummy task to the agent that provides the best offer
 * among a set of agents (whose local
 * names must be specified as arguments).
 * @author Giovanni Caire - TILAB
 * 
 * -agents iniciator1:demo.ContractNetInitiatorAgent("resp1","resp2");
 * resp1:demo.ContractNetResponderAgent;
 * resp2:demo.ContractNetResponderAgent -gui
 * 
 * atualizado por Lara Popov Zambiasi Bazzi Oberderfer
 * em Abril de 2021
 */

public class ContractNetInitiatorAgent extends Agent {
	private static final long serialVersionUID = 1L;
	public int nResponders;
	public List<Comida> invetory = new ArrayList<Comida>();
	public List<AID> buyers = new ArrayList<AID>();

    protected void setup() {
        try {
            fillInvetory();
        } catch (IOException e) {
            e.printStackTrace();
        }
        findBuyers();
        addBehaviour(new SellItem(this, 100)); //new SellItem(this, 10000)
        //addBehaviour(new SellItem());
    }

    @Override
    protected void takeDown() {
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        Object[] args = getArguments();
        for (int i = 0; i < getArguments().length; ++i) {
            msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
        }
        msg.setContent("sale-ended");
        this.send(msg);
    }

    public class SellItem extends TickerBehaviour {
    //public class SellItem extends Behaviour {
        public SellItem(Agent a, long period) {
            super(a, period); // tick every minute
        }

        @Override
        public void onTick() {
       // public void action() {          	           
        	//Caso este método retorne TRUE o comportamento será finalizado
        	if (invetory.size() == 0) {
            	System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
            	System.out.println("     Restaurante "+this.myAgent.getName()+" finalizou as vendas.");
            	System.out.println(" - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
            	ContadorTempo.parar();
            	System.out.println("\n - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - ");
            	this.myAgent.doDelete();            	
            } else {
	            System.out.println("Restaurante "+this.myAgent.getName()+" vende {"+ invetory.get(0).getName() + " , "
	                    + invetory.get(0).getPrice() + "} para um dos " + nResponders + " clientes");
	            try {          	            		
	                    ACLMessage msg = new ACLMessage(ACLMessage.CFP);	
	                    
	                    for (int i = 0; i < buyers.size(); ++i) {	                    	
	                        msg.addReceiver(buyers.get(i));
	                    }		                    
	                    msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
	                    msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000)); //original era 10000
	                    msg.setContentObject(invetory.get(0)); //prato	
	                   
	                    //System.out.println("estou enviando: "+invetory.get(0).getName());
	                    //System.out.println("CFP iniciator: "+msg.getContent().hashCode());
	                    
	                    addBehaviour(new PerformRequest((ContractNetInitiatorAgent) myAgent, msg));	  
	                    //System.out.println("CFP conversa iniciator: "+msg.getConversationId());
	            } catch (IOException e) {
	                e.printStackTrace();	
	            }    
            }
        }
       
    }

    private void findBuyers() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            nResponders = args.length;
            for (int i = 0; i < args.length; ++i) {
                buyers.add(new AID((String) args[i], AID.ISLOCALNAME));
            }
        } else {
            System.out.println("Nao consegui encontrar clientes.");
        }
    }

    private void fillInvetory() throws IOException {
    	String itens = "";
    	List<String> domain = Arrays.asList();
    	
    	itens = "sushi";
    	domain = Arrays.asList(itens.split(","));
    	Comida item = new Comida("sushi", 30.3, domain);
    	invetory.add(item);
    	
    	itens = "mexicana";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("mexicana", 70.5, domain);
    	invetory.add(item);
    	
    	itens = "burguer";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("burguer", 15.2, domain);
    	invetory.add(item);
    	
    	itens = "fritas";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("fritas", 10.2, domain);
    	invetory.add(item);
    	
    	itens = "pizza";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("pizza", 50.3, domain);
    	invetory.add(item);
    	
    	itens = "massa";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("massa", 40.5, domain);
    	invetory.add(item);
    	
    	itens = "arabe";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("arabe", 20.3, domain);
    	invetory.add(item);
    	
    	itens = "churrasco";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("churrasco", 80.3, domain);
    	invetory.add(item);
    	
    	itens = "camarao";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("camarao", 60.3, domain);
    	invetory.add(item);
    	
    	itens = "tilapia";
    	domain = Arrays.asList(itens.split(","));
    	item = new Comida("tilapia", 20.3, domain);
    	invetory.add(item);
    }

    public void deleteComida(String name) {
        for (int index = 0; index < invetory.size(); index++) {
            if (invetory.get(index).getName().equals(name)) {
            	System.out.println("Excluindo item: "+invetory.get(index).getName());
                invetory.remove(index);                
            }
        }
    }

    public void increaseDiscount(String name){
        for(Comida a : invetory){
            if(a.getName().equals(name)){
                a.increaseDiscount();
            }
        }
    }
}