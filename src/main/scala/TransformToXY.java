import org.gdal.osr.CoordinateTransformation;
import org.gdal.osr.SpatialReference;

public class TransformToXY {
    private double x;
    private double y;
    private int epsg;

    /**
     * Constructor with lat long input, transforms directly
     * @param lat
     * @param lon
     */
    public TransformToXY(double lat, double lon){
        this.transform(lat, lon);
    }

    /**
     * Constructor without transformation input
     */
    public TransformToXY(){
        // do nothing
    }


    /**
     * Method to transform lat and long coordinates
     * Different projection are used depending on where the points lays
     *
     * Projection used can be retrieved with getEpsg()
     * Projected coordinates can be retrieved with getX() and getY()
     *
     * @param lat
     * @param lon
     */
    public void transform(double lat, double lon){
        SpatialReference s_srs = new SpatialReference();
        // WGS84 (lat/long)
        s_srs.ImportFromEPSG(4326);

        SpatialReference t_srs = new SpatialReference();

        if (lat<=90 & lat>=55){
            // polar stereo north
            t_srs.ImportFromEPSG(3413);
        }else if(lat<=-55 & lat>=-90){
            // antarctic polar stereo
            t_srs.ImportFromEPSG(3031);
        }else{ //not on poles
            // add stuff here
        }

        CoordinateTransformation transform = new CoordinateTransformation(s_srs, t_srs);
        double [] transformed = transform.TransformPoint(lon, lat);

        this.x = transformed[0];
        this.y = transformed[1];
        this.epsg = Integer.parseInt(t_srs.GetAttrValue("AUTHORITY", 1));

    }

    /**
     * Get projected x coordinate
     * @return x
     */
    public double getX() {
        return x;
    }

    /**
     * Get projected y coordinate
     * @return y
     */
    public double getY() {
        return y;
    }

    /**
     * Get projection used for transformation
     * @return EPSG
     */
    public int getEpsg() {
        return epsg;
    }
}
