/* Initial beliefs and rules */

all_proposals_received(CNPId,NP) :-              // NP = number of participants
     .count(propose(CNPId,_)[source(_)], NO) &   // number of proposes received
     .count(refuse(CNPId)[source(_)], NR) &      // number of refusals received
     NP = NO + NR.
	
moneyGanied(0).
priceCurrentComida(0).
comida(sushi).
comida(mexicana).
comida(burguer).
comida(fritas).
comida(pizza).
comida(massa).
comida(arabe).
comida(churrasco).
comida(camarao).
comida(tilapia).

/* Initial goals */

!register.
!setup.

/* Plans */

+!register <- .df_register(initiator).

+!setup <- 	
	.findall(Com, comida(Com), L);
	.print("Vendo: ", L);
	!setupComida(L).

+!setupComida([Head|Tail]) <- 
	?comida(Head);
	.random(R);
	-priceCurrentComida(_);
	+priceCurrentComida(math.floor((R*100)+20));
	?priceCurrentComida(Tot);
	.print("Restaurante vendendo ",Head," por no minimo R$ ", Tot, " reais");
	!cnp(math.floor((R*100)),Head);
	!setupComida(Tail).

	
+!setupComida(Com) <- 
	.print("Venda encerrada");
	?moneyGanied(Tot);
	.print("Valor recebido R$ ", Tot, " reais").
	
// start the CNP
+!cnp(Id,Comida)
   <- !call(Id,Comida,LP);
      !bid(Id,LP);
      !winner(Id,LO,WAg);
      !result(Id,LO,WAg);
	   .wait(6000);
	  !cleandata(Comida).

+!call(Id,Comida,LP)
   <- .print("Esperando clientes para pedir ",Comida,"...");
      .wait(2000);  // wait participants introduction
      .df_search("participant",LP);
      .print("Enviando CFP ",Id," para ",LP);
	  ?priceCurrentComida(Tot);
      .send(LP,tell,cfp(Id,Comida,Tot)).
	  
+!bid(Id,LP) // the deadline of the CNP is now + 4 seconds (or all proposals received)
   <- .wait(all_proposals_received(Id,.length(LP)), 4000, _).
   
+!winner(CNPId,LO,WAg)
   :  	.findall(offer(O,A),propose(CNPId,_,O)[source(A)],LO) 
   			& LO \== [] 
   <- ?priceCurrentComida(Tot);
      .print("As ofertas dos clientes foram ", LO," espero pelo menos R$ ",Tot, " reais");
      .max(LO,offer(WOf,WAg));
      .print("O ",WAg," venceu o pedido por R$ ",WOf, " reais");
	  +sell(WOf).
	  
+!winner(_,_,nowinner).

+!result(_,[],_).

+!result(CNPId,[offer(_,WAg)|T],WAg) 
   <- .send(WAg,tell,accept_proposal(CNPId));
      !result(CNPId,T,WAg).
	  
+!result(CNPId,[offer(_,LAg)|T],WAg) 
   <- .send(LAg,tell,reject_proposal(CNPId));
      !result(CNPId,T,WAg).
	  
+!cleandata(Comida) <-
	   ?comida(Comida);
	   -comida(Comida).

+sell(Price): moneyGanied(X) <-
	-moneyGanied(_);
	+moneyGanied(X+Price);
	?moneyGanied(Tot);
	.print("Saldo: R$ ", Tot, " reais").
