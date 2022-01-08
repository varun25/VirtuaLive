package reverseminds.virtualive.login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import reverseminds.virtualive.login.models.ServerRequest;
import reverseminds.virtualive.login.models.ServerResponse;

/**
 * Created by Kushal I on 28/10/2017.
 */

public interface RequestInterface {

    @POST("html/virtualive/login/")
    Call<ServerResponse> operation(@Body ServerRequest request);

}