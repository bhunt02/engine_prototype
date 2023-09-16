package classes.data_types;

public class Pair<V0, V1> {

    private V0 Value0;
    private V1 Value1;
    public Pair(V0 Value0, V1 Value1) {
        this.Value0 = Value0;
        this.Value1 = Value1;
    }
    public V0 getValue0() {
        return Value0;
    }
    public V1 getValue1() {
        return Value1;
    }
    public void setValue0(V0 value0) {
        Value0 = value0;
    }
    public void setValue1(V1 value1) {
        Value1 = value1;
    }

    public boolean equals(Pair<V0,V1> other) {
        return ((other.Value0 == this.Value0 && other.Value1 == this.Value1) || (other.Value0 == this.Value1 && other.Value1 == this.Value0));
    }
}
