package kroonprins.mocker.util;

public class Random {

    public long randomLong(long minimum, long maximum) {
        return minimum + (long) (Math.random() * (maximum - minimum));
    }
}
