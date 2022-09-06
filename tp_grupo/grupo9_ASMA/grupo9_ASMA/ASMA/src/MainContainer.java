import jade.wrapper.ContainerController;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;


public class MainContainer {

    Runtime rt;
    ContainerController container;

    public ContainerController initContainerInPlatform(String host, String port, String containerName) {
        // Get the JADE runtime interface (singleton)
        this.rt = Runtime.instance();

        // Create a Profile, where the launch arguments are stored
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, containerName);
        profile.setParameter(Profile.MAIN_HOST, host);
        profile.setParameter(Profile.MAIN_PORT, port);
        // create a non-main agent container
        return rt.createAgentContainer(profile);
    }

    public void initMainContainerInPlatform(String host, String port, String containerName) {

        // Get the JADE runtime interface (singleton)
        this.rt = Runtime.instance();

        // Create a Profile, where the launch arguments are stored
        Profile prof = new ProfileImpl();
        prof.setParameter(Profile.CONTAINER_NAME, containerName);
        prof.setParameter(Profile.MAIN_HOST, host);
        prof.setParameter(Profile.MAIN_PORT, port);
        prof.setParameter(Profile.MAIN, "true");
        prof.setParameter(Profile.GUI, "true");

        // create a main agent container
        this.container = rt.createMainContainer(prof);
        rt.setCloseVM(true);

    }

    public void startAgentInPlatform(String name, String classpath, Object[] p) {
        try {
            AgentController ac = container.createNewAgent(name, classpath, p);
            ac.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        MainContainer a = new MainContainer();
        int max=35;
        int numbersNeeded = 20;
        if (max < numbersNeeded) {
            throw new IllegalArgumentException("Can't ask for more numbers than are available"); }
        Random rng = new Random(); // Ideally just create one instance globally

        Set<Integer> generated = new LinkedHashSet<Integer>();
        while (generated.size() < numbersNeeded) {
            Integer next = rng.nextInt(max) + 1;
            // As we're adding to a set, this will automatically do a containment check
            generated.add(next); }

        try {
            Iterator<Integer> posicoes = generated.iterator();


            a.initMainContainerInPlatform("localhost", "9888", "MainContainer");


            a.startAgentInPlatform("Arbitro", "Arbitro",new Object[]{});
            Thread.sleep(500);

            int x,y;

            // First Open Central Agent - Lider
            a.startAgentInPlatform("LiderA", "Lider",new Object[] {"EquipaA" } );
            a.startAgentInPlatform("LiderB" , "Lider",new Object[] {"EquipaB"} );
            // Provide some time for Agent to register in services

            Thread.sleep(500);





            // Start agents Jogadores!

            for (int l = 0; l<5 ; l++) {
                x = posicoes.next();
                y = posicoes.next();
                a.startAgentInPlatform("Jogador" + l, "Jogador", new Object[]{new Position(x, y), "EquipaA", "defensivo"});
            }

            for (int j=10; j<15;j++) {
                x = posicoes.next();
                y = posicoes.next();
                a.startAgentInPlatform("Jogador" + j, "Jogador", new Object[]{new Position(x, y), "EquipaB", "ofensivo"});
            }

            // Provide some time for Agents to register in services
            Thread.sleep(0);


        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

