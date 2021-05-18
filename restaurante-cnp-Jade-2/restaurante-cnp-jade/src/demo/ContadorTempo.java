package demo;
 
public class ContadorTempo {
    
    public static long tempo_Inicial;    
    public static long tempo_Final;
         
    public static void comecar(){
    	tempo_Inicial = System.currentTimeMillis();  
    }
        
    public static void parar(){
    	tempo_Final = System.currentTimeMillis();  
        
        long dif = (tempo_Final - tempo_Inicial);          
       //status é um JLabel q tenho na minha aplicacao.
        System.out.printf("Tempo de execução: %02d segundos e %02d milisegundos", (dif/1000), dif%1000);        
    }
}