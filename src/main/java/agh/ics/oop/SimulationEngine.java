package agh.ics.oop;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimulationEngine {
    private final List<Simulation> simulations;
    private final List<Thread> threads;
    private final ExecutorService executorService;

    public SimulationEngine(List<Simulation> simulations, List<Thread> threads) {
        this.simulations = simulations;
        this.threads = threads;
        this.executorService = Executors.newFixedThreadPool(5);
    }

    public void runAsyncInThreadPool() throws InterruptedException {
        for (Simulation simulation : simulations){
            executorService.submit(simulation);
        }
        awaitSimulationsEnd();
    }

    public void awaitSimulationsEnd() throws InterruptedException {
        for (Thread thread : threads) {
            thread.join();
        }

        executorService.shutdown();
        if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }
    }

}
