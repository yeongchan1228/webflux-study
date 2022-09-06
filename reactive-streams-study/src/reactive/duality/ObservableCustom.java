package reactive.duality;

import java.util.Observable;
import java.util.Observer;

@SuppressWarnings("deprecation")
public class ObservableCustom {

    static class IntObservable extends Observable implements Runnable {

        @Override
        public void run() {
            for (int i = 1; i <= 10; i++) {
                setChanged();
                notifyObservers(i);
            }
        }
    }

    public static void main(String[] args) {
        Observer ob = (o, arg) -> System.out.println(arg);

        IntObservable intObservable = new IntObservable();
        intObservable.addObserver(ob);

        intObservable.run();
    }
}
