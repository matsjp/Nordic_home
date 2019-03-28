package viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.nordichome.ApplicationExtension;

public class JsonFilesViewModel extends ViewModel {
    private ApplicationExtension application = null;

    //This must always be set
    public void setApplication(ApplicationExtension application){
        this.application = application;
    }

    public LiveData<Boolean> getImportSuccessSignal(){
        return application.getScannerRepo().getImportSuccessSignal();
    }

    public LiveData<Boolean> getImportFailSignal(){
        return application.getScannerRepo().getImportFailSignal();
    }
}
