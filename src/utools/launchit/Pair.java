/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utools.launchit;

/**
 *
 * @author Maxim
 */
public class Pair<A, B> implements Comparable<Pair<A, B>>{

    private A first;
    private B second;
    
    private Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }
    
    public static <A, B> Pair<A, B> of (A first, B second) {
        return new Pair(first, second);
    }
    
    public A first() {
        return first;
    }

    public B second() {
        return second;
    }    
    
    @Override
    public int compareTo(Pair<A, B> o) {
        int cmp = compare(first, o.first);
        return cmp == 0 ? compare(second, o.second) : cmp;
    }
    
    private static int compare (Object o1, Object o2) {
        return o1 == null ? o2  == null ? 0 : -1 : o2 == null ? +1 : ((Comparable)o1).compareTo(o2);
    }
    
    @Override
    public int hashCode() {
        return 31 * hashCode(first) + hashCode(second);
    }
    
    private static int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }
    
    @Override
    public boolean equals (Object other) {
        if (!(other instanceof Pair)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        
        return equal(first,((Pair)other).first) &&
                equal(second,((Pair)other).second);
    }
    
    private boolean equal(Object o1, Object o2) {
        return o1 == null ? o2 == null : (o1 == o2 || o1.equals(o2));
    }
    
    @Override
    public String toString() {
        return "(" + first.toString() + ", " + second.toString() + ")";
    }

}
