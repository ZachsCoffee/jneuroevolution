package networks.interfaces;

public interface PartialNetwork extends Network {
    void setWeightStatus(int index, boolean status);

    boolean getWeightStatus(int index);

    void reverseWeightStatus(int index);
}
