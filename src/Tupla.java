public class Tupla {
    public Integer first;
    public Integer second;
    public Integer type; // 1 si es iniciadora-calculadora
                         // 2 si es de tipo calculadora-calculadora
                         // 3 si es de tipo calculadora-ejecutora

    public Tupla (Integer first, Integer second){
        this.first = first;
        this.second = second;
    }

    @Override
    public String toString() {
        return first + " , " + second;
    }

    public boolean containsNode(int node) {
        if(first + 1 == node || second + 1== node) {
            return true;
        }
        return false;
    }

}
