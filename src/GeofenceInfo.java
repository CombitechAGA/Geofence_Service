/**
 * Created by Fredrik on 2015-08-06.
 */
public class GeofenceInfo {


    private int radius;
    private double currentLat = Double.MAX_VALUE;
    private double currentLong = Double.MAX_VALUE;
    private double homeLat;
    private double homeLong;
    private boolean currentlyError = false;


    public GeofenceInfo(int radius,double homeLat,double homeLong ){
        this.homeLat=homeLat;
        this.homeLong=homeLong;
        this.radius=radius;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public void setHomeLong(double homeLong) {
        this.homeLong = homeLong;
    }

    public void setHomeLat(double homeLat) {
        this.homeLat = homeLat;
    }


    public double distanceFromHome(double homeLat, double homeLong,double currentLat,double currentLong) {
        int R = 6371;
        double a =
                0.5 - Math.cos((currentLat - homeLat) * Math.PI / 180)/2 +
                        Math.cos(homeLat * Math.PI / 180) * Math.cos(currentLat * Math.PI / 180) *
                                (1 - Math.cos((currentLong - homeLong) * Math.PI / 180))/2;

        //för att få km
        return 1000 * R * 2 * Math.asin(Math.sqrt(a));
    }

    public boolean checkForPointOfNoReturn() {
        if(currentLat !=Double.MAX_VALUE && currentLong!=Double.MAX_VALUE && radius!=0){
            double distanceFromHome = distanceFromHome(homeLat,homeLong,currentLat,currentLong);
            System.out.println("Distance From home:"+distanceFromHome);
            if(distanceFromHome>radius){
                if(!currentlyError){
                    currentlyError = true;
                    return true;
                }
            }
            else{
                currentlyError=false;
            }
        }
        return false;

    }

}
