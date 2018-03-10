package ic.lab.incrediblehome;

import org.json.JSONObject;

/**
 * Created by aic on 26/09/17.
 */

public interface AsyncResult
{
    void onResult(JSONObject object);
}