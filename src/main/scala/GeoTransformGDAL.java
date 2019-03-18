import org.gdal.ogr.*;
import org.gdal.gdal.gdal;
import org.gdal.gdalconst.gdalconstConstants;
import org.gdal.ogr.DataSource;
import org.gdal.osr.SpatialReference;
import org.gdal.osr.*;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;


// script to read netCDF and convert lat longs to point objects
// adds columns with reprojected coords

public class GeoTransformGDAL {

    public static void main(String[] args){
        gdal.AllRegister();

        String filename = "/home/livia/IdeaProjects/FilterNetCDF/exampleData/testIn.nc";
        Driver inDriver = ogr.GetDriverByName("NetCDF");
        DataSource inSource = inDriver.Open(filename, gdalconstConstants.GA_ReadOnly);


        Driver memDriver = ogr.GetDriverByName("Memory");
        DataSource source = memDriver.CreateDataSource("memData");
        DataSource tmp = memDriver.Open("memData",1);
        Layer layer= source.CopyLayer(inSource.GetLayerByIndex(0),"data");

        // Lat/Lon
        SpatialReference s_srs = new SpatialReference();
        s_srs.ImportFromEPSG(4326);

        //Polarstereo
        SpatialReference t_srs = new SpatialReference();
        t_srs.ImportFromEPSG(3413);
        GeomFieldDefn srs = new GeomFieldDefn();
        srs.SetSpatialRef(t_srs);

        layer.CreateGeomField(srs,1);
        layer.CreateField(new FieldDefn("X", ogr.OFTReal));
        layer.CreateField(new FieldDefn("Y", ogr.OFTReal));
        layer.CreateField(new FieldDefn("CRS", ogr.OFTInteger));

        CoordinateTransformation transform = new CoordinateTransformation(s_srs, t_srs);


        for(int i = 0; i<layer.GetFeatureCount(); i++){
            Feature feature = layer.GetNextFeature();
            feature.GetFieldAsDouble("lon");
            String wkt = String.format("POINT(%f %f)", feature.GetFieldAsDouble("lat"), feature.GetFieldAsDouble("lon"));
            Geometry point = ogr.CreateGeometryFromWkt(wkt);
            point.Transform(transform);         //re-projection
            feature.SetGeometry(point);

            feature.SetField("X", feature.GetGeometryRef().GetX());
            feature.SetField("Y", feature.GetGeometryRef().GetY());
            feature.SetField("CRS", 3413);
            layer.SetFeature(feature);
        }

        System.out.println("Final file check");

        System.out.print("Fields:");
        FeatureDefn layerDefinition = layer.GetLayerDefn();
        for(int i = 0; i<layerDefinition.GetFieldCount(); i++){
            layerDefinition.GetFieldDefn(i).GetName();
        }

        System.out.print("Spatial Reference:");
        System.out.print(layer.GetSpatialRef());



        layer.ResetReading();

        // out driver
        Driver outDriver = ogr.GetDriverByName("netCDF");
        List<String> numbers = Arrays.asList("FORMAT=NC4C", "COMPRESS=DEFLATE", "ZLEVEL=9");
        Vector ofile_opts = new Vector(numbers);
        DataSource outSource = outDriver.CreateDataSource("/home/livia/IdeaProjects/FilterNetCDF/exampleData/enriched7.nc", ofile_opts);

        Vector options = new Vector(Arrays.asList("OVERWRITE=YES"));
        outSource.CopyLayer(layer,"data", options);
        outSource.SyncToDisk();


    }
}
