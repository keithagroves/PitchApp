package processing.test.radial_solfege_app;

import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import processing.core.PApplet;

public class AndroidPermissionHelper {
    int maVersion = Build.VERSION.SDK_INT;
    Activity act;

    void androidPermissions( PApplet app) {
        //Android stuff
        act = app.getActivity();
        this.act = act;
        // PermissionRequestor re;
        if (maVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {//verifier API
            if (!permissionsDejaAccordees()) {//ne pas redemander si la permission a déjà été accordée
                demandePermissionParticuliere();//sinon, demander....
            }


        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("permissions accordées");////now you can open your microphone

                } else {
                    System.out.println("permissions not granted");
                }
                break;
            default:
                if (maVersion > Build.VERSION_CODES.LOLLIPOP_MR1)
                    act.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean permissionsDejaAccordees() {
        if (maVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int result = act.checkSelfPermission(Manifest.permission.RECORD_AUDIO);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }


    private void demandePermissionParticuliere() {
        if (maVersion > Build.VERSION_CODES.LOLLIPOP_MR1) //verifier API

            act.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 101);//+WRITE_EXTERNAL_STORAGE????
    }


}
