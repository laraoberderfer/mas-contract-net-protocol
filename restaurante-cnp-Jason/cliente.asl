/* Initial beliefs and rules */

price(_Comida,X) :- 
	.random(R) & X = (10*R)+100.
	
/* Initial goals */

!register.
!setup.

/* Initial goals */

+!setup <- 	.random(R);
	        +avaibleMoney(math.floor((R*100)+1500));
	        ?avaibleMoney(Tot);
			.print("Tenho R$ ", Tot," reais").

+!register <- .df_register("participant");
              .df_subscribe("initiator").

@c1 +cfp(CNPId,Comida,Price)[source(A)]
   :  provider(A,"initiator") & 
      price(Comida,Offer) &
	  Offer > Price
   <- +proposal(CNPId,Comida, math.floor(Offer)); 
      .send(A,tell,propose(CNPId,Comida,math.floor(Offer))).

@r1 +accept_proposal(CNPId)
   :  proposal(CNPId,Comida,Offer)
   <- .print("Ganhei o CNP ", CNPId,". Minha proposta foi de R$ '",Offer,"' reais para ", Comida,"!");
   	  +bought(Comida,Offer).

@r2 +reject_proposal(CNPId)
   <- .print("Perdi o CNP ",CNPId, ".");
      -proposal(CNPId,_,_). 

//solucao para o problema do avaibleMoney () concorrente
@b[atomic]
+bought(Comida, Price): avaibleMoney(X) <-
	-avaibleMoney(_);  //programação concorrente
	+avaibleMoney(X-Price); 
	+comida(Comida);
	?avaibleMoney(TotSpent);
	.print("Comprei ", Comida, " por R$ ", Price, " reais. Meu saldo ficou R$ ", TotSpent," reais").
