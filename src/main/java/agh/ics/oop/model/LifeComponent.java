package agh.ics.oop.model;

public class LifeComponent {
    private boolean isAlive = true;
    private final int dayOfBirth;
    private int deathDay;
    private int numOfLivedDays;

    public LifeComponent(int dayOfBirth) { this.dayOfBirth = dayOfBirth; }

    public boolean isAlive() { return isAlive; }
    public void die(int day) { isAlive = false; deathDay = day; }
    public void nextDay() { numOfLivedDays++; }
    public int getNumOfLivedDays() { return numOfLivedDays; }
    public int getDeathDay() { return deathDay; }
    public int getDayOfBirth() { return dayOfBirth; }
}
