import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class Jogador extends Agent {

    private InfoPosition current_location;
    private String equipa;
    private String tipo;




    protected void setup() {
        super.setup();
        Object[] o = getArguments();
        this.equipa = (String) o[1];
        this.tipo = (String) o[2];


        this.addBehaviour(new Register_Player());
        this.addBehaviour(new Receiver());
    }

    protected void takeDown() {
        super.takeDown();
    }

    private class Register_Player extends OneShotBehaviour {
        public void action() {

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            if (equipa.equals("EquipaA")) sd.setType("EquipaA");
            else sd.setType("EquipaB");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);

                // If Lider is available!
                if (result.length > 0) {
                    Object[] p = getArguments();
                    current_location = new InfoPosition(myAgent.getAID(), (Position) p[0],tipo);

                    ACLMessage msg = new ACLMessage(ACLMessage.SUBSCRIBE);
                    msg.setContentObject(current_location);

                    for (DFAgentDescription dfAgentDescription : result) {
                        msg.addReceiver(dfAgentDescription.getName());
                    }

                    myAgent.send(msg);
                }
                // No Lider is available - kill the agent!
                else {
                    System.out.println(myAgent.getAID().getLocalName() + ": No Lider available. Agent offline");
                }

            } catch (IOException | FIPAException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    private class Receiver extends CyclicBehaviour {

        public void action() {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            if (equipa.equals("EquipaA")) sd.setType("EquipaA");
            else sd.setType("EquipaB");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);

                if (result.length > 0) {

                    ACLMessage msg = receive();

                    if (msg != null) {
                        if (msg.getPerformative() == ACLMessage.REQUEST) {// agente morreu
                            System.out.println( "MORREU : " + myAgent.getLocalName());
                            myAgent.doDelete();
                        }

                        if (msg.getPerformative() == ACLMessage.INFORM) { // movimento agente conforme estrategia
                            try {
                                Estrategia e = (Estrategia) msg.getContentObject();

                                if (e.getTipo() == 1) { // se receber uma posicao para onde ir
                                    Position p = e.getPosition();

                                    int x = current_location.getPosition().getX();
                                    int y = current_location.getPosition().getY();

                                    if (x < p.getX()) current_location.setPositionX(x + 1);
                                    else if (x > p.getX() ) current_location.setPositionX(x - 1);
                                    else if (y < p.getY() ) current_location.setPositionY(y + 1);
                                    else if (y > p.getY() ) current_location.setPositionY(y - 1);
                                    //System.out.println("POS JOG DEPOIS: " + current_location);

                                    ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
                                    mensagem.setContentObject(current_location);

                                    for (DFAgentDescription dfAgentDescription : result) {
                                        mensagem.addReceiver(dfAgentDescription.getName());
                                    }
                                    myAgent.send(mensagem);

                                     //current_location.getPosition().equals(p)
                                    ACLMessage men = new ACLMessage(ACLMessage.CONFIRM);
                                    for (DFAgentDescription dfAgentDescription : result) {
                                        men.addReceiver(dfAgentDescription.getName());
                                    }
                                    myAgent.send(men);
                                       System.out.println( myAgent.getLocalName() + ":" + current_location.getPosition() + '\n');

                                }

                                if (e.getTipo() == 0) { // se nao tem inimigos, faz movimento stutter

                                    Position nova = current_location.getPosition();

                                    int x = nova.getX();
                                    int y = nova.getY();

                                    if (x < 17 && (!e.getPos_Inimigos().contains(new Position(x+1,y)))) nova.setX(x + 1);
                                    else if (x > 17 && (!e.getPos_Inimigos().contains(new Position(x-1,y)))) nova.setX(x - 1);
                                    else if (y < 17 && (!e.getPos_Inimigos().contains(new Position(x,y+1)))) nova.setY(y + 1);
                                    else if (y > 17 && (!e.getPos_Inimigos().contains(new Position(x,y-1)))) nova.setY(y - 1);


                                    ACLMessage mensagem = new ACLMessage(ACLMessage.INFORM);
                                    mensagem.setContentObject(current_location);

                                    for (DFAgentDescription dfAgentDescription : result) {
                                        mensagem.addReceiver(dfAgentDescription.getName());
                                    }
                                    myAgent.send(mensagem);
                                    ACLMessage men = new ACLMessage(ACLMessage.CONFIRM);
                                    for (DFAgentDescription dfAgentDescription : result) {
                                        men.addReceiver(dfAgentDescription.getName());
                                    }
                                    myAgent.send(men);

                                    System.out.println( myAgent.getLocalName() + ":" + current_location.getPosition()+ '\n');
                                }
                            } catch (IOException | UnreadableException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }
    }
}
