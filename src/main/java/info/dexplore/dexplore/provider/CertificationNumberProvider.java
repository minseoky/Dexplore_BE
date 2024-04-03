package info.dexplore.dexplore.provider;


public class CertificationNumberProvider {

    public static String generateNumber() {

        String number = "";
        for(int i = 0 ; i < 6 ; i++)
            number += (int) (Math.random() * 10);


        return number;
    }

}
