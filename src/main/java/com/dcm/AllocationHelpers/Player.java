package com.dcm.AllocationHelpers;

public class Player {

    // statics
    private static final double STANDARD_ELO = 25;
    private static final double STANDARD_SIGMA_SQUARE = (double) Math.pow(STANDARD_ELO / 3, 2);
    private static final double STANDARD_SIGMA = (double) Math.sqrt(STANDARD_SIGMA_SQUARE);
    public static final double PERFORMANCE_VARIANCE_SQUARE = (double) Math.pow(STANDARD_SIGMA_SQUARE / 2, 2);
    public static final double PERFORMANCE_VARIANCE = (double) Math.sqrt(PERFORMANCE_VARIANCE_SQUARE);
    public static final double DYNMAICS_VARIANCE_SQUARE = (double) Math.pow(STANDARD_ELO / 100, 2);
    public static final double DYNMAICS_VARIANCE = (double) Math.sqrt(DYNMAICS_VARIANCE_SQUARE);

    final int id;
    final String name;
    double mu;
    double sigma;

    boolean canSpeak;
    boolean canJudge;
    boolean coutldntLastTime;

    String nextTournament;
    int experience;

    boolean german;
    boolean english;

    boolean bp;
    boolean opd;

    boolean isAdmin;

    public boolean isAdmin() {
        return isAdmin;
    }

    public Player(int id, String name, double mu, double sigma, boolean canSpeak, boolean canJudge, boolean german, boolean english, boolean bp, boolean opd, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.mu = mu;
        this.sigma = sigma;
        this.canSpeak = canSpeak;
        this.canJudge = canJudge;
        this.german = german;
        this.english = english;
        this.bp = bp;
        this.opd = opd;
        this.isAdmin = isAdmin;
    }

    public Player(int id, String name, double mu, double sigma, boolean german, boolean english) {
        this(id, name, mu, sigma, true, false, german, english, false, true, true);
    }

    public Player(int id, String name, boolean german, boolean english) {
        this(id, name, STANDARD_ELO, STANDARD_SIGMA, german, english);
    }

    public Player(int id, String name) {
        this(id, name, STANDARD_ELO, STANDARD_SIGMA, true, true);
    }

    public Player(String name, boolean english, boolean german, boolean bp, boolean opd, boolean canSpeak, boolean canJudge, int experience, String nextTournament, boolean couldntLastTime) {
        this.name = name;
        this.id = -1; // Default ID, should be set later
        this.mu = STANDARD_ELO;
        this.sigma = STANDARD_SIGMA;
        this.canSpeak = canSpeak;
        this.canJudge = canJudge;
        this.german = german;
        this.english = english;
        this.experience = experience;
        this.nextTournament = nextTournament;
        this.coutldntLastTime = couldntLastTime;
        this.bp = bp;
        this.opd = opd;
        this.isAdmin = false; // Default value, should be set later if needed
    }

    public double lowerQuantile() {
        return mu - 3 * sigma;
    }

    public String getName() {
        return name;
    }
    public double getMu() {
        return mu;
    }
    public double getSigma() {
        return sigma;
    }
    public boolean canSpeak() {
        return canSpeak;
    }
    public boolean canJudge() {
        return canJudge;
    }
    public boolean isGerman() {
        return german;
    }
    public boolean isEnglish() {
        return english;
    }
    public boolean couldntLastTime() {
        return coutldntLastTime;
    }
    public String getNextTournament() {
        return nextTournament;
    }
    public int getExperience() {
        return experience;
    }
    public int getId() {
        return id;
    }
    public boolean isBp() {
        return bp;
    }
    public boolean isOpd() {
        return opd;
    }
    
}
