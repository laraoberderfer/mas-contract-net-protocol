package demo;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * This example shows how to implement the responder role in 
 * a FIPA-contract-net interaction protocol. In this case in particular 
 * we use a <code>ContractNetResponder</code> 
 * to participate into a negotiation where an initiator needs to assign
 * a task to an agent among a set of candidates.
 * @author Giovanni Caire - TILAB
 * 
 * -agents responder1:demo.ContractNetResponderAgent -gui
 * 
 * atualizado por Lara Popov Zambiasi Bazzi Oberderfer
 * em Abril de 2021
 */
public class ContractNetResponderAgent extends Agent {
    public List<Comida> currentItems = new LinkedList();
    public double availabilityMoney = 0.0;
    public List<Interest> Interests = new LinkedList<>();
    public int cfp_valor;

    protected void setup() {
        buildAgents();
        System.out.println("Cliente: " + getLocalName()  + " Tenho R$ " + availabilityMoney
                + " reais. Interesses : " + printInterest() + " aguardando por CFP...");
        MessageTemplate templateResponder = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));
        cfp_valor = templateResponder.hashCode();
        //System.out.println("CFP responder: "+templateResponder.hashCode());      
        addBehaviour(new PerformResponder(this, templateResponder));
        //addBehaviour(new PerformResponderSaleEnded(this, 3000));
        addBehaviour(new PerformResponderSaleEnded());
    }

    //private class PerformResponderSaleEnded extends TickerBehaviour {
    private class PerformResponderSaleEnded extends CyclicBehaviour {
        //public PerformResponderSaleEnded(Agent a, long period) {
        //    super(a, period);
        //}

        @Override
        //protected void onTick() {
        public void action(){        	
            MessageTemplate templateSaleEnded = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage msg = myAgent.receive(templateSaleEnded);
            if (msg != null) {            	
				if(msg.getContent().equals("sale-ended")){
                    System.out.println("Cliente: " + myAgent.getLocalName() + " Valor total R$ " + availabilityMoney);
                    myAgent.doDelete();
                    System.out.println("Cliente : " + myAgent.getLocalName() + " Pratos: " + currentItems.size());
                }
            } else {
                block();
            }
        }
    }

    protected void buyItem(Comida a, double proposal){
        System.out.println("CFP: "+cfp_valor+" Cliente: " + getLocalName() + " ganhou o CNP. Proposta foi de R$ " + proposal);
        currentItems.add(a);
        availabilityMoney = availabilityMoney - proposal;
    }

    private void buildAgents() {        
        availabilityMoney = getRandomVar(400,2000);
        Interests.add(new Interest("sushi",1));
        Interests.add(new Interest("mexicana",2));
        Interests.add(new Interest("burguer",3));
        Interests.add(new Interest("fritas",4));
        Interests.add(new Interest("pizza",5));
        Interests.add(new Interest("massa",6));
        Interests.add(new Interest("arabe",7));
        Interests.add(new Interest("churrasco",8));
        Interests.add(new Interest("camarao",9));
        Interests.add(new Interest("tilapia",10));        
    }

    public int getRandomVar(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    private String printInterest() {
        String res = "";
        for(Interest i : Interests){
            res += i.getValue() + ",";
        }
        return res;
    }
}