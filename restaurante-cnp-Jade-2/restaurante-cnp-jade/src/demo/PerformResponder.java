package demo;

import jade.core.Agent;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.RefuseException;

import java.util.HashSet;
import java.util.Set;

public class PerformResponder extends ContractNetResponder {
    public PerformResponder(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException {
        Comida itemOffered = null;
        
        try {
            itemOffered = (Comida) cfp.getContentObject();
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
       
        if (comidaIsRelevant(itemOffered)) {        	
            if(((ContractNetResponderAgent) myAgent).availabilityMoney > 0.0){            	
                double proposal = evaluteComida(itemOffered);
                if(proposal > ((ContractNetResponderAgent) myAgent).availabilityMoney){
                    proposal = ((ContractNetResponderAgent) myAgent).availabilityMoney;                    
                }
                //System.out.println("CFP responder: "+cfp.hashCode());
                System.out.println("CFP: "+cfp.hashCode()+": Cliente " + myAgent.getLocalName() + " Propos {"+itemOffered.getName()+", R$ " + proposal + "} para "+cfp.getConversationId());
                ACLMessage propose = cfp.createReply();
                propose.setPerformative(ACLMessage.PROPOSE);
                propose.setContent(String.valueOf(proposal));                
                return propose;
            } else{
                ACLMessage refuse = cfp.createReply();
                refuse.setPerformative(ACLMessage.REFUSE);
                return refuse;
            }

        } else if (itemOffered.isDiscount()) {
            double proposal = ((itemOffered.getPrice()/2) + (((ContractNetResponderAgent) myAgent).availabilityMoney * 0.10));
            System.out.println("CFP: "+cfp.hashCode()+": Cliente " + myAgent.getLocalName() + " Propos R$ " + proposal+" para "+cfp.getConversationId());
            ACLMessage propose = cfp.createReply();
            propose.setPerformative(ACLMessage.PROPOSE);
            propose.setContent(String.valueOf(proposal));
            return propose;
        } else {
            System.out.println("CFP: "+cfp.hashCode()+": O Cliente " + myAgent.getLocalName() + ": recusou o "+cfp.getConversationId());
            throw new RefuseException("evaluation-failed");
        }
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        System.out.println("CFP: "+cfp.hashCode()+": Restaurante " +cfp.getConversationId()+ ": aceitou a proposta do "+myAgent.getLocalName());
        Comida contentObject;
        try {
            contentObject = (Comida) cfp.getContentObject();
            double proposal = Double.parseDouble(propose.getContent());
            ((ContractNetResponderAgent) myAgent).buyItem(contentObject,proposal);
        } catch (UnreadableException e) {
            e.printStackTrace();
        }
        ACLMessage inform = accept.createReply();
        inform.setPerformative(ACLMessage.INFORM);
        return inform;
    }

    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        System.out.println("CFP: "+cfp.hashCode()+": Cliente " + myAgent.getLocalName() + ": recusou a proposta do "+cfp.getConversationId());
    }

    private double evaluteComida(Comida itemOffered) {
    	
        int score = 0;
        for (Interest i : ((ContractNetResponderAgent) myAgent).Interests) {
            if (itemOffered.getDomain().contains(i.getValue())) {
                score += i.getRank();
            }
        }
        return itemOffered.getPrice() + (score * 0.01) * itemOffered.getPrice();
    }

    private boolean comidaIsRelevant(Comida itemOffered) { 	    	
        Set<String> res = new HashSet<String>();
        for (Interest i : ((ContractNetResponderAgent) myAgent).Interests) {
            res.add(i.getValue());
        }               
        for (String interest : itemOffered.getDomain()) {
            if ((res.contains(interest.toLowerCase()))) {
                return true;
            }
        }
        return false;
    }
}