package app.android.scc331.setadmin.REST.DataObjects;

public class HomeData {

    public String routerTotal;
    public String routerOnline;
    public String routerCliamed;
    public String routerUnclaimed;

    public String sensorTotal;
    public String sensorOnline;
    public String sensorClaimed;
    public String sensorUnclaimed;

    public String userTotal;

    public static class HomeDataBuilder{

        public int routerTotal;
        public int routerOnline;
        public int routerCliamed;
        public int routerUnclaimed;

        public int sensorTotal;
        public int sensorOnline;
        public int sensorClaimed;
        public int sensorUnclaimed;

        public int userTotal;

        public HomeDataBuilder(){

        }

        public HomeDataBuilder setRouterInfo(int total, int online, int claimed, int unclaimed){
            this.routerTotal = total;
            this.routerOnline = online;
            this.routerCliamed = claimed;
            this.routerUnclaimed = unclaimed;
            return this;
        }

        public HomeDataBuilder setSensorInfo(int total, int online, int claimed, int unclaimed){
            this.sensorTotal = total;
            this.sensorOnline = online;
            this.sensorClaimed = claimed;
            this.sensorUnclaimed = unclaimed;
            return this;
        }

        public HomeDataBuilder setUserInto(int total){
            this.userTotal = total;
            return this;
        }

        public HomeData create(){
            return new HomeData(routerTotal, routerOnline, routerCliamed, routerUnclaimed,
                    sensorTotal, sensorOnline, sensorClaimed, sensorUnclaimed, userTotal);
        }

    }

    private HomeData(int routerTotal, int routerOnline, int routerCliamed, int routerUnclaimed,
                    int sensorTotal, int sensorOnline, int sensorClaimed, int sensorUnclaimed,
                    int userTotal){
        this.routerTotal = String.valueOf(routerTotal);
        this.routerOnline = String.valueOf(routerOnline);
        this.routerCliamed = String.valueOf(routerCliamed);
        this.routerUnclaimed = String.valueOf(routerUnclaimed);
        this.sensorOnline = String.valueOf(sensorOnline);
        this.sensorClaimed = String.valueOf(sensorClaimed);
        this.sensorUnclaimed = String.valueOf(sensorUnclaimed);
        this.sensorTotal = String.valueOf(sensorTotal);
        this.userTotal = String.valueOf(userTotal);

    }

}
