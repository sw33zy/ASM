import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.IOException;
import java.util.*;



public class Lider extends Agent {

    private final ArrayList<InfoPosition> player_position = new ArrayList<>();
    private int nrJogadores;
    Map<AID, InfoInimigo> info = new HashMap<>();
    private int i;

    int max =0;
    int j;
    int k;




    protected void setup() {
        super.setup();
        i =0;
        j=0;
        k=0;

        Object[] obj = getArguments();
        if (obj[0].equals("EquipaA")) {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            ServiceDescription sd = new ServiceDescription();
            sd.setType("EquipaA");
            sd.setName(getLocalName());
            dfd.addServices(sd);
            nrJogadores=5;
            try {
                DFService.register(this, dfd);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        } else {
            DFAgentDescription dfd2 = new DFAgentDescription();
            dfd2.setName(getAID());
            ServiceDescription sd2 = new ServiceDescription();
            sd2.setType("EquipaB");
            sd2.setName(getLocalName());
            dfd2.addServices(sd2);
            nrJogadores=5;
            try {
                DFService.register(this, dfd2);
            } catch (FIPAException e) {
                e.printStackTrace();
            }
        }

        this.addBehaviour(new Receiver());
    }

    protected void takeDown() {

        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        super.takeDown();
    }



    private class Receiver extends CyclicBehaviour {

        public void action() {
            ACLMessage msg = receive();
            Script script = new Script();

            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("Arbitro");
            template.addServices(sd);

            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);

                if (result.length > 0) {
                    if (msg != null) {
                        if (msg.getPerformative() == ACLMessage.SUBSCRIBE) { // regista jogadores

                            try {

                                InfoPosition content = (InfoPosition) msg.getContentObject();
                                player_position.add(content);

                                System.out.println(myAgent.getAID().getLocalName() + ": " + msg.getSender().getLocalName()
                                        + " registered, " + content.getPosition() + "!");



                                if(player_position.size()==nrJogadores){
                                    ACLMessage msg1 = new ACLMessage(ACLMessage.INFORM);
                                    InfoEquipa e = new InfoEquipa(myAgent.getAID(), player_position);
                                    msg1.setContentObject(e);

                                    for (DFAgentDescription dfAgentDescription : result) {
                                        msg1.addReceiver(dfAgentDescription.getName());
                                    }

                                    myAgent.send(msg1);
                                    System.out.println("\n\n");
                                }

                            } catch (UnreadableException | IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        if ((msg.getPerformative() == ACLMessage.INFORM) && (!msg.getSender().getLocalName().equals("Arbitro"))) {
                            //update das posicoes dos jogadores, e envia esse update ao arbitro

                            try {
                                InfoPosition content = (InfoPosition) msg.getContentObject();

                                for (InfoPosition i : player_position) {
                                    int index = player_position.indexOf(i);
                                    if (i.getAgent().toString().equals(content.getAgent().toString()))
                                        player_position.set(index, content);
                                }

                                ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
                                InfoEquipa e = new InfoEquipa(myAgent.getAID(), player_position);
                                msg2.setContentObject(e);

                                for (DFAgentDescription dfAgentDescription : result) {
                                    msg2.addReceiver(dfAgentDescription.getName());
                                }

                                myAgent.send(msg2);



                            } catch (UnreadableException | IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                        if ((msg.getPerformative() == ACLMessage.INFORM) &&(msg.getSender().getLocalName().equals("Arbitro")) && nrJogadores>0) {
                            InfoInimigo melhor = new InfoInimigo();
                            ArrayList<Position> pos_Inimigos = new ArrayList<>();

                                try {
                                    ArrayList<InfoInimigo> content = (ArrayList<InfoInimigo>) msg.getContentObject();
                                    for (InfoInimigo infoI : content){
                                        for(InfoPosition pos : player_position){
                                            if(pos.getAgent().equals(infoI.getAgent())){
                                                info.put(infoI.getAgent(), infoI);
                                                for(Position p : infoI.getMapPosInimigos().keySet()){
                                                    if (!pos_Inimigos.contains(p))pos_Inimigos.add(p);
                                                }
                                            }
                                        }
                                    }
                                    System.out.println("InfoValues: " + info);


                                } catch (UnreadableException e) {
                                    e.printStackTrace();
                                }


                            if (info.size()==nrJogadores && nrJogadores>0) { //ja temos todos as infos dos jogadores, calculado o agente com mais inimigos
                                for (AID a : info.keySet()) {
                                    if (info.get(a).getMapPosInimigos().size() >= max) {
                                        // System.out.println("AQUI " + a.getLocalName() + ' ' + info.get(a).getMapPosInimigos());
                                        max = info.get(a).getMapPosInimigos().size();
                                        melhor = info.get(a);
                                    }
                                }
                                System.out.println(melhor);


                                try {
                                    if (max == 0) { // se nao tiver nenhum inimigo a vista, o lider ordena os jogadores fazer o movimento stutter
                                        for(InfoPosition p : player_position){
                                            Estrategia stutter = new Estrategia(0, p.getAgent(), null,pos_Inimigos);
                                            System.out.println("Estrategia1 para " + p.getAgent().getLocalName() + " " + stutter.toString());
                                            ACLMessage st = new ACLMessage(ACLMessage.INFORM);
                                            st.addReceiver(p.getAgent());
                                            st.setContentObject(stutter);
                                            myAgent.send(st);
                                        }
                                    }

                                    else { // se tiver 1 ou mais inimigos no seu campo de visao, entra aqui dentro

                                        for(InfoPosition p : player_position) pos_Inimigos.add(p.getPosition());
                                        Position inimigo ;
                                        //procura o inimigo com menor distancia e torna-o o alvo
                                        Double menorDist = Collections.min(melhor.getMapPosInimigos().values());
                                        inimigo = script.getKey(melhor.getMapPosInimigos(),menorDist);

                                        System.out.println( melhor.getAgent().getLocalName() + " ESCOLHEU O INIMIGO :" + inimigo);


                                        Position adjCima = new Position(inimigo.getX()-1, inimigo.getY());

                                        Position adjBaixo = new Position(inimigo.getX()+1, inimigo.getY());

                                        Position adjEsq = new Position(inimigo.getX(), inimigo.getY()-1);

                                        Position adjDir = new Position(inimigo.getX(), inimigo.getY()+1);


                                        double distC,distB,distE,distD,distR;

                                        AID jogadorC , jogadorB, jogadorE, jogadorD ,jogadorR;


                                        Map<AID,Double> listaDistC = new HashMap<>();
                                        Map<AID,Double> listaDistB = new HashMap<>();
                                        Map<AID,Double> listaDistE = new HashMap<>();
                                        Map<AID,Double> listaDistD = new HashMap<>();


                                        ACLMessage mJC = new ACLMessage(ACLMessage.INFORM);
                                        ACLMessage mJB = new ACLMessage(ACLMessage.INFORM);
                                        ACLMessage mJD = new ACLMessage(ACLMessage.INFORM);
                                        ACLMessage mJE = new ACLMessage(ACLMessage.INFORM);
                                        ACLMessage mJF = new ACLMessage(ACLMessage.INFORM);
                                        ACLMessage mJR = new ACLMessage(ACLMessage.INFORM);

                                        for (InfoPosition p: player_position){ //calcula distancias de cada jogador as posições adjacentes

                                            distC = script.calculaDistancia(adjCima,p.getPosition());
                                            listaDistC.put(p.getAgent() ,distC);

                                            distB = script.calculaDistancia(adjBaixo,p.getPosition());
                                            listaDistB.put(p.getAgent(),distB);

                                            distE = script.calculaDistancia(adjEsq,p.getPosition());
                                            listaDistE.put(p.getAgent(),distE);

                                            distD = script.calculaDistancia(adjDir,p.getPosition());
                                            listaDistD.put(p.getAgent(),distD);

                                        }

                                        InfoPosition best = null;
                                        String type = null;
                                        for(InfoPosition i : player_position){
                                            if(i.getAgent().equals(melhor.getAgent())) {
                                                type = i.getTipo();
                                                best = i;
                                            }
                                        }

                                        if(best!=null){
                                            Map<Position, Double> ajuda = new HashMap<>();
                                            ajuda = script.campoVisao(best,player_position);

                                           // System.out.println("Ajuda " + ajuda);
                                            if(max == 1 && type!= null && type.equals("defensivo") && ajuda.size()==1) {
                                                //caso tenha apenas 1 inimigo e for defensivo ele foge

                                                listaDistD.remove(melhor.getAgent());
                                                listaDistC.remove(melhor.getAgent());
                                                listaDistB.remove(melhor.getAgent());
                                                listaDistE.remove(melhor.getAgent());
                                                try{
                                                    mJF.addReceiver(melhor.getAgent());
                                                    Estrategia f = new Estrategia(0, melhor.getAgent(), null, pos_Inimigos);
                                                    mJF.setContentObject(f);
                                                    myAgent.send(mJF);
                                                }catch ( IOException e ){
                                                    e.printStackTrace();
                                                }
                                            }

//1
                                           if (((max >= 2) && (ajuda.size() <= max-1)) || (( max>=2) && ((ajuda.size()) == max) && type != null && (type.equals("defensivo")))) {
                                               // caso esteja em desvantagem numerica e ou ser defensivo ele foge
                                                listaDistD.remove(melhor.getAgent());
                                                listaDistC.remove(melhor.getAgent());
                                                listaDistB.remove(melhor.getAgent());
                                                listaDistE.remove(melhor.getAgent());
                                                try {
                                                    mJF.addReceiver(melhor.getAgent());
                                                    Estrategia f = new Estrategia(0, melhor.getAgent(), null, pos_Inimigos);
                                                    mJF.setContentObject(f);
                                                    myAgent.send(mJF);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }

                                            //se for encurrlado no canto superior esq
                                            if ((inimigo.getX() == 1) && (inimigo.getY() == 1)) {

                                                distB = Collections.min(listaDistB.values());
                                                jogadorB = script.getKey(listaDistB, distB);
                                                mJB.addReceiver(jogadorB);
                                                Estrategia b = new Estrategia(1, jogadorB, adjBaixo, pos_Inimigos);
                                                mJB.setContentObject(b);
                                                myAgent.send(mJB);

                                                listaDistD.remove(jogadorB);


                                                if (listaDistD.size() > 0) {
                                                    distD = Collections.min(listaDistD.values());
                                                    jogadorD = script.getKey(listaDistD, distD);

                                                    mJD.addReceiver(jogadorD);
                                                    Estrategia d = new Estrategia(1, jogadorD, adjDir, pos_Inimigos);
                                                    mJD.setContentObject(d);
                                                    myAgent.send(mJD);
                                                    listaDistD.remove(jogadorD);
                                                }

                                                if (listaDistD.size() > 0) {
                                                    for (AID j : listaDistD.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }
                                            }


                                            //se for encurrulado no canto superior dir
                                            if ((inimigo.getX() == 1) && (inimigo.getY() == 35)) {
                                                distB = Collections.min(listaDistB.values());
                                                jogadorB = script.getKey(listaDistB, distB);

                                                mJB.addReceiver(jogadorB);
                                                Estrategia b = new Estrategia(1, jogadorB, adjBaixo, pos_Inimigos);
                                                mJB.setContentObject(b);
                                                myAgent.send(mJB);

                                                listaDistE.remove(jogadorB);

                                                if (listaDistE.size() > 0) {
                                                    distE = Collections.min(listaDistE.values());
                                                    jogadorE = script.getKey(listaDistE, distE);

                                                    mJE.addReceiver(jogadorE);
                                                    Estrategia e = new Estrategia(1, jogadorE, adjEsq, pos_Inimigos);
                                                    mJE.setContentObject(e);
                                                    myAgent.send(mJE);
                                                    listaDistE.remove(jogadorE);
                                                }

                                                if (listaDistE.size() > 0) {
                                                    for (AID j : listaDistE.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }

                                            }

                                            //se for encurrulado no canto inferior esq
                                            if ((inimigo.getX() == 35) && (inimigo.getY() == 1)) {
                                                distC = Collections.min(listaDistC.values());
                                                jogadorC = script.getKey(listaDistC, distC);

                                                mJC.addReceiver(jogadorC);
                                                Estrategia c = new Estrategia(1, jogadorC, adjCima, pos_Inimigos);
                                                mJC.setContentObject(c);
                                                myAgent.send(mJC);

                                                listaDistD.remove(jogadorC);

                                                if (listaDistD.size() > 0) {
                                                    distD = Collections.min(listaDistD.values());
                                                    jogadorD = script.getKey(listaDistD, distD);

                                                    mJD.addReceiver(jogadorD);
                                                    Estrategia d = new Estrategia(1, jogadorD, adjDir, pos_Inimigos);
                                                    mJD.setContentObject(d);
                                                    myAgent.send(mJD);
                                                    listaDistD.remove(jogadorD);
                                                }

                                                if (listaDistD.size() > 0) {
                                                    for (AID j : listaDistD.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }
                                            }

                                            //se for encurralado no canto inf dir
                                            if ((inimigo.getX() == 35) && (inimigo.getY() == 35)) {
                                                distC = Collections.min(listaDistC.values());
                                                jogadorC = script.getKey(listaDistC, distC);


                                                mJC.addReceiver(jogadorC);
                                                Estrategia c = new Estrategia(1, jogadorC, adjCima, pos_Inimigos);
                                                mJC.setContentObject(c);
                                                myAgent.send(mJC);

                                                listaDistE.remove(jogadorC);

                                                if (listaDistE.size() > 0) {
                                                    distE = Collections.min(listaDistE.values());
                                                    jogadorE = script.getKey(listaDistE, distE);

                                                    mJE.addReceiver(jogadorE);
                                                    Estrategia e = new Estrategia(1, jogadorE, adjEsq, pos_Inimigos);
                                                    mJE.setContentObject(e);
                                                    myAgent.send(mJE);
                                                    listaDistE.remove(jogadorE);
                                                }

                                                if (listaDistE.size() > 0) {
                                                    for (AID j : listaDistE.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }
                                            }

                                            //encurralado na parede de cima
                                            if ((inimigo.getX() == 1) && (inimigo.getY() != 1) && (inimigo.getY() != 35)) {
                                                distB = Collections.min(listaDistB.values());
                                                jogadorB = script.getKey(listaDistB, distB);
                                                mJB.addReceiver(jogadorB);
                                                Estrategia b = new Estrategia(1, jogadorB, adjBaixo, pos_Inimigos);
                                                mJB.setContentObject(b);
                                                myAgent.send(mJB);

                                                listaDistE.remove(jogadorB);
                                                listaDistD.remove(jogadorB);


                                                if (listaDistE.size() > 0) {
                                                    distE = Collections.min(listaDistE.values());
                                                    jogadorE = script.getKey(listaDistE, distE);
                                                    mJE.addReceiver(jogadorE);
                                                    Estrategia e = new Estrategia(1, jogadorE, adjEsq, pos_Inimigos);
                                                    mJE.setContentObject(e);
                                                    myAgent.send(mJE);
                                                    listaDistD.remove(jogadorE);
                                                }

                                                if (listaDistD.size() > 0) {

                                                    distD = Collections.min(listaDistD.values());
                                                    jogadorD = script.getKey(listaDistD, distD);

                                                    mJD.addReceiver(jogadorD);
                                                    Estrategia d = new Estrategia(1, jogadorD, adjDir, pos_Inimigos);
                                                    mJD.setContentObject(d);
                                                    myAgent.send(mJD);
                                                    listaDistD.remove(jogadorD);
                                                }

                                                if (listaDistD.size() > 0) {
                                                    for (AID j : listaDistD.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }
                                            }




                                            if ((inimigo.getX() == 35) && (inimigo.getY() != 1) && (inimigo.getY() != 35)) {
                                                distC = Collections.min(listaDistC.values());
                                                jogadorC = script.getKey(listaDistC, distC);

                                                mJC.addReceiver(jogadorC);
                                                Estrategia c = new Estrategia(1, jogadorC, adjCima, pos_Inimigos);
                                                mJC.setContentObject(c);
                                                myAgent.send(mJC);

                                                listaDistE.remove(jogadorC);
                                                listaDistD.remove(jogadorC);


                                                if (listaDistE.size() > 0) {
                                                    distE = Collections.min(listaDistE.values());
                                                    jogadorE = script.getKey(listaDistE, distE);
                                                    mJE.addReceiver(jogadorE);
                                                    Estrategia e = new Estrategia(1, jogadorE, adjEsq, pos_Inimigos);
                                                    mJE.setContentObject(e);
                                                    myAgent.send(mJE);
                                                    listaDistD.remove(jogadorE);
                                                }


                                                if (listaDistD.size() > 0) {
                                                    distD = Collections.min(listaDistD.values());
                                                    jogadorD = script.getKey(listaDistD, distD);
                                                    mJD.addReceiver(jogadorD);
                                                    Estrategia d = new Estrategia(1, jogadorD, adjDir, pos_Inimigos);
                                                    mJD.setContentObject(d);
                                                    myAgent.send(mJD);
                                                    listaDistD.remove(jogadorD);
                                                }

                                                if (listaDistD.size() > 0) {
                                                    for (AID j : listaDistD.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }
                                            }


                                            if ((inimigo.getY() == 1) && (inimigo.getX() != 1) && (inimigo.getX() != 35)) {

                                                distB = Collections.min(listaDistB.values());
                                                jogadorB = script.getKey(listaDistB, distB);


                                                mJB.addReceiver(jogadorB);
                                                Estrategia b = new Estrategia(1, jogadorB, adjBaixo, pos_Inimigos);
                                                mJB.setContentObject(b);
                                                myAgent.send(mJB);

                                                listaDistC.remove(jogadorB);
                                                listaDistD.remove(jogadorB);


                                                if (listaDistC.size() > 0) {
                                                    distC = Collections.min(listaDistC.values());
                                                    jogadorC = script.getKey(listaDistC, distC);
                                                    mJC.addReceiver(jogadorC);
                                                    Estrategia c = new Estrategia(1, jogadorC, adjCima, pos_Inimigos);
                                                    mJC.setContentObject(c);
                                                    myAgent.send(mJC);
                                                    listaDistD.remove(jogadorC);
                                                }

                                                if (listaDistD.size() > 0) {
                                                    distD = Collections.min(listaDistD.values());
                                                    jogadorD = script.getKey(listaDistD, distD);

                                                    mJD.addReceiver(jogadorD);
                                                    Estrategia d = new Estrategia(1, jogadorD, adjDir, pos_Inimigos);
                                                    mJD.setContentObject(d);

                                                    myAgent.send(mJD);
                                                    listaDistD.remove(jogadorD);
                                                }

                                                if (listaDistD.size() > 0) {
                                                    for (AID j : listaDistD.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }
                                            }


                                            if ((inimigo.getY() == 35) && (inimigo.getX() != 1) && (inimigo.getX() != 35)) {
                                                distB = Collections.min(listaDistB.values());
                                                jogadorB = script.getKey(listaDistB, distB);

                                                mJB.addReceiver(jogadorB);
                                                Estrategia b = new Estrategia(1, jogadorB, adjBaixo, pos_Inimigos);
                                                mJB.setContentObject(b);
                                                myAgent.send(mJB);

                                                listaDistC.remove(jogadorB);
                                                listaDistE.remove(jogadorB);


                                                if (listaDistC.size() > 0) {
                                                    distC = Collections.min(listaDistC.values());
                                                    jogadorC = script.getKey(listaDistC, distC);

                                                    mJC.addReceiver(jogadorC);
                                                    Estrategia c = new Estrategia(1, jogadorC, adjCima, pos_Inimigos);
                                                    mJC.setContentObject(c);
                                                    myAgent.send(mJC);

                                                    listaDistE.remove(jogadorC);
                                                }


                                                if (listaDistE.size() > 0) {
                                                    distE = Collections.min(listaDistE.values());
                                                    jogadorE = script.getKey(listaDistE, distE);

                                                    mJE.addReceiver(jogadorE);
                                                    Estrategia e = new Estrategia(1, jogadorE, adjEsq, pos_Inimigos);
                                                    mJE.setContentObject(e);
                                                    myAgent.send(mJE);
                                                    listaDistE.remove(jogadorE);
                                                }

                                                if (listaDistE.size() > 0) {
                                                    for (AID j : listaDistE.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }
                                            }

//3
                                            if ((inimigo.getY() != 1) && (inimigo.getY() != 35) && (inimigo.getX() != 1) && (inimigo.getX() != 35)) {

                                                if(listaDistC.size()>1) {
                                                    distC = Collections.min(listaDistC.values());
                                                    jogadorC = script.getKey(listaDistC, distC);

                                                    mJC.addReceiver(jogadorC);
                                                    Estrategia c = new Estrategia(1, jogadorC, adjCima, pos_Inimigos);
                                                    mJC.setContentObject(c);
                                                    myAgent.send(mJC);

                                                    listaDistB.remove(jogadorC);
                                                    listaDistD.remove(jogadorC);
                                                    listaDistE.remove(jogadorC);
                                                }

                                                if (listaDistD.size() > 0) {
                                                    distD = Collections.min(listaDistD.values());
                                                    jogadorD = script.getKey(listaDistD, distD);

                                                    mJD.addReceiver(jogadorD);
                                                    Estrategia d = new Estrategia(1, jogadorD, adjDir, pos_Inimigos);
                                                    mJD.setContentObject(d);
                                                    myAgent.send(mJD);
                                                    listaDistB.remove(jogadorD);
                                                    listaDistE.remove(jogadorD);
                                                }


                                                if (listaDistB.size() > 0) {
                                                    distB = Collections.min(listaDistB.values());
                                                    jogadorB = script.getKey(listaDistB, distB);

                                                    mJB.addReceiver(jogadorB);
                                                    Estrategia b = new Estrategia(1, jogadorB, adjBaixo, pos_Inimigos);


                                                    mJB.setContentObject(b);
                                                    myAgent.send(mJB);
                                                    listaDistE.remove(jogadorB);
                                                }


                                                if (listaDistE.size() > 0) {
                                                    distE = Collections.min(listaDistE.values());
                                                    jogadorE = script.getKey(listaDistE, distE);

                                                    mJE.addReceiver(jogadorE);
                                                    Estrategia e = new Estrategia(1, jogadorE, adjEsq, pos_Inimigos);

                                                    mJE.setContentObject(e);
                                                    myAgent.send(mJE);
                                                    listaDistE.remove(jogadorE);
                                                }

//4
                                                //REFORÇO
                                                if (listaDistE.size() > 0) {
                                                    distR = Collections.min(listaDistE.values());
                                                    jogadorR = script.getKey(listaDistE, distR);

                                                    mJR.addReceiver(jogadorR);
                                                    Estrategia h = new Estrategia(1, jogadorR, adjEsq, pos_Inimigos);
                                                    mJR.setContentObject(h);
                                                    myAgent.send(mJR);
                                                    listaDistE.remove(jogadorR);
                                                }

                                                if (listaDistE.size() > 0) {
                                                    for (AID j : listaDistE.keySet()) {
                                                        Estrategia r = new Estrategia(0, j, null, pos_Inimigos);
                                                        ACLMessage m = new ACLMessage(ACLMessage.INFORM);
                                                        m.addReceiver(j);
                                                        m.setContentObject(r);
                                                        myAgent.send(m);
                                                    }
                                                }
                                            }
                                        max = 0;

                                    }
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                    }


                        if(msg.getPerformative()==ACLMessage.CONFIRM){
                            i++;
                            if(i==player_position.size()){
                                try{
                                    ACLMessage m = new ACLMessage(ACLMessage.CONFIRM);
                                    m.setContentObject(myAgent.getAID().getLocalName());


                                    for (DFAgentDescription dfAgentDescription : result) {
                                        m.addReceiver(dfAgentDescription.getName());
                                    }
                                    myAgent.send(m);
                                    i=0;
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        if(msg.getPerformative()==ACLMessage.AGREE) {
                            //retirar os inimigos que foram eliminados do mapa de inimigos

                            try {
                                InfoPosition po = (InfoPosition) msg.getContentObject();
                                Position posMorto = po.getPosition();
                                System.out.println("ENTROU AGREE para retirar o " + po.getAgent().getLocalName());

                                for(InfoInimigo ini : info.values() ){
                                    if(ini.getMapPosInimigos().containsKey(posMorto)) {
                                         ini.setMapPosInimigos(ini.removerInimigos(posMorto));
                                    }
                                }
                            }catch (UnreadableException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        if (msg.getPerformative() == ACLMessage.REQUEST) {
                            try {
                                InfoPosition content = (InfoPosition) msg.getContentObject();
                                for(InfoPosition p : player_position){
                                    if(p.equals(content)){
                                        player_position.remove(p);
                                        nrJogadores--;
                                        break;
                                    }
                                }

                                for(AID p : info.keySet()){
                                    if(p.equals(content.getAgent())){
                                        System.out.println("RETIREI " + p.getLocalName() + "do info");
                                        info.remove(p);
                                        break;
                                    }
                                }

                            } catch (UnreadableException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                }
            } catch (FIPAException e) {
                throw new RuntimeException(e);
            }
        }
    }
}


