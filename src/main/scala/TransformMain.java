// Use when running:
// LD_LIBRARY_PATH=/usr/lib/jni


/**
 * Main method calling the TransformToXY class
 */
public class TransformMain {


    public static void main(String[] args){
        // transform 1
        TransformToXY t2 = new TransformToXY(80.0, 0);
        System.out.println(t2.getX() + ", " + t2.getY() + ", EPSG: " +  t2.getEpsg());

        // transform 2
        TransformToXY t = new TransformToXY();
        t.transform(-80.0, 89.9);
        System.out.println(t.getX() + ", " + t.getY() + ", EPSG: " +  t.getEpsg());
    }

}
