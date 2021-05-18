package demo;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import java.util.Enumeration;
import java.util.Vector;

public class PerformRequest extends ContractNetInitiator {
    public Comida currentComida;
    public int currentRefuse = 0;
    public int currentRejectd = 0;

    public PerformRequest(ContractNetInitiatorAgent a, ACLMessage cfp) {
        super(a, cfp);
        try {
            this.currentComida = (Comida) cfp.getContentObject();         
            
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
    }

    protected void handleFailure(ACLMessage failure) {
        if (failure.getSender().equals(myAgent.getAMS())) {
            // FAILURE notification from the JADE runtime: the receiver
            // does not exist
            System.out.println("Cliente não existe");
        } else {
            System.out.println("Agent " + failure.getSender().getName() + " falhou");
        }
        // Immediate failure --> we will not receive a response from this agent
        //((ContractNetInitiatorAgent)myAgent).nResponders--;
    }

    protected void handleRefuse(ACLMessage refuse) {
        currentRefuse++;
    }

    protected void handleAllResponses(Vector responses, Vector acceptances) {       	    	
    	
        if (responses.size() < ((ContractNetInitiatorAgent)myAgent).nResponders) {        	
           System.out.println("Timeout expired: faltam " + (((ContractNetInitiatorAgent)myAgent).nResponders - responses.size()) + " clientes responderem o CNP");
        }
        // Evaluate proposals.
        double bestProposal = -1.0;
        AID bestProposer = null;
        ACLMessage accept = null;
        Enumeration e = responses.elements();
        
        while (e.hasMoreElements()) {        	
            ACLMessage msg = (ACLMessage) e.nextElement();
            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                acceptances.addElement(reply);
                //System.out.println("----------------CFP conversa iniciator: "+msg.getConversationId());
                double proposal = Double.parseDouble(msg.getContent());
                if(proposal > currentComida.getPrice()){
                    if (proposal > bestProposal) {
                        bestProposal = proposal;
                        bestProposer = msg.getSender();
                        accept = reply;
                    }
                }
            } //else {
              //  currentRejectd++;
           // }
        }
        // Accept the proposal of the best proposer
        if (accept != null) {        	
            System.out.println("Aceitando a proposta R$ " + bestProposal + " do cliente " + bestProposer.getName());
            accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        } //else if (accept == null && currentRefuse == currentRejectd){
         //   ((ContractNetInitiatorAgent)myAgent).increaseDiscount(currentComida.getName());
            
       // }
    }

    protected void handleInform(ACLMessage inform) {    	
    	System.out.println("Cliente " + inform.getSender().getName() + " realizou a compra.");
        ((ContractNetInitiatorAgent)myAgent).deleteComida(currentComida.getName());
        System.out.println("\nProximos pratos do Restaurante "+myAgent.getName());
        for(Comida a : ((ContractNetInitiatorAgent) myAgent).invetory){
            System.out.println("Prato " + a.getName() + " Valor R$ " + a.getPrice());
        }
        System.out.println();
    }

}
