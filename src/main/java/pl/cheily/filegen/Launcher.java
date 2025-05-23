package pl.cheily.filegen;

public class Launcher {
    public static void main(String[] args) {
        System.out.println("Launching ver " + Launcher.class.getPackage().getImplementationVersion());
        ScoreboardApplication.main(args);
    }
}
